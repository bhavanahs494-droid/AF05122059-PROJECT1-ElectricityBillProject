package electricity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConsumerDAO {

 // ─── 1. Add New Consumer ─────────────────────────────────
 public boolean addConsumer(String name, String address,
                            String meterNumber, String type) {
     String sql = "INSERT INTO consumers (name, address, meter_number, connection_type) "
                + "VALUES (?, ?, ?, ?)";
     try (Connection con = DBConnection.getConnection();
          PreparedStatement ps = con.prepareStatement(sql)) {

         ps.setString(1, name);
         ps.setString(2, address);
         ps.setString(3, meterNumber);
         ps.setString(4, type);
         ps.executeUpdate();
         System.out.println("✅ Consumer added: " + name);
         return true;

     } catch (SQLException e) {
         System.out.println("❌ Error adding consumer: " + e.getMessage());
         return false;
     }
 }

 // ─── 2. View All Consumers ───────────────────────────────
 public List<Consumer> getAllConsumers() {
     List<Consumer> list = new ArrayList<>();
     String sql = "SELECT * FROM consumers ORDER BY consumer_id";

     try (Connection con = DBConnection.getConnection();
          Statement st = con.createStatement();
          ResultSet rs = st.executeQuery(sql)) {

         while (rs.next()) {
             list.add(new Consumer(
                 rs.getInt("consumer_id"),
                 rs.getString("name"),
                 rs.getString("address"),
                 rs.getString("meter_number"),
                 rs.getString("connection_type")
             ));
         }
     } catch (SQLException e) {
         System.out.println("❌ Error fetching consumers: " + e.getMessage());
     }
     return list;
 }

 // ─── 3. Search Consumer by Meter Number ──────────────────
 public Consumer searchByMeter(String meterNumber) {
     String sql = "SELECT * FROM consumers WHERE meter_number = ?";
     try (Connection con = DBConnection.getConnection();
          PreparedStatement ps = con.prepareStatement(sql)) {

         ps.setString(1, meterNumber);
         ResultSet rs = ps.executeQuery();
         if (rs.next()) {
             return new Consumer(
                 rs.getInt("consumer_id"),
                 rs.getString("name"),
                 rs.getString("address"),
                 rs.getString("meter_number"),
                 rs.getString("connection_type")
             );
         }
     } catch (SQLException e) {
         System.out.println("❌ Error searching: " + e.getMessage());
     }
     return null;
 }

 // ─── 4. Calculate Bill (Slab-based) ──────────────────────
 public double calculateBill(int units, String connectionType) {
     double amount = 0;
     switch (connectionType.toUpperCase()) {
         case "DOMESTIC" -> {
             // Slab rates
             if      (units <= 100) amount = units * 3.50;
             else if (units <= 300) amount = 100 * 3.50 + (units - 100) * 5.00;
             else                   amount = 100 * 3.50 + 200 * 5.00 + (units - 300) * 7.00;
         }
         case "COMMERCIAL"  -> amount = units * 8.00;
         case "INDUSTRIAL"  -> amount = units * 6.50;
         default            -> amount = units * 5.00;
     }
     return amount + 50.0;  // ₹50 fixed service charge
 }

 // ─── 5. Add Meter Reading + Auto-generate Bill ───────────
 public boolean addReadingAndGenerateBill(int consumerId, String month,
                                          int prevReading, int currReading,
                                          String dueDate) {
     if (currReading <= prevReading) {
         System.out.println("❌ Current reading must be greater than previous reading!");
         return false;
     }

     try (Connection con = DBConnection.getConnection()) {
         con.setAutoCommit(false);  // Begin Transaction

         // Insert meter reading
         String readSql = "INSERT INTO meter_readings "
                        + "(consumer_id, reading_month, previous_reading, current_reading) "
                        + "VALUES (?, ?, ?, ?)";
         PreparedStatement readPs = con.prepareStatement(
                 readSql, Statement.RETURN_GENERATED_KEYS);
         readPs.setInt(1, consumerId);
         readPs.setString(2, month);
         readPs.setInt(3, prevReading);
         readPs.setInt(4, currReading);
         readPs.executeUpdate();

         // Get generated reading_id
         ResultSet generatedKeys = readPs.getGeneratedKeys();
         int readingId = generatedKeys.next() ? generatedKeys.getInt(1) : -1;

         // Get consumer connection type for billing
         PreparedStatement typePs = con.prepareStatement(
             "SELECT connection_type FROM consumers WHERE consumer_id = ?");
         typePs.setInt(1, consumerId);
         ResultSet typeRs = typePs.executeQuery();
         String connType = typeRs.next() ? typeRs.getString("connection_type") : "DOMESTIC";

         int units      = currReading - prevReading;
         double amount  = calculateBill(units, connType);

         // Insert bill
         String billSql = "INSERT INTO bills "
                        + "(consumer_id, reading_id, bill_month, amount, due_date) "
                        + "VALUES (?, ?, ?, ?, ?)";
         PreparedStatement billPs = con.prepareStatement(billSql);
         billPs.setInt(1, consumerId);
         billPs.setInt(2, readingId);
         billPs.setString(3, month);
         billPs.setDouble(4, amount);
         billPs.setString(5, dueDate);
         billPs.executeUpdate();

         con.commit();  // Commit Transaction
         System.out.println("✅ Meter reading saved. Units consumed: " + units);
         System.out.printf("   Bill Generated: ₹%.2f | Due Date: %s%n", amount, dueDate);
         return true;

     } catch (SQLException e) {
         System.out.println("❌ Error generating bill: " + e.getMessage());
         return false;
     }
 }

 // ─── 6. Pay Bill ─────────────────────────────────────────
 public boolean payBill(int billId) {
     String sql = "UPDATE bills SET paid = TRUE, paid_date = CURDATE() "
                + "WHERE bill_id = ? AND paid = FALSE";
     try (Connection con = DBConnection.getConnection();
          PreparedStatement ps = con.prepareStatement(sql)) {

         ps.setInt(1, billId);
         int rows = ps.executeUpdate();
         if (rows > 0) {
             System.out.println("✅ Bill ID " + billId + " paid successfully!");
             return true;
         } else {
             System.out.println("⚠️  Bill not found or already paid.");
             return false;
         }
     } catch (SQLException e) {
         System.out.println("❌ Error paying bill: " + e.getMessage());
         return false;
     }
 }

 // ─── 7. View Unpaid Bills ────────────────────────────────
 public void viewUnpaidBills() {
     String sql = """
         SELECT b.bill_id, c.name, c.meter_number,
                b.bill_month, b.amount, b.due_date
         FROM bills b
         JOIN consumers c ON b.consumer_id = c.consumer_id
         WHERE b.paid = FALSE
         ORDER BY b.due_date
     """;
     try (Connection con = DBConnection.getConnection();
          Statement st = con.createStatement();
          ResultSet rs = st.executeQuery(sql)) {

         System.out.println("\n╔══════════════════ UNPAID BILLS ══════════════════╗");
         System.out.printf("%-6s %-18s %-10s %-12s %-10s %-12s%n",
                 "BillID", "Consumer", "Meter", "Month", "Amount", "Due Date");
         System.out.println("─".repeat(72));

         boolean found = false;
         while (rs.next()) {
             found = true;
             System.out.printf("%-6d %-18s %-10s %-12s ₹%-9.2f %-12s%n",
                     rs.getInt("bill_id"),
                     rs.getString("name"),
                     rs.getString("meter_number"),
                     rs.getString("bill_month"),
                     rs.getDouble("amount"),
                     rs.getString("due_date"));
         }
         if (!found) System.out.println("  No unpaid bills found.");
         System.out.println("╚══════════════════════════════════════════════════╝");

     } catch (SQLException e) {
         System.out.println("❌ Error: " + e.getMessage());
     }
 }

 // ─── 8. View Consumer's Billing History ──────────────────
 public void viewBillingHistory(int consumerId) {
     String sql = """
         SELECT b.bill_id, b.bill_month, m.units_consumed,
                b.amount, b.paid, b.paid_date, b.due_date
         FROM bills b
         JOIN meter_readings m ON b.reading_id = m.reading_id
         WHERE b.consumer_id = ?
         ORDER BY b.bill_id DESC
     """;
     try (Connection con = DBConnection.getConnection();
          PreparedStatement ps = con.prepareStatement(sql)) {

         ps.setInt(1, consumerId);
         ResultSet rs = ps.executeQuery();

         System.out.println("\n╔══════════════════ BILLING HISTORY ══════════════════╗");
         System.out.printf("%-6s %-14s %-7s %-10s %-5s %-12s %-12s%n",
                 "BillID", "Month", "Units", "Amount", "Paid", "PaidOn", "DueDate");
         System.out.println("─".repeat(72));

         boolean found = false;
         while (rs.next()) {
             found = true;
             System.out.printf("%-6d %-14s %-7d ₹%-9.2f %-5s %-12s %-12s%n",
                     rs.getInt("bill_id"),
                     rs.getString("bill_month"),
                     rs.getInt("units_consumed"),
                     rs.getDouble("amount"),
                     rs.getBoolean("paid") ? "YES" : "NO",
                     rs.getString("paid_date") != null ? rs.getString("paid_date") : "-",
                     rs.getString("due_date"));
         }
         if (!found) System.out.println("  No billing history found for this consumer.");
         System.out.println("╚══════════════════════════════════════════════════════╝");

     } catch (SQLException e) {
         System.out.println("❌ Error: " + e.getMessage());
     }
 }
}