import java.util.Optional;

public class Authentication {
    private final StudentManager studentManager;
    private EmployeeRegister employeeRegister; //

    public Authentication(StudentManager studentManager) {
        this.studentManager = studentManager;
    }

    public void setEmployeeRegister(EmployeeRegister er) {
        this.employeeRegister = er;
    }
// container object
    public Optional<Student> loginStudent(String id, String pin) {
        Optional<Student> s = studentManager.findById(id);
        if (s.isPresent() && s.get().checkPin(pin)) return s;
        return Optional.empty();
    }


    public Optional<Employee> loginEmployee(String id, String pin) {
        if (employeeRegister == null) return Optional.empty();
        return employeeRegister.loginEmployee(id, pin);
    }
}
