public class Manager extends Employee {
    public Manager(String id, String name) {
        super(id, name, Role.MANAGER);
    }
    // Keep thin: menu/report actions are driven from Main via MenuManager/ReportManager
}
