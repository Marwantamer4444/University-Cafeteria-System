import java.util.Objects;

/**
 * Represents an item in the cafeteria menu.
 * - Uses Money (BigDecimal wrapper) for currency-safe pricing.
 * - Immutable: id, name, description, category, and price do not change once created.
 *   (If prices/categories need updating, create a new MenuItem instead.)
 */
public final class MenuItem {

    private final String id;          // unique identifier (e.g., "M001" / "itm-1")
    private final String name;        // human-readable name
    private final String description; // short description shown to users
    private final String category;    // e.g., "BEVERAGE", "MAIN", "DESSERT"
    private final Money price;        // currency-safe price

    public MenuItem(String id, String name, String description, String category, Money price) {
        this.id = Objects.requireNonNull(id, "id").trim();
        this.name = Objects.requireNonNull(name, "name").trim();
        this.description = (description == null ? "" : description).trim();
        String cat = Objects.requireNonNull(category, "category").trim();
        this.category = cat.isEmpty() ? "MAIN" : cat.toUpperCase(java.util.Locale.ROOT);
        this.price = Objects.requireNonNull(price, "price");
    }

    /** Backward-compatible: no description provided. */
    public MenuItem(String id, String name, String category, Money price) {
        this(id, name, "", category, price);
    }

    // =====================================================================================
    // Getters
    // =====================================================================================

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public Money getPrice() { return price; }

    // =====================================================================================
    // Helpers
    // =====================================================================================

    @Override
    public String toString() {
        return id + " | " + name + " - " + description + " (" + category + ") - " + price;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MenuItem other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode(); }
}
