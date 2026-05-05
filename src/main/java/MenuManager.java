import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class MenuManager implements IMenuManager {

    private static final AtomicInteger SEQ = new AtomicInteger(1);
    private final Map<String, MenuItem> byId = new LinkedHashMap<>();

    // ---- Add (legacy, no description) ----
    @Override
    public MenuItem add(String name, double priceEGP, Category category) {
        return add(name, "", priceEGP, category);
    }

    // ---- Add (with description) ----
    @Override
    public MenuItem add(String name, String description, double priceEGP, Category category) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name required");
        if (category == null) category = Category.MAIN;
        if (priceEGP < 0) priceEGP = 0;

        String id = "itm-" + SEQ.getAndIncrement();
        MenuItem mi = new MenuItem(
                id,
                name,
                (description == null ? "" : description),
                category.name(),           // MenuItem stores category as String
                Money.of(priceEGP)
        );
        byId.put(id, mi);
        return mi;
    }

    @Override
    public Optional<MenuItem> findById(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(byId.get(id));
    }



    // ---- Update (with description change) ----
    @Override
    public boolean updateById(String id, String newName, String newDescription, double newPriceEGP, Category newCategory) {
        MenuItem old = byId.get(id);
        if (old == null) return false;

        String name  = (newName == null || newName.isBlank()) ? old.getName() : newName;
        String desc  = (newDescription == null) ? old.getDescription() : newDescription;
        double price = (newPriceEGP < 0) ? old.getPrice().asDouble() : newPriceEGP;
        String cat   = (newCategory == null) ? old.getCategory() : newCategory.name();

        MenuItem updated = new MenuItem(old.getId(), name, desc, cat, Money.of(price));
        byId.put(id, updated);
        return true;
    }

    @Override
    public boolean removeById(String id) {
        return byId.remove(id) != null;
    }

    @Override
    public void displayMenu() {
        if (byId.isEmpty()) {
            System.out.println("(Menu is empty)");
            return;
        }
        System.out.println("\n-- Menu --");
        System.out.printf("%-8s | %-18s | %-8s | %-10s | %s%n",
                "ID", "Name", "Price", "Category", "Description");
        System.out.println("--------------------------------------------------------------------------");
        for (MenuItem mi : byId.values()) {
            System.out.printf("%-8s | %-18s | %-8.2f | %-10s | %s%n",
                    mi.getId(), mi.getName(), mi.getPrice().asDouble(), mi.getCategory(), mi.getDescription());
        }
    }
}
