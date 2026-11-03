import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.lang.IO.println;

public class HandleTransactions {
    private String filename;
    private RandomAccessFile read_and_write;

    public HandleTransactions(String filename) throws FileNotFoundException {

        File file = new File(filename);
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        file = null;

        read_and_write = new RandomAccessFile(filename, "rw");
        this.filename = filename;
    }

    private String transactionToCSV_Style(Transaction t) {
        DateTimeFormatter HMS_format = DateTimeFormatter.ofPattern("HH:mm:ss");
        DateTimeFormatter YMD_format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String HMS = t.getWhen_transacted().format(HMS_format);
        String YMD = t.getWhen_transacted().format(YMD_format);
        return String.format("%s|%s|%s|%s|%.2f\n",
                HMS,
                YMD,
                t.getDescription(),
                t.getVendor(),
                t.getAmount());
    }

    public int numTransactions() {

        return 0;
    }

    public Transaction getTransaction(int index) {

        return new Transaction();
    }

    public void newTransaction(double difference, String vendor, String description) throws IOException {
        Transaction t = new Transaction();
        LocalDateTime now = LocalDateTime.now();
        t.setWhen_transacted(now);
        t.setAmount(difference);
        t.setDescription(description);
        t.setVendor(vendor);

        read_and_write.seek(read_and_write.length());
        read_and_write.writeBytes(transactionToCSV_Style(t));
    }
}
