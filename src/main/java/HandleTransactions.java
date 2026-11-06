import java.io.*;
import java.text.Format;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.lang.IO.println;

public class HandleTransactions {
    private RandomAccessFile read_and_write;

    public HandleTransactions(String filename) throws FileNotFoundException {

        File file = new File(filename);
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        file = null;

        read_and_write = new RandomAccessFile(filename, "rw");
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

    public int numTransactions() throws IOException {
        int counter = 0;
        read_and_write.seek(0);
        while (read_and_write.readLine() != null) {
            counter ++;
        }
        return counter;
    }



    public Transaction getTransaction(int index) throws IndexOutOfBoundsException, InternalError, IOException {
        int num_transactions = this.numTransactions();
        Transaction t = null;

        if (index > num_transactions) {
            throw new IndexOutOfBoundsException("Index "+index+" is out of bounds. This instance has "+num_transactions+" transactions.");
        }

        String transaction;
        try {
            // move to start of file
            read_and_write.seek(0);

            // read lines from the file (each line contains one transaction) until you reach the desired transaction
            for (int i = 0; i < num_transactions; i++) {
                transaction = read_and_write.readLine();

                if (i == index) {

                    String [] fields = transaction.split("\\|");
                    LocalDateTime transaction_time = getTransactionTime(fields);
                    t = new Transaction(transaction_time, fields[2], fields[3], Double.parseDouble(fields[4]));
                    break;
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return t;
    }

    private static LocalDateTime getTransactionTime(String[] fields) {
        if (fields.length != 5) {
            throw new InternalError();
        }

        String [] time = fields[0].split(":");
        if (time.length != 3) {
            throw new InternalError();
        }
        String [] date = fields[1].split("-");
        if (date.length != 3) {
            throw new InternalError();
        }

        int [] HMS = {Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2])};
        int [] YMD = {Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2])};

        return LocalDateTime.of(YMD[0], YMD[1], YMD[2], HMS[0], HMS[1], HMS[2]);
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
