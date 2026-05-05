public class RedemptionResult {
    public final boolean applied;
    public final int pointsUsed;     // how many points were actually deducted
    public final double discount;    // EGP discount obtained from those points/rule
    public final String ruleName;    // e.g., "Free Coffee"

    public RedemptionResult(boolean applied, int pointsUsed, double discount, String ruleName) {
        this.applied = applied;
        this.pointsUsed = Math.max(0, pointsUsed);
        this.discount = Math.max(0.0, discount);
        this.ruleName = (ruleName == null ? "" : ruleName);
    }

    public static RedemptionResult none() {
        return new RedemptionResult(false, 0, 0.0, "");
    }

    @Override
    public String toString() {
        return applied
                ? ruleName + " applied: -" + discount + " EGP (points used: " + pointsUsed + ")"
                : "No redemption";
    }
}
