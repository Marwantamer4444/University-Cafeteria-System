public class Chef extends Employee {
    private final OrderProcessor orderProcessor;
    private final NotificationService notifications;

    public Chef(String id, String name,
                OrderProcessor orderProcessor,
                NotificationService notifications) {
        super(id, name, Role.CHEF);
        this.orderProcessor = orderProcessor;
        this.notifications = notifications;
    }

    //Marks the order READY and notifies the student & staff.
    //Find the order.
    //
    //If not found → return false.
    //
    //If found → try to set status to READY.
    //
    //If successful → notify student and staff.
    //
    //Return the result (true or false).
    public boolean setReady(String orderId) {
        var opt = orderProcessor.findById(orderId);
        if (opt.isEmpty()) return false;
        Order o = opt.get();

        boolean ok = orderProcessor.setStatus(orderId, OrderStatus.READY);
        if (ok) {
            notifications.notifyStudent(o.getStudentId(), "Order " + orderId + " is READY for pickup.");
            notifications.notifyStaff("Order " + orderId + " marked READY.");
        }
        return ok;
    }
}
