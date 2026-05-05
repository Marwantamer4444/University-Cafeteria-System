import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Student {
    private final String id;
    private final String name;
    private final String pin;

    // Loyalty points
    private int loyaltyPoints = 0;

    // Free-coffee credits the student can redeem from points on the home screen
    private int freeCoffeeCredits = 0;

    // Track IDs of orders created by this student
    private final List<String> orderIds = new ArrayList<>();

    public Student(String id, String name, String pin) {
        this.id = id;
        this.name = name;
        this.pin = pin;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public boolean checkPin(String input) {
        return pin != null && pin.equals(input);
    }

    // ---- Loyalty points ----
    public int getLoyaltyPoints() { return loyaltyPoints;
    }

    //Add or subtract points; result never below 0.
    public void addPoints(int delta) {
        long next = (long) loyaltyPoints + delta;
        if (next < 0) next = 0;
        if (next > Integer.MAX_VALUE) next = Integer.MAX_VALUE;
        loyaltyPoints = (int) next;
    }

    // ---- Free coffee credits ----
    public int getFreeCoffeeCredits() {
        return freeCoffeeCredits;
    }
    public void addFreeCoffeeCredit() {
        freeCoffeeCredits++;
    }
    // Consumes one credit if available; returns true if consumed.
    public boolean consumeFreeCoffeeCredit() {
        if (freeCoffeeCredits <= 0) return false;
        freeCoffeeCredits--;
        return true;
    }


    @Override
    public String toString() {
        return "Student{id='" + id + "', name='" + name + "', points=" + loyaltyPoints +
                ", coffeeCredits=" + freeCoffeeCredits + ", orders=" + orderIds.size() + "}";
    }
}
