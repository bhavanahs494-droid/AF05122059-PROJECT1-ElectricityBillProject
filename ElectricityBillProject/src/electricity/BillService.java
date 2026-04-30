package electricity;

import java.util.List;

public class BillService {

 private ConsumerDAO dao = new ConsumerDAO();

 // ─── 1. Register Consumer (with validation) ──────────────
 public void registerConsumer(String name, String address,
                               String meterNumber, String type) {
     // Validate
     if (name == null || name.trim().isEmpty()) {
         System.out.println("⚠️  Name cannot be empty."); return;
     }
     if (meterNumber == null || meterNumber.trim().isEmpty()) {
         System.out.println("⚠️  Meter number cannot be empty."); return;
     }
     if (!type.equalsIgnoreCase("DOMESTIC")
      && !type.equalsIgnoreCase("COMMERCIAL")
      && !type.equalsIgnoreCase("INDUSTRIAL")) {
         System.out.println("⚠️  Type must be DOMESTIC, COMMERCIAL, or INDUSTRIAL."); return;
     }
     dao.addConsumer(name.trim(), address.trim(),
                     meterNumber.trim().toUpperCase(), type.trim().toUpperCase());
 }

 // ─── 2. Display All Consumers ────────────────────────────
 public void showAllConsumers() {
     List<Consumer> list = dao.getAllConsumers();
     if (list.isEmpty()) {
         System.out.println("  No consumers registered yet."); return;
     }
     System.out.println("\n╔══════════════════ ALL CONSUMERS ══════════════════╗");
     for (Consumer c : list) {
         System.out.println("  " + c);
     }
     System.out.println("╚════════════════════════════════════════════════════╝");
 }

 // ─── 3. Search Consumer by Meter Number ──────────────────
 public void searchConsumer(String meterNumber) {
     Consumer c = dao.searchByMeter(meterNumber.toUpperCase());
     if (c != null) {
         System.out.println("\n✅ Consumer Found:");
         System.out.println("   " + c);
     } else {
         System.out.println("⚠️  No consumer found with meter: " + meterNumber);
     }
 }

 // ─── 4. Add Meter Reading (with validation) ──────────────
 public void addMeterReading(int consumerId, String month,
                              int prev, int curr, String dueDate) {
     if (curr <= prev) {
         System.out.println("⚠️  Current reading must be GREATER than previous reading!");
         return;
     }
     if (month == null || month.trim().isEmpty()) {
         System.out.println("⚠️  Month cannot be empty. Example: April-2025");
         return;
     }
     if (!dueDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
         System.out.println("⚠️  Due date must be in YYYY-MM-DD format. Example: 2025-05-10");
         return;
     }
     dao.addReadingAndGenerateBill(consumerId, month.trim(), prev, curr, dueDate.trim());
 }

 // ─── 5. Pay a Bill ───────────────────────────────────────
 public void payBill(int billId) {
     if (billId <= 0) {
         System.out.println("⚠️  Invalid Bill ID."); return;
     }
     dao.payBill(billId);
 }

 // ─── 6. Show Unpaid Bills ────────────────────────────────
 public void showUnpaidBills() {
     dao.viewUnpaidBills();
 }

 // ─── 7. Show Billing History ─────────────────────────────
 public void showBillingHistory(int consumerId) {
     if (consumerId <= 0) {
         System.out.println("⚠️  Invalid Consumer ID."); return;
     }
     dao.viewBillingHistory(consumerId);
 }

 // ─── 8. Show Bill Slab Info ──────────────────────────────
 public void showBillSlabs() {
     System.out.println("\n╔══════════ BILLING RATE SLABS ══════════╗");
     System.out.println("  DOMESTIC:");
     System.out.println("    0   - 100 units  →  ₹3.50 per unit");
     System.out.println("    101 - 300 units  →  ₹5.00 per unit");
     System.out.println("    301 + units      →  ₹7.00 per unit");
     System.out.println("  COMMERCIAL  →  ₹8.00 per unit (flat)");
     System.out.println("  INDUSTRIAL  →  ₹6.50 per unit (flat)");
     System.out.println("  Fixed Service Charge: ₹50 (all types)");
     System.out.println("╚════════════════════════════════════════╝");
 }
}
