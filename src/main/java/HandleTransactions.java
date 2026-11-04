import java.io.*;
import java.text.Format;
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

    public int numTransactions() throws IOException {
        int counter = 0;
        while (read_and_write.readLine() != null) {
            counter ++;
        }
        return counter;
    }


    public Transaction getTransaction(int index) throws IndexOutOfBoundsException, InternalError, IOException {
        int num_transactions = this.numTransactions();
        Transaction t = null;

        if (index > num_transactions) {
            throw new IndexOutOfBoundsException();
        }

        String transaction = null;
        try {
            // move to start of file
            read_and_write.seek(0);

            // read lines from the file (each line contains one transaction) until you reach the desired transaction
            for (int i = 0; i < num_transactions; i++) {
                transaction = read_and_write.readLine();

                if (i == index) {

                    String [] fields = transaction.split("\\|");
                    if (fields.length != 5) {
                        throw new InternalError();
                    }

                    LocalDateTime transaction_time = LocalDateTime.parse(fields[0] + Transaction.separator + fields[1],
                            DateTimeFormatter.ofPattern("hh:mm:ss"+Transaction.separator+"yyyy-MM-dd"));

                    t = new Transaction(transaction_time, fields[2], fields[3], Double.parseDouble(fields[4]));
                    break;
                }
            }
        } catch (IOException e) {
            //  for when the file reader has trouble
            throw new RuntimeException(e);
        }

        return t;
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
