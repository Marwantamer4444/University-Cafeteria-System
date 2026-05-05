public class LoyaltyProgram {

    // 10 points = 1 EGP
    private static final int POINTS_PER_EGP = 10;

    // Earn: 1 point per 10 EGP actually paid
    private static final double EARN_RATE_EGP_PER_POINT = 10.0;

    // Default Free Coffee price (EGP) if not tied to a specific item
    private double defaultCoffeePriceEGP = 25.0;

    public void setDefaultCoffeePrice(double priceEGP) {
        if (priceEGP > 0) this.defaultCoffeePriceEGP = priceEGP;
    }
    public double getDefaultCoffeePrice() { return defaultCoffeePriceEGP; }

    // ---- Conversions ----
    //convert from point to egp
    public double pointsToEGP(int points) {
        return points <= 0 ? 0.0 : points / (double) POINTS_PER_EGP;
    }
    //convert from egp to point
    public int egpToPointsCeil(double egp) {
        return egp <= 0 ? 0 : (int) Math.ceil(egp * POINTS_PER_EGP);
    }

    // ---- how much discount (in EGP) the requested points are worth.----
    public double previewDiscount(int pointsToRedeem) {
        return pointsToEGP(pointsToRedeem);
    }

    /* Redeems up to the student's available points, deducts them,
       and returns the equivalent discount in EGP.
        using max */
    public double applyRedemption(Student s, int pointsToRedeem) {
        if (s == null || pointsToRedeem <= 0) return 0.0;
        int usable = Math.min(pointsToRedeem, Math.max(0, s.getLoyaltyPoints()));
        if (usable <= 0) return 0.0;
        s.addPoints(-usable); // safe, clamped in Student
        return pointsToEGP(usable);
    }

    // ---- Earning points (after payment) ----
    public int pointsEarnedFromAmount(double amountPaidEGP) {
        if (amountPaidEGP <= 0) return 0;
        return (int) Math.floor(amountPaidEGP / EARN_RATE_EGP_PER_POINT);
    }

    // ---- Free Coffee (student-home redemption -> credit) ----
    /** How many points are required for a Free Coffee at the default price. */
    public int freeCoffeePoints(double coffeePriceEGP) {
        return egpToPointsCeil(coffeePriceEGP); }


    /**
     * Redeems a Free Coffee CREDIT from the student home:
     * - Deducts points immediately
     * - Adds 1 credit to the student (used automatically on next checkout)
     * Returns a RedemptionResult describing the redemption, or RedemptionResult.none() if ineligible.
     */
    public RedemptionResult redeemFreeCoffeeCredit(Student s) {
        if (s == null) return RedemptionResult.none();
        int needed = freeCoffeePoints(defaultCoffeePriceEGP);
        if (s.getLoyaltyPoints() < needed) return RedemptionResult.none();

        s.addPoints(-needed);        // deduct points now
        s.addFreeCoffeeCredit();     // grant credit to use later
        return new RedemptionResult(true, needed, defaultCoffeePriceEGP, "Free Coffee (credit)");
    }
}
