import java.util.*;

public class NotificationService {
    private final Map<String, Deque<String>> inboxByStudent = new HashMap<>();

    public void notifyStudent(String studentId, String msg) {
        inboxByStudent.computeIfAbsent(studentId, k -> new ArrayDeque<>()).add(msg);
        System.out.println("[Notify Student " + studentId + "] " + msg); // keep console echo
    }
    public void notifyStaff(String msg) {
        System.out.println("[Notify Staff] " + msg);
    }
    /** Student pulls messages from their inbox (for in-app viewing). */
    public List<String> getAndClearForStudent(String studentId) {
        Deque<String> q = inboxByStudent.getOrDefault(studentId, new ArrayDeque<>());
        List<String> out = new ArrayList<>(q);
        q.clear();
        return out;
    }
}
