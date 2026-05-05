import java.util.Objects;
//is responsible for creating Employee objects.
public final class EmployeeFactory {
    private final OrderProcessor orders;
    private final StudentManager students;
    private final NotificationService notify;

    public EmployeeFactory(OrderProcessor orders, StudentManager students, NotificationService notify) {
       //check lw el object b null lw b null by3ml excpettion null  pointer
        this.orders = Objects.requireNonNull(orders);
        this.students = Objects.requireNonNull(students);
        this.notify = Objects.requireNonNull(notify);
    }


    public Employee create(Role role, String id, String name) {
        switch (role) {
            case CASHIER -> { return new Cashier(id, name, orders, students, notify); }
            case CHEF    -> { return new Chef(id, name, orders, notify); }
            case MANAGER -> { return new Manager(id, name); }
            default -> throw new IllegalArgumentException("Unsupported role: " + role);
        }
    }
}
