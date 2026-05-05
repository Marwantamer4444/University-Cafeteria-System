import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

public class ReportManager {
    private final OrderProcessor orderProcessor;

    public ReportManager(OrderProcessor orderProcessor) {
        this.orderProcessor = orderProcessor;
    }

    public void printDailySummary(LocalDate day) {
        if (day == null) day = LocalDate.now();
        int count = 0;
        double revenue = 0.0;

        List<Order> os = orderProcessor.listAll();
        for (Order o : os) {
            if (!o.isPaid() || o.getPaidAt() == null) continue;
            if (o.getPaidAt().toLocalDate().isEqual(day)) {
                count++;
                revenue += o.getAmountPaid();
            }
        }
        System.out.println("=== Daily Summary (PAID) " + day + " ===");
        System.out.println("Paid Orders: " + count);
        System.out.printf("Revenue (paid): %.2f EGP%n", revenue);
    }

    public void printWeeklySummary(LocalDate referenceDate) {
        if (referenceDate == null) referenceDate = LocalDate.now();
        WeekFields wf = WeekFields.of(Locale.getDefault());
        int refWeek = referenceDate.get(wf.weekOfWeekBasedYear());
        int refYear = referenceDate.get(wf.weekBasedYear());

        int count = 0;
        double revenue = 0.0;

        List<Order> os = orderProcessor.listAll();
        //filtering the order list to only include paid orders for a specific date,
        // then updating the daily order count and revenue.
        for (Order o : os) {
            if (!o.isPaid() || o.getPaidAt() == null) continue;
            LocalDate d = o.getPaidAt().toLocalDate();
            int w = d.get(wf.weekOfWeekBasedYear());
            int y = d.get(wf.weekBasedYear());
            if (w == refWeek && y == refYear) {
                count++;
                revenue += o.getAmountPaid();
            }
        }
        System.out.println("=== Weekly Summary (PAID) week " + refWeek + ", " + refYear + " ===");
        System.out.println("Paid Orders: " + count);
        System.out.printf("Revenue (paid): %.2f EGP%n", revenue);
    }
}
