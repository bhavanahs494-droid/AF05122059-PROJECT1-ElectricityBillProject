package electricity;

import java.util.Scanner;

public class Main {

 public static void main(String[] args) {

     BillService service = new BillService();
     Scanner sc = new Scanner(System.in);

     System.out.println("╔══════════════════════════════════════════╗");
     System.out.println("║   ELECTRICITY BILL MANAGEMENT SYSTEM     ║");
     System.out.println("║   Connecting to database...              ║");
     System.out.println("╚══════════════════════════════════════════╝");

     // Test DB connection at startup
     if (electricity.DBConnection.getConnection() != null) {
         System.out.println("✅ Database Connected Successfully!\n");
     } else {
         System.out.println("❌ Database connection failed. Check DBConnection.java");
         return;
     }

     while (true) {
         printMenu();
         System.out.print("Enter Choice: ");
         int choice;
         try {
             choice = Integer.parseInt(sc.nextLine().trim());
         } catch (NumberFormatException e) {
             System.out.println("⚠️  Please enter a valid number.");
             continue;
         }

         switch (choice) {

             // ── OPTION 1: Add Consumer ───────────────────
             case 1 -> {
                 System.out.println("\n--- ADD NEW CONSUMER ---");
                 System.out.print("Name          : "); String name = sc.nextLine();
                 System.out.print("Address       : "); String addr = sc.nextLine();
                 System.out.print("Meter Number  : "); String meter = sc.nextLine();
                 System.out.println("Connection Type Options: DOMESTIC / COMMERCIAL / INDUSTRIAL");
                 System.out.print("Connection Type: "); String type = sc.nextLine();
                 service.registerConsumer(name, addr, meter, type);
             }

             // ── OPTION 2: View All Consumers ─────────────
             case 2 -> {
                 System.out.println("\n--- ALL REGISTERED CONSUMERS ---");
                 service.showAllConsumers();
             }

             // ── OPTION 3: Search Consumer by Meter ───────
             case 3 -> {
                 System.out.println("\n--- SEARCH CONSUMER ---");
                 System.out.print("Enter Meter Number (e.g. M-10001): ");
                 String meter = sc.nextLine();
                 service.searchConsumer(meter);
             }

             // ── OPTION 4: Add Meter Reading + Bill ───────
             case 4 -> {
                 System.out.println("\n--- ADD METER READING ---");
                 System.out.print("Consumer ID       : ");
                 int cid = Integer.parseInt(sc.nextLine().trim());
                 System.out.print("Previous Reading  : ");
                 int prev = Integer.parseInt(sc.nextLine().trim());
                 System.out.print("Current Reading   : ");
                 int curr = Integer.parseInt(sc.nextLine().trim());
                 System.out.print("Month (e.g. April-2025): ");
                 String month = sc.nextLine();
                 System.out.print("Due Date (YYYY-MM-DD)  : ");
                 String due = sc.nextLine();
                 service.addMeterReading(cid, month, prev, curr, due);
             }

             // ── OPTION 5: Pay Bill ────────────────────────
             case 5 -> {
                 System.out.println("\n--- PAY BILL ---");
                 service.showUnpaidBills();   // show list first
                 System.out.print("Enter Bill ID to Pay: ");
                 int billId = Integer.parseInt(sc.nextLine().trim());
                 service.payBill(billId);
             }

             // ── OPTION 6: View Unpaid Bills ───────────────
             case 6 -> {
                 System.out.println("\n--- UNPAID BILLS ---");
                 service.showUnpaidBills();
             }

             // ── OPTION 7: Billing History ─────────────────
             case 7 -> {
                 System.out.println("\n--- BILLING HISTORY ---");
                 System.out.print("Enter Consumer ID: ");
                 int cid = Integer.parseInt(sc.nextLine().trim());
                 service.showBillingHistory(cid);
             }

             // ── OPTION 8: View Rate Slabs ─────────────────
             case 8 -> service.showBillSlabs();

             // ── OPTION 9: Exit ────────────────────────────
             case 9 -> {
                 System.out.println("\n✅ Thank you! Exiting system...");
                 sc.close();
                 return;
             }

             default -> System.out.println("⚠️  Invalid choice. Enter 1-9.");
         }
     }
 }

 // ─── Menu Display ────────────────────────────────────────
 static void printMenu() {
     System.out.println("\n╔══════════════════════════════════════╗");
     System.out.println("║      ELECTRICITY BILL SYSTEM         ║");
     System.out.println("╠══════════════════════════════════════╣");
     System.out.println("║  1. Add Consumer                     ║");
     System.out.println("║  2. View All Consumers               ║");
     System.out.println("║  3. Search Consumer by Meter No      ║");
     System.out.println("║  4. Add Meter Reading (Generates Bill)║");
     System.out.println("║  5. Pay Bill                         ║");
     System.out.println("║  6. View All Unpaid Bills            ║");
     System.out.println("║  7. View Consumer Billing History    ║");
     System.out.println("║  8. View Rate Slabs                  ║");
     System.out.println("║  9. Exit                             ║");
     System.out.println("╚══════════════════════════════════════╝");
 }
}