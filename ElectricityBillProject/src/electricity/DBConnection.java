package electricity;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

 // ✅ Full Database Connection Path
 private static final String URL      = "jdbc:mysql://localhost:3306/electricity_db";
 private static final String USERNAME = "root";
 private static final String PASSWORD = "Root@123";   // 🔁 Change this to your MySQL password

 // Returns a live Connection object
 public static Connection getConnection() {
     Connection con = null;
     try {
         Class.forName("com.mysql.cj.jdbc.Driver");
         con = DriverManager.getConnection(URL, USERNAME, PASSWORD);
     } catch (ClassNotFoundException e) {
         System.out.println("❌ Driver not found: " + e.getMessage());
     } catch (SQLException e) {
         System.out.println("❌ DB Connection Failed: " + e.getMessage());
     }
     return con;
 }
}