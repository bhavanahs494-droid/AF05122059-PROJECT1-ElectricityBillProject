package electricity;

public class Consumer {

 private int    consumerId;
 private String name;
 private String address;
 private String meterNumber;
 private String connectionType;  // DOMESTIC / COMMERCIAL / INDUSTRIAL

 // ─── Constructor ─────────────────────────────────────────
 public Consumer(int consumerId, String name, String address,
                 String meterNumber, String connectionType) {
     this.consumerId     = consumerId;
     this.name           = name;
     this.address        = address;
     this.meterNumber    = meterNumber;
     this.connectionType = connectionType;
 }

 // ─── Getters ──────────────────────────────────────────────
 public int    getConsumerId()     { return consumerId; }
 public String getName()           { return name; }
 public String getAddress()        { return address; }
 public String getMeterNumber()    { return meterNumber; }
 public String getConnectionType() { return connectionType; }

 // ─── Setters ──────────────────────────────────────────────
 public void setConsumerId(int consumerId)         { this.consumerId = consumerId; }
 public void setName(String name)                  { this.name = name; }
 public void setAddress(String address)            { this.address = address; }
 public void setMeterNumber(String meterNumber)    { this.meterNumber = meterNumber; }
 public void setConnectionType(String type)        { this.connectionType = type; }

 // ─── Display ─────────────────────────────────────────────
 @Override
 public String toString() {
     return String.format("ID:%-4d | %-20s | Meter: %-10s | Type: %-12s | Address: %s",
             consumerId, name, meterNumber, connectionType, address);
 }
}