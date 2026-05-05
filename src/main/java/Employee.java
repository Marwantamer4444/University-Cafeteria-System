public abstract class Employee {
    private final String id;
    private final String name;
    private final Role role;

    protected Employee(String id, String name, Role role) {
        this.id = id; this.name = name; this.role = role;
    }
    public String getId() { return id; }
    public String getName() { return name; }
    public Role getRole() { return role; }
    //string template formatting.
    @Override public String toString() {
        return "%s(%s)".formatted(name, role);
    }
}
