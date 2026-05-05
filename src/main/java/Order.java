import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {
    private final String id;
    private final String studentId;
    private final LocalDateTime createdAt;
    private final List<OrderLine> lines;
    private OrderStatus status;




    // ---- Payment tracking ----
    private boolean paid = false;
    private double amountPaid = 0.0;
    private LocalDateTime paidAt = null;
    private String paymentMethod = ""; // "WALLET" / "CASH"

    public Order(String id, String studentId, List<OrderLine> lines, OrderStatus status) {
        this.id = id;
        this.studentId = studentId;
        this.createdAt = LocalDateTime.now();
        this.lines = (lines == null) ? new ArrayList<>() : new ArrayList<>(lines);
        this.status = (status == null) ? OrderStatus.PENDING : status;
    }


    public String getId() {
        return id;
    }

    public String getStudentId() {
        return studentId;
    }


    /*
     * Calculates the total order value before applying any discounts:
     * - Uses each OrderLine subtotal if available
     * - Otherwise calculates price × quantity
     * - Returns the total as a double
     */

    public double getTotalBeforeDiscount() {
        double sum = 0.0;
        for (OrderLine ol : lines) {
            Money m = ol.getSubtotal();
            if (m != null) sum += m.asDouble();
            else sum += ol.getItem().getPrice().asDouble() * ol.getQuantity();
        }
        return sum;
    }


    // ---- Status ----
    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        if (status != null) this.status = status;
    }

    // ---- Paid tracking API ----
    public boolean isPaid() {
        return paid;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }



    public void markPaid(double amount, String method) {
        this.paid = true;
        this.amountPaid = Math.max(0.0, amount);
        this.paidAt = LocalDateTime.now();
        this.paymentMethod = (method == null ? "" : method);
    }

}
