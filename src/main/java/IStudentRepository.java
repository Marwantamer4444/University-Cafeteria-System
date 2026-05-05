import java.util.List;
import java.util.Optional;

public interface IStudentRepository {
    Optional<Student> findById(String id);
    boolean addOrderToStudent(String id, String orderId);
    boolean addPoints(String id, int points);
    List<Student> getAllStudents();

    // Required by Authentication: look up and verify a student by id+pin.
    Optional<Student> login(String id, String pin);
}
