import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

//class handles:
//
//Registering employees.
//
//Storing their PINs.
//
//Authenticating employees (login check).
public class EmployeeRegister {
    private final Authentication auth;
    private final EmployeeFactory factory;

    private final Map<String, Employee> employees = new HashMap<>();
    private final Map<String, String> pins = new HashMap<>();

    public EmployeeRegister(Authentication auth, EmployeeFactory factory) {
        this.auth = auth;
        this.factory = factory;
        if (this.auth != null) this.auth.setEmployeeRegister(this);
    }

    /**
     * Register a new employee using the factory that expects (Role, id, name).
     * The PIN is stored separately for login verification.
     */
    public Employee register(Role role, String id, String name, String pin) {
        if (role == null || id == null || id.isBlank() || name == null || name.isBlank() || pin == null) return null;
        //Check for duplicate ID
        if (employees.containsKey(id)) return null;

        // Your EmployeeFactory expects exactly THREE args: (Role, id, name)
        Employee e = factory.create(role, id, name);
        if (e == null) return null;

        employees.put(id, e);
        pins.put(id, pin);
        return e;
    }



    /** Authentication entrypoint used by Authentication.loginEmployee(...). */
    public Optional<Employee> loginEmployee(String id, String pin) {
        if (id == null || pin == null) return Optional.empty();
        if (!pin.equals(pins.get(id))) return Optional.empty();
        return Optional.ofNullable(employees.get(id));
    }
}
