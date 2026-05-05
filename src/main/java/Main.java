import java.time.LocalDate;
import java.util.*;

public class Main {

    // --- Services / singletons ---
    private static final StudentManager studentRepo = new StudentManager();
    private static final MenuManager menu = new MenuManager();
    private static final OrderProcessor orders = new OrderProcessor();
    private static final NotificationService notify = new NotificationService();
    private static final LoyaltyProgram loyalty = new LoyaltyProgram();
    private static Authentication auth;

    private static EmployeeRegister registrar;
    private static Admin admin;
    private static ReportManager reports;

    private static final Map<String, WalletPayment> wallets = new HashMap<>();
    private static final IPaymentProcessor pp = new PaymentProcessor();

    // Persist carts per student
    private static final Map<String, List<OrderLine>> carts = new HashMap<>();

    public static void main(String[] args) {
        bootstrap();
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n=== University Cafeteria ===");
                System.out.println("1) Student");
                System.out.println("2) Employee");
                System.out.println("3) Admin");
                System.out.println("0) Exit");
                System.out.print("Choose: ");
                switch (sc.nextLine().trim()) {
                    case "1" -> studentPortal(sc);
                    case "2" -> employeePortal(sc);
                    case "3" -> adminPortal(sc);
                    case "0" -> { System.out.println("Goodbye!"); return; }
                    default -> System.out.println("Invalid choice.");
                }
            }
        }
    }

    // ---------- Student flows ----------
    private static void studentPortal(Scanner sc) {
        System.out.println("\n== Student ==");
        System.out.println("1) Register");
        System.out.println("2) Login");
        System.out.print("Choose: ");
        switch (sc.nextLine().trim()) {
            case "1" -> studentRegister(sc);
            case "2" -> studentLogin(sc);
            default -> System.out.println("Invalid.");
        }
    }

    private static void studentRegister(Scanner sc) {
        System.out.print("Choose ID: "); String id = sc.nextLine().trim();
        System.out.print("Name: "); String name = sc.nextLine().trim();
        System.out.print("PIN: "); String pin = sc.nextLine().trim();
        if (studentRepo.register(new Student(id, name, pin))) {
            System.out.println("Registered. You can login now.");
        } else {
            System.out.println("ID already exists.");
        }
    }

    private static void studentLogin(Scanner sc) {
        System.out.print("ID: "); String id = sc.nextLine().trim();
        System.out.print("PIN: "); String pin = sc.nextLine().trim();
        var opt = auth.loginStudent(id, pin);
        if (opt.isEmpty()) { System.out.println("Login failed."); return; }
        Student s = opt.get();

        wallets.putIfAbsent(s.getId(), new WalletPayment(AppConfig.INITIAL_WALLET_BALANCE));
        carts.putIfAbsent(s.getId(), new ArrayList<>());
        studentHome(sc, s);
    }

    private static void studentHome(Scanner sc, Student s) {
        while (true) {
            System.out.println("\n== Student Home (" + s.getName() + ")");
            System.out.println("Points: " + s.getLoyaltyPoints() + "  |  Coffee credits: " + s.getFreeCoffeeCredits());
            System.out.println("1) Browse Menu");
            System.out.println("2) View Cart");
            System.out.println("3) Add Item to Cart");
            System.out.println("4) Checkout");
            System.out.println("5) Redeem Free Coffee (points → 1 credit)");
            System.out.println("6) Cancel / Clear Cart");
            System.out.println("7) Wallet balance");
            System.out.println("8) Notifications");
            System.out.println("0) Logout");
            System.out.print("Choose: ");
            switch (sc.nextLine().trim()) {
                case "1" -> menu.displayMenu();
                case "2" -> viewCart(s);
                case "3" -> addItemToCart(sc, s);
                case "4" -> checkout(sc, s);
                case "5" -> redeemFreeCoffeeFromHome(s);
                case "6" -> clearCart(s);
                case "7" -> {
                    WalletPayment w = wallets.get(s.getId());
                    System.out.printf("Wallet balance: %.2f EGP%n", (w == null ? 0.0 : w.getBalance()));
                }
                case "8" -> showNotifications(s);
                case "0" -> { return; }
                default -> System.out.println("Invalid.");
            }
        }
    }

    private static void viewCart(Student s) {
        var baseCart = carts.getOrDefault(s.getId(), List.of());
        System.out.println("\n== Cart ==");
        System.out.println("ID | Item  xQty  @ Unit  = Subtotal");
        System.out.println("------------------------------------------------");
        double total = 0.0;

        if (baseCart.isEmpty() && s.getFreeCoffeeCredits() <= 0) {
            System.out.println("(empty)");
            return;
        }

        // Print normal items with full details regardless of OrderLine.toString()
        for (OrderLine ol : baseCart) {
            MenuItem it = ol.getItem();
            int qty = ol.getQuantity();
            double unit = ol.getUnitPrice().asDouble();
            double sub = ol.getSubtotal().asDouble();
            System.out.printf("%s | %s  x%d  @ %.2f EGP  = %.2f EGP%n",
                    it.getId(), it.getName(), qty, unit, sub);
            total += sub;
        }

        // Visually include ONE free coffee if the student has a credit
        if (s.getFreeCoffeeCredits() > 0) {
            MenuItem free = makeFreeCoffeeItem();
            System.out.printf("%s | %s  x1  @ %.2f EGP  = %.2f EGP  <-- Free Coffee (credit)%n",
                    free.getId(), free.getName(), 0.0, 0.0);
            // price is 0.00 so total unchanged
        }

        System.out.printf("TOTAL: %.2f EGP%n", total);
    }

    private static void clearCart(Student s) {
        carts.getOrDefault(s.getId(), new ArrayList<>()).clear();
        System.out.println("Cart cleared.");
    }

    private static void addItemToCart(Scanner sc, Student s) {
        var cart = carts.computeIfAbsent(s.getId(), k -> new ArrayList<>());
        menu.displayMenu();
        System.out.print("Enter Menu Item ID: ");
        String id = sc.nextLine().trim();
        var mi = menu.findById(id);
        if (mi.isEmpty()) { System.out.println("Not found."); return; }
        System.out.print("Qty: ");
        int qty = parseIntSafe(sc.nextLine(), 1);
        if (qty <= 0) qty = 1;
        cart.add(new OrderLine(mi.get(), qty));
        System.out.println("Added.");
    }

    /** Redeem one Free Coffee credit from the Student home (deducts points immediately). */
    private static void redeemFreeCoffeeFromHome(Student s) {
        var rr = loyalty.redeemFreeCoffeeCredit(s);
        if (rr.applied) {
            System.out.printf("Redeemed Free Coffee credit: -%d pts (%.2f EGP). You now have %d credits.%n",
                    rr.pointsUsed, rr.discount, s.getFreeCoffeeCredits());
        } else {
            int need = loyalty.freeCoffeePoints(loyalty.getDefaultCoffeePrice());
            System.out.println("Not enough points. Need " + need + " pts.");
        }
    }

    private static void checkout(Scanner sc, Student s) {
        var baseCart = carts.getOrDefault(s.getId(), new ArrayList<>());
        if (baseCart.isEmpty() && s.getFreeCoffeeCredits() <= 0) {
            System.out.println("Cart is empty.");
            return;
        }

        // Build the order lines: cart items + ONE free coffee line (0 EGP) if a credit exists
        List<OrderLine> lines = new ArrayList<>(baseCart);
        boolean willUseCredit = false;
        if (s.getFreeCoffeeCredits() > 0) {
            lines.add(new OrderLine(makeFreeCoffeeItem(), 1));
            willUseCredit = true;
        }

        // Compute subtotal from lines (free coffee contributes 0.00)
        double subtotal = 0.0;
        for (OrderLine ol : lines) subtotal += ol.getSubtotal().asDouble();

        // Optional variable points redemption (10 pts = 1 EGP)
        System.out.println("\nYou have " + s.getLoyaltyPoints() + " pts (~ " +
                String.format("%.2f", loyalty.pointsToEGP(s.getLoyaltyPoints())) + " EGP).");
        System.out.print("Enter points to redeem (or 0): ");
        int variablePoints = Math.max(0, parseIntSafe(sc.nextLine(), 0));
        if (variablePoints > s.getLoyaltyPoints()) {
            System.out.println("Too many; using max.");
            variablePoints = s.getLoyaltyPoints();
        }

        // Preview due – NO 'Promo' here, only points discount
        PaymentBreakdown preview = pp.preview(subtotal, variablePoints, loyalty);
        System.out.printf("Items total: %.2f | Free Coffee lines: %d | Points discount: -%.2f | Total due: %.2f EGP%n",
                subtotal, (willUseCredit ? 1 : 0), preview.discountApplied, preview.totalDue);

        System.out.println("\nPay with: 1) Cash  2) Wallet");
        String pay = sc.nextLine().trim();

        // Create order; consume the credit NOW because this order is being placed
        Order o = orders.createOrder(s.getId(), lines);
        if (willUseCredit) s.consumeFreeCoffeeCredit();
        studentRepo.addOrderToStudent(s.getId(), o.getId());

        if ("2".equals(pay)) {
            WalletPayment wallet = wallets.get(s.getId());
            if (wallet == null) { System.out.println("Wallet is not initialized."); return; }
            if (!wallet.pay(preview.totalDue)) { System.out.println("Not enough wallet balance."); return; }
            try {
                PaymentReceipt rc = pp.pay(o, s, variablePoints, preview.totalDue, loyalty, wallet);
                orders.updateStatus(o.getId(), OrderStatus.PREPARING);
                orders.markPaid(o.getId(), preview.totalDue, "WALLET");
                System.out.println(rc);
                notify.notifyStudent(s.getId(), "Order " + o.getId() + " paid via Wallet. Preparing.");
                notify.notifyStaff("Order " + o.getId() + " PREPARING (Wallet).");
                baseCart.clear(); // empty cart after successful placement
            } catch (IllegalArgumentException e) {
                System.out.println("Payment failed: " + e.getMessage());
            }
        } else {
            System.out.printf("Go to cashier with Order ID [%s] and pay %.2f EGP.%n", o.getId(), preview.totalDue);
            notify.notifyStaff("Order " + o.getId() + " awaiting CASH payment.");
            notify.notifyStudent(s.getId(), "Order " + o.getId() + " placed. Pay at cashier: " + preview.totalDue);
            baseCart.clear(); // order placed; cart becomes empty
        }
    }

    private static void showNotifications(Student s) {
        List<String> msgs = notify.getAndClearForStudent(s.getId());
        if (msgs.isEmpty()) System.out.println("No notifications.");
        else for (String m : msgs) System.out.println(m);
    }
    // --- Admin portal (in Main.java) ---
    private static void adminPortal(Scanner sc) {
        System.out.println("\n== Admin ==");
        System.out.print("Username: "); String user = sc.nextLine().trim();
        System.out.print("Password: "); String pass = sc.nextLine().trim();

        if (!admin.login(user, pass)) {
            System.out.println("Unauthorized.");
            return;
        }

        while (true) {
            System.out.println("\n1) Register Cashier");
            System.out.println("2) Register Chef");
            System.out.println("3) Register Manager");
            System.out.println("0) Back");
            System.out.print("Choose: ");
            String ch = sc.nextLine().trim();
            switch (ch) {
                case "1" -> registerEmp(sc, Role.CASHIER);
                case "2" -> registerEmp(sc, Role.CHEF);
                case "3" -> registerEmp(sc, Role.MANAGER);
                case "0" -> { return; }
                default -> System.out.println("Invalid.");
            }
        }
    }

    // Helper used by the admin portal
    private static void registerEmp(Scanner sc, Role role) {
        System.out.print("Employee ID: "); String id = sc.nextLine().trim();
        System.out.print("Name: "); String name = sc.nextLine().trim();
        System.out.print("PIN: "); String pin = sc.nextLine().trim();

        Employee e = admin.registerEmployee(role, id, name, pin);
        System.out.println(e != null ? "Registered." : "Failed (maybe duplicate ID?).");
    }


    // ---------- Employee / Manager / Admin ----------
    private static void employeePortal(Scanner sc) {
        System.out.println("\n== Employee Login ==");
        System.out.print("ID: "); String id = sc.nextLine().trim();
        System.out.print("PIN: "); String pin = sc.nextLine().trim();
        var eopt = auth.loginEmployee(id, pin);
        if (eopt.isEmpty()) { System.out.println("Login failed."); return; }
        Employee emp = eopt.get();
        switch (emp.getRole()) {
            case CASHIER -> cashierHome(sc, (Cashier) emp);
            case CHEF -> chefHome(sc, (Chef) emp);
            case MANAGER -> managerHome(sc, emp);
            default -> System.out.println("Unsupported role.");
        }
    }

    private static void cashierHome(Scanner sc, Cashier c) {
        while (true) {
            System.out.println("\n== Cashier ==");
            System.out.println("1) List PENDING orders");
            System.out.println("2) Confirm CASH payment");
            System.out.println("0) Logout");
            System.out.print("Choose: ");
            switch (sc.nextLine().trim()) {
                case "1" -> orders.listByStatus(OrderStatus.PENDING).forEach(System.out::println);
                case "2" -> {
                    System.out.print("Order ID: "); String oid = sc.nextLine().trim();
                    var rc = c.confirmCashPayment(oid, 0, pp, loyalty);
                    rc.ifPresentOrElse(
                            r -> { System.out.println("Payment confirmed."); System.out.println(r); },
                            () -> System.out.println("Failed to confirm payment.")
                    );
                }
                case "0" -> { return; }
                default -> System.out.println("Invalid.");
            }
        }
    }

    private static void chefHome(Scanner sc, Chef chef) {
        while (true) {
            System.out.println("\n== Chef ==");
            System.out.println("1) View PREPARING");
            System.out.println("2) Mark READY");
            System.out.println("0) Logout");
            System.out.print("Choose: ");
            switch (sc.nextLine().trim()) {
                case "1" -> orders.listByStatus(OrderStatus.PREPARING).forEach(System.out::println);
                case "2" -> {
                    System.out.print("Order ID: "); String oid = sc.nextLine().trim();
                    boolean ok = chef.setReady(oid);
                    System.out.println(ok ? "Marked READY." : "Could not mark READY.");
                }
                case "0" -> { return; }
                default -> System.out.println("Invalid.");
            }
        }
    }

    private static void managerHome(Scanner sc, Employee mng) {
        while (true) {
            System.out.println("\n-- Manager --");
            System.out.println("1) Show Menu");
            System.out.println("2) Add Item");
            System.out.println("3) Edit Item");
            System.out.println("4) Remove Item");
            System.out.println("5) Reports: Daily Summary");
            System.out.println("6) Reports: Weekly Summary");
            System.out.println("0) Back");
            System.out.print("Choose: ");
            switch (sc.nextLine().trim()) {
                case "1" -> menu.displayMenu();
                case "2" -> managerAddItem(sc);
                case "3" -> managerEditItem(sc);
                case "4" -> managerRemoveItem(sc);
                case "5" -> reports.printDailySummary(LocalDate.now());
                case "6" -> reports.printWeeklySummary(LocalDate.now());
                case "0" -> { return; }
                default -> System.out.println("Invalid.");
            }
        }
    }
    private static void managerAddItem(Scanner sc) {
        System.out.println("\n== Add Menu Item ==");
        System.out.print("Name: ");
        String name = sc.nextLine().trim();

        System.out.print("Description: ");
        String desc = sc.nextLine().trim();

        System.out.print("Price (EGP): ");
        double price = parseDoubleSafe(sc.nextLine(), 0.0);

        Category cat = askCategory(sc);

        // requires MenuManager.add(String name, String desc, double price, Category cat)
        MenuItem mi = menu.add(name, desc, price, cat);
        System.out.println("Added: " + mi);
    }



    private static void managerEditItem(Scanner sc) {
        System.out.println("\n== Edit Menu Item ==");
        menu.displayMenu();
        System.out.print("Enter Item ID: ");
        String id = sc.nextLine().trim();

        var opt = menu.findById(id);
        if (opt.isEmpty()) { System.out.println("Not found."); return; }
        MenuItem old = opt.get();
        System.out.println("Editing: " + old);

        System.out.print("New name (blank keep '" + old.getName() + "'): ");
        String newName = sc.nextLine();
        if (newName == null || newName.isBlank()) newName = old.getName();

        System.out.print("New description (blank keep current): ");
        String newDesc = sc.nextLine();
        if (newDesc == null || newDesc.isBlank()) newDesc = old.getDescription();

        System.out.print("New price (blank keep " + old.getPrice().asDouble() + "): ");
        String priceS = sc.nextLine();
        double newPrice = (priceS == null || priceS.isBlank())
                ? old.getPrice().asDouble()
                : parseDoubleSafe(priceS, old.getPrice().asDouble());

        System.out.print("New category (blank keep " + old.getCategory() + "): ");
        String catS = sc.nextLine();
        Category newCat = (catS == null || catS.isBlank())
                ? Category.valueOf(old.getCategory().toUpperCase())
                : parseCategory(catS, Category.valueOf(old.getCategory().toUpperCase()));

        boolean ok = menu.updateById(id, newName, newDesc, newPrice, newCat);
        System.out.println(ok ? "Item updated." : "Failed to update item.");
    }

    private static void managerRemoveItem(Scanner sc) {
        System.out.println("\n== Remove Menu Item ==");
        menu.displayMenu();
        System.out.print("Enter Item ID to remove: "); String id = sc.nextLine().trim();
        boolean ok = menu.removeById(id);
        System.out.println(ok ? "Removed." : "Not found.");
    }

    // ---------- Bootstrap ----------

    private static void bootstrap() {
        // Seed with descriptions
        menu.add("Cheeseburger", "Classic beef patty with cheese", 85.0, Category.MAIN);
        menu.add("Chicken Wrap", "Grilled chicken, fresh veggies, garlic sauce", 70.0, Category.MAIN);
        menu.add("Espresso", "Strong single shot of coffee", 25.0, Category.BEVERAGE);
        menu.add("Cappuccino", "Espresso topped with steamed milk foam", 35.0, Category.BEVERAGE);
        menu.add("Latte", "Espresso with silky steamed milk", 30.0, Category.BEVERAGE);

        loyalty.setDefaultCoffeePrice(25.0);

        auth = new Authentication(studentRepo);
        EmployeeFactory factory = new EmployeeFactory(orders, studentRepo, notify);
        registrar = new EmployeeRegister(auth, factory);
        admin = new Admin("Maro", "1234", registrar);

        reports = new ReportManager(orders);
    }

    // ---------- Helpers ----------
    private static MenuItem makeFreeCoffeeItem() {
        // Standalone item; not stored in MenuManager
        String id = "free-" + System.nanoTime(); // unique enough for an order
        return new MenuItem(id, "Free Coffee (credit)", "BEVERAGE", Money.of(0.0));
    }

    private static int parseIntSafe(String s, int def) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return def; }
    }
    private static double parseDoubleSafe(String s, double def) {
        try { return Double.parseDouble(s.trim()); } catch (Exception e) { return def; }
    }
    private static Category parseCategory(String s, Category def) {
        try { return Category.valueOf(s.trim().toUpperCase()); } catch (Exception e) { return def; }
    }
    private static Category askCategory(Scanner sc) {
        System.out.print("Category (MAIN/BEVERAGE/DESSERT): ");
        return parseCategory(sc.nextLine(), Category.MAIN);
    }
}
