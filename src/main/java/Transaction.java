import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private LocalDateTime when_transacted;
    private String description;
    private String vendor;
    private double amount;

    public Transaction(LocalDateTime when_transacted, String description, String vendor, double amount) {
        this.when_transacted = when_transacted;
        this.description = description;
        this.vendor = vendor;
        this.amount = amount;
    }

    public Transaction() {
        when_transacted = null;
        description = null;
        vendor = null;
        amount = 0.0;
    }

    @Override
    public String toString() {
        DateTimeFormatter HMS_format = DateTimeFormatter.ofPattern("HH:mm:ss");
        DateTimeFormatter YMD_format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String HMS = when_transacted.format(HMS_format);
        String YMD = when_transacted.format(YMD_format);
        return String.format("%s|%s|%30s|%30s|%.2f\n",
                HMS,
                YMD,
                this.description,
                this.vendor,
                this.amount);
    }

    public double getAmount() {
        return amount;
    }

    public String getVendor() {
        return vendor;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getWhen_transacted() {
        return when_transacted;
    }

    public void setWhen_transacted(LocalDateTime when_transacted) {
        this.when_transacted = when_transacted;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
