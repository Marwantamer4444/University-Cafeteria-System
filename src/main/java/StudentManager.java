import java.util.*;

/**
 * In-memory student repository.
 * Stores students and (optionally) their order IDs.
 */
public class StudentManager implements IStudentRepository {

    private final Map<String, Student> students = new LinkedHashMap<>();
    // Keep order IDs per student here (so Student doesn't have to own them)
    private final Map<String, List<String>> orderIdsByStudent = new HashMap<>();

    // ---------- Queries ----------

    @Override
    public Optional<Student> findById(String id) {
        return Optional.ofNullable(students.get(id));
    }

    @Override
    public List<Student> getAllStudents() {
        return List.copyOf(students.values());
    }

    @Override
    public Optional<Student> login(String id, String pin) {
        Student s = students.get(id);
        if (s == null) return Optional.empty();
        return s.checkPin(pin) ? Optional.of(s) : Optional.empty();
    }

    // ---------- Mutations ----------

    /** Register a new student (false if duplicate or invalid). */
    public boolean register(Student s) {
        if (s == null || s.getId() == null || s.getId().isBlank()) return false;
        if (students.containsKey(s.getId())) return false;
        students.put(s.getId(), s);
        orderIdsByStudent.put(s.getId(), new ArrayList<>());
        return true;
    }

    @Override
    public boolean addOrderToStudent(String id, String orderId) {
        if (id == null || orderId == null || orderId.isBlank()) return false;
        if (!students.containsKey(id)) return false;

        // Avoid lambda “unused parameter” warnings by expanding the logic.
        List<String> list = orderIdsByStudent.get(id);
        if (list == null) {
            list = new ArrayList<>();
            orderIdsByStudent.put(id, list);
        }
        list.add(orderId);
        return true;
    }

    @Override
    public boolean addPoints(String id, int points) {
        Student s = students.get(id);
        if (s == null) return false;
        s.addPoints(points);
        return true;
    }
    }


