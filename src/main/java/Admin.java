public class Admin {
    private final String username;
    private final String password;
    private final EmployeeRegister registrar;

    public Admin(String username, String password, EmployeeRegister registrar) {
        this.username = username;
        this.password = password;
        this.registrar = registrar;
    }

    public boolean login(String user, String pass) {
        return this.username.equals(user) && this.password.equals(pass);
    }

    /**
     * Register an employee with a role. Returns the created Employee, or null on failure.
     */
    public Employee registerEmployee(Role role, String id, String name, String pin) {
        if (registrar == null) return null;
        return registrar.register(role, id, name, pin);
    }
}
