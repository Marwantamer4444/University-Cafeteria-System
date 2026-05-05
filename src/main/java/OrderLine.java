// OrderLine.java (updated)
import java.util.Objects;

/** Value object: one line in an order; snapshots unitPrice at add time. */
public class OrderLine {
    private final MenuItem item;           // reference for display
    private final Money unitPriceSnapshot; // immutable snapshot
    private int quantity;

    public OrderLine(MenuItem item, int quantity) {
        this.item = Objects.requireNonNull(item);
        this.unitPriceSnapshot = item.getPrice();   // SNAPSHOT HERE
        this.quantity = Math.max(1, quantity);
    }

    public MenuItem getItem() {
        return item; }
    public Money getUnitPrice() {
        return unitPriceSnapshot; }
    public int getQuantity() {
        return quantity; }
    public void setQuantity(int q) {
        this.quantity = Math.max(1, q);
    }

    public Money getSubtotal() { return unitPriceSnapshot.multiply(quantity); }

    @Override public boolean equals(Object o){
        if(!(o instanceof OrderLine ol)) return false;
        return item.getId().equals(ol.item.getId()) && unitPriceSnapshot.equals(ol.unitPriceSnapshot) && quantity==ol.quantity;
    }
    @Override public int hashCode(){ return Objects.hash(item.getId(), unitPriceSnapshot, quantity); }

    @Override
    public String toString() {
        // Example: M001 | Latte  x2  @ 45.00 EGP  = 90.00 EGP
        String id = item.getId();
        String nm = item.getName();
        double unit = unitPriceSnapshot.asDouble();
        double sub = getSubtotal().asDouble();
        return String.format("%s | %s  x%d  @ %.2f EGP  = %.2f EGP", id, nm, quantity, unit, sub);
    }
}
