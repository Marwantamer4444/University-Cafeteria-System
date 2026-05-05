public class Cashier extends Employee {
    private final OrderProcessor orderProcessor;
    private final StudentManager studentManager;
    private final NotificationService notifications;

    public Cashier(String id, String name,
                   OrderProcessor orderProcessor,
                   StudentManager studentManager,
                   NotificationService notifications) {
        super(id, name, Role.CASHIER);
        this.orderProcessor = orderProcessor;
        this.studentManager = studentManager;
        this.notifications = notifications;
    }


//method b return payment receipt
    public java.util.Optional<PaymentReceipt> confirmCashPayment(
            String orderId,
            int pointsToRedeem_UNUSED,
            IPaymentProcessor paymentSvc,
            LoyaltyProgram loyalty
    ) {
        var orderOpt = orderProcessor.findById(orderId);
        if (orderOpt.isEmpty()) { System.out.println("Order not found."); return java.util.Optional.empty(); }
        Order o = orderOpt.get();
        //Find the student who placed this order, using their ID.
        var studentOpt = studentManager.findById(o.getStudentId());
        if (studentOpt.isEmpty()) { System.out.println("Student not found."); return java.util.Optional.empty(); }
        Student s = studentOpt.get();

        //Get the order total before any discounts/loyalty points.
        double subtotal = o.getTotalBeforeDiscount();
        PaymentBreakdown preview = paymentSvc.preview(subtotal, 0, loyalty);

        try {
            PaymentReceipt rc = paymentSvc.pay(o, s, 0, preview.totalDue, loyalty, new CashPayment());
            orderProcessor.setStatus(o.getId(), OrderStatus.PREPARING);
            orderProcessor.markPaid(o.getId(), preview.totalDue, "CASH");
            // Sends notifications: to students and staff
            notifications.notifyStudent(s.getId(), "Cash received for Order " + o.getId() + ". Preparing now.");
            notifications.notifyStaff("Order " + o.getId() + " is PREPARING (Cash paid).");
            return java.util.Optional.of(rc);
        } catch (IllegalArgumentException ex) {
            System.out.println("Payment failed: " + ex.getMessage());
            return java.util.Optional.empty();
        }
    }
}
