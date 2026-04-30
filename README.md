⚡ Electricity Bill Management System
A Java console-based application connected to MySQL that manages electricity consumers, tracks meter readings, auto-generates bills using slab-based pricing, and handles bill payments.

📁 Project Structure
ElectricityBillProject/
└── src/
    └── electricity/
        ├── Main.java          # Entry point, menu-driven console UI
        ├── Consumer.java      # Model class for consumer data
        ├── BillService.java   # Business logic & input validation
        ├── ConsumerDAO.java   # Database operations (CRUD + billing)
        └── DBConnection.java  # MySQL JDBC connection setup

🗄️ Database Schema
Database name: electricity_db
consumers
ColumnTypeDescriptionconsumer_idINT (PK)Auto-incremented IDnameVARCHARConsumer's full nameaddressVARCHARConsumer's addressmeter_numberVARCHARUnique meter number (e.g. M-10001)connection_typeENUMDOMESTIC, COMMERCIAL, or INDUSTRIAL
meter_readings
ColumnTypeDescriptionreading_idINT (PK)Auto-incremented IDconsumer_idINT (FK)References consumersreading_monthVARCHARe.g. April-2025previous_readingINTPrevious meter readingcurrent_readingINTCurrent meter readingunits_consumedINTAuto-calculated (current − previous)
bills
ColumnTypeDescriptionbill_idINT (PK)Auto-incremented IDconsumer_idINT (FK)References consumersreading_idINT (FK)References meter_readingsbill_monthVARCHARBilling monthamountDOUBLETotal bill amount (₹)due_dateDATEPayment due datepaidBOOLEANPayment status (default: FALSE)paid_dateDATEDate of payment

💡 Billing Rate Slabs
DOMESTIC
Units ConsumedRate per Unit0 – 100 units₹3.50101 – 300 units₹5.00301+ units₹7.00
COMMERCIAL
Flat rate: ₹8.00 per unit
INDUSTRIAL
Flat rate: ₹6.50 per unit

₹50 fixed service charge is added to all connection types.


⚙️ Setup Instructions
Prerequisites

Java 17+ (uses switch expressions and text blocks)
MySQL 8.0+
MySQL Connector/J (JDBC Driver)
Eclipse IDE (recommended) or any Java IDE

Steps
1. Clone the repository
bashgit clone https://github.com/your-username/ElectricityBillProject.git
cd ElectricityBillProject
2. Create the MySQL database
sqlCREATE DATABASE electricity_db;
USE electricity_db;

CREATE TABLE consumers (
    consumer_id     INT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    address         VARCHAR(200),
    meter_number    VARCHAR(20) UNIQUE NOT NULL,
    connection_type ENUM('DOMESTIC','COMMERCIAL','INDUSTRIAL') NOT NULL
);

CREATE TABLE meter_readings (
    reading_id       INT AUTO_INCREMENT PRIMARY KEY,
    consumer_id      INT NOT NULL,
    reading_month    VARCHAR(20) NOT NULL,
    previous_reading INT NOT NULL,
    current_reading  INT NOT NULL,
    units_consumed   INT GENERATED ALWAYS AS (current_reading - previous_reading) STORED,
    FOREIGN KEY (consumer_id) REFERENCES consumers(consumer_id) ON DELETE CASCADE
);

CREATE TABLE bills (
    bill_id     INT AUTO_INCREMENT PRIMARY KEY,
    consumer_id INT NOT NULL,
    reading_id  INT NOT NULL,
    bill_month  VARCHAR(20) NOT NULL,
    amount      DOUBLE NOT NULL,
    due_date    DATE NOT NULL,
    paid        BOOLEAN DEFAULT FALSE,
    paid_date   DATE,
    FOREIGN KEY (consumer_id) REFERENCES consumers(consumer_id) ON DELETE CASCADE,
    FOREIGN KEY (reading_id)  REFERENCES meter_readings(reading_id) ON DELETE CASCADE
);
3. Configure the database connection
Open src/electricity/DBConnection.java and update your credentials:
javaprivate static final String URL      = "jdbc:mysql://localhost:3306/electricity_db";
private static final String USERNAME = "root";
private static final String PASSWORD = "your_password";  // Change this
4. Add MySQL JDBC Driver

Download mysql-connector-j-x.x.x.jar from MySQL Downloads
In Eclipse: Right-click project → Build Path → Add External JARs → select the JAR

5. Run the project
Run Main.java as a Java Application.

🖥️ Menu Options
╔══════════════════════════════════════╗
║      ELECTRICITY BILL SYSTEM         ║
╠══════════════════════════════════════╣
║  1. Add Consumer                     ║
║  2. View All Consumers               ║
║  3. Search Consumer by Meter No      ║
║  4. Add Meter Reading (Generates Bill)║
║  5. Pay Bill                         ║
║  6. View All Unpaid Bills            ║
║  7. View Consumer Billing History    ║
║  8. View Rate Slabs                  ║
║  9. Exit                             ║
╚══════════════════════════════════════╝

🚀 Features

👤 Consumer Registration — Add consumers with name, address, meter number, and connection type (DOMESTIC / COMMERCIAL / INDUSTRIAL)
🔍 Meter-based Search — Quickly look up any consumer by their meter number
📊 Auto Bill Generation — Enter meter readings and the bill is instantly calculated using slab-based pricing
💳 Bill Payment — Mark unpaid bills as paid with a single entry
📋 Billing History — View complete month-wise billing history per consumer
⚠️ Input Validation — All inputs are validated before hitting the database
🔒 Transaction Safety — Meter readings and bills are inserted within a single DB transaction


🛠️ Tech Stack
LayerTechnologyLanguageJava 17+DatabaseMySQL 8.0ConnectivityJDBC (MySQL Connector/J)IDEEclipsePatternDAO (Data Access Object)

📄 License
This project is open-source and available under the MIT License.
