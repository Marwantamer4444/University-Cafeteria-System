import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/** In-memory order store + simple status helpers. */
public class OrderProcessor {
    private static final AtomicInteger SEQ = new AtomicInteger(1);
    private final Map<String, Order> byId = new LinkedHashMap<>();

    /**
     * Creates and stores a new order with PENDING status using IDs ord-1, ord-2, ...
     */
    public Order createOrder(String studentId, List<OrderLine> lines) {
        String id = "ord-" + SEQ.getAndIncrement();
        Order o = new Order(id, studentId, lines, OrderStatus.PENDING);
        byId.put(id, o);
        return o;
    }

    public Optional<Order> findById(String id) {
        return (id == null) ? Optional.empty() : Optional.ofNullable(byId.get(id));
    }

    public List<Order> listAll() {
        return List.copyOf(byId.values());
    }

    public List<Order> listByStatus(OrderStatus s) {
        if (s == null) return List.of();
        return byId.values().stream()
                .filter(o -> s.equals(o.getStatus()))
                .collect(Collectors.toList());
    }

    public void updateStatus(String id, OrderStatus st) {
        if (id == null || st == null) return;
        Order o = byId.get(id);
        if (o != null) o.setStatus(st);
    }

    public boolean setStatus(String id, OrderStatus st) {
        if (id == null || st == null) return false;
        Order o = byId.get(id);
        if (o == null) return false;
        o.setStatus(st);
        return true;
    }


    public void markPaid(String id, double amountPaid, String method) {
        Order o = byId.get(id);
        if (o != null) o.markPaid(amountPaid, method);
    }

}