import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.IO.print;
import static java.lang.IO.println;
import static java.lang.Thread.sleep;

/**
 *  structure it like:
 *  UI is separate
 *  all changes to the data go through calls to an "interface" class, which accesses the CSV file
 *  and sends data in the form of a ledger transaction object to be parsed as necessary.
 *  All data is stored in the CSV file and only temporary copies of said data are stored in objects when
 *  they are needed for reading by the program, stored to be written to the file, etc.
 */
public class Main {
    private Scanner sc;
    private HandleTransactions ht;

    private final String go_back_one = "Return One Screen";
    // menu strings
    private final String [] mainMenuOptions = {"Add Deposit", "Make Payment", "View Ledger", "EXIT Application"};
    private final String [] ledgerMainMenuOptions = {"All", "Deposits", "Payments", "Reports", "Custom Search", go_back_one};
    private final String [] ledgerReportsMenOptions = {"Month to Date", "Previous Month", "Year to Date", "Previous Year", "Search by Vendor", go_back_one};

    // menu option objects
    private MenuOptions mainMenu;
    private MenuOptions mainLedgerMenu;
    private MenuOptions ledgerReportMenu;

    private MenuOptions yesNo;
    void main() {

        initialize();

        try {
            startup();
        } catch (Exception e) {
            println("Exiting...");
            return;
        }

        while (true) {
            try {
                switch (mainMenu.menuPromptUser(sc)) {
                    case 1 -> accountTransaction(true);
                    case 2 -> accountTransaction(false);
                    case 3 -> viewLedger();
                    case 4 -> {
                        println("Goodbye!");
                        return;
                    }
                    default -> println("ERROR");
                }
            } catch (IOException e) {
                println("There was a problem reading the file. Exiting...");
                break;
            }
        }

    }

    private void initialize() {
        // scanner
        sc = new Scanner(System.in);

        // menuOptions objects
        mainMenu = new MenuOptions(mainMenuOptions);
        mainLedgerMenu = new MenuOptions(ledgerMainMenuOptions);
        ledgerReportMenu = new MenuOptions(ledgerReportsMenOptions);

        yesNo = new MenuOptions(true);
    }

    private void startup() throws Exception {
        println("Welcome to Frenchy Softworks's account ledger Software.");
        final String file_extension = ".csv";
        boolean quit = false;
        while (!quit) {

            print("What is the name of the file containing your account information?\n" +
                    "Name of file -> ");
            String filename = sc.nextLine().trim();

            if (!filename.endsWith(file_extension)) {
                println("Does this file end with a \""+file_extension+"\" extension?");
                if(Truthiness.toBool(yesNo.menuPromptUser(sc))) {
                    filename = filename.concat(file_extension);
                } else {
                    println("Sorry. This application only accepts files which are formatted as a "+file_extension+" file.");
                    continue;
                }
            }

            println("Connecting to your account...");
            try {
                ht = new HandleTransactions(filename);
            } catch (FileNotFoundException e) {
                print("The filename you provided does not exist in the current directory, which is\n"+
                        System.getProperty("user.dir")+
                        "\nPlease check to see if the name is correct, and make sure that your file is in the aforementioned\n" +
                        "working directory of the program.\n\n" +
                        "Try again?\n");
                if (Truthiness.toBool(yesNo.menuPromptUser(sc))) {
                    continue;
                } else {
                    throw new Exception();
                }
            }

            println("Connected to your account successfully!");
            quit = true;
        }
    }

    private void accountTransaction(boolean deposit) {
        String desc = null;
        if (deposit) {
            desc = "Deposit";
        }
        String vendor;
        while (true) {
            print("How much are you transacting?\nAmount -> ");
            double amount;
            try {
                amount = Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                println("Invalid input, sorry. Please input a number.");
                continue;
            }

            if (amount < 0.0 && deposit) {
                println("You cannot make a negative deposit. Try again.");
                continue;
            } else if (amount > 0.0 && !deposit) {
                println("Payments must be submitted as a negative value. Try again");
                continue;
            } else if (amount == 0.0) {
                println("You cannot transact no money. Try again.");
                continue;
            }

            if (!deposit) {
                print("What are you paying for?\nDescription -> ");
                desc = sc.nextLine().trim();
            }

            if (deposit) {
                print("What or who is the source of this money?\nVendor -> ");
            } else {
                print("To whom is the payment being made out?\nWhom -> ");
            }

            vendor = sc.nextLine().trim();
            try {
                ht.newTransaction(amount, vendor, desc);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            println("Transaction recorded. Would you like to add another?");
            if (!Truthiness.toBool(yesNo.menuPromptUser(sc))) {
                break;
            }
        }
    }

    private void viewLedger() throws IOException {
        while (true) {
            println("Ledger Options:");
            switch (mainLedgerMenu.menuPromptUser(sc)) {
                case 1 -> displayFullLedger();
                case 2 -> displayDepositsOrPaymentsLedger(true);
                case 3 -> displayDepositsOrPaymentsLedger(false);
                case 4 -> ledgerReports();
                case 5 -> customSearch();
                case 6 -> {
                    return;
                }
            }
        }
    }

    private int promptNumberInRange(String member, String of_set, int min, int max) {
        int retval;
        while (true) {
            print("Choose a "+member+" of the "+of_set+".\n"+member+" -> ");
            try {
                retval = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                println("Sorry, I didn't understand that.");
                continue;
            }

            if (retval < min || retval > max) {
                println("Invalid "+member+". Should be a number between "+min+" and "+max+", inclusive.");
            } else {
                break;
            }
        }
        return retval;
    }

    private LocalDate promptDate() {

        int day = promptNumberInRange("Day", "Month", 1, 31);

        int month = promptNumberInRange("Month", "Year", 1, 12);

        int year = promptNumberInRange("Year", "last decade",
                LocalDate.now().minusYears(10L).getYear(),
                LocalDate.now().getYear());

        return LocalDate.of(year, month, day);
    }

    private boolean isWithinAPenny(double val1, double val2) {
        // floating point comparisons are really tricky. this is to make sure things within a penny's worth will match,
        // since floating point representation of small numbers can be inexact.
        double difference1 = val1 - val2;
        double difference2 = val2 - val1;

        return (difference1 > 0.0 && difference1 <= 0.01) || (difference2 > 0.0 && difference2 <= 0.01);
    }

    private void customSearch() {
        LocalDate start_date;
        LocalDate end_date;
        String description;
        String vendor;
        double amount;


        while (true) {
            start_date = null;
            end_date = null;
            description = "";
            vendor = "";
            amount = 0.0;

            MenuOptions.clearScreen();

            println("Search by Start/End date?");
            if (Truthiness.toBool(yesNo.menuPromptUser(sc))) {

                println("Input the start date you would like to search from.");
                start_date = promptDate();

                println("Input the end date you would like to search from.");
                end_date = promptDate();

                if (start_date.isAfter(end_date)) {
                    println("The start date must occur before the end date. Try again?");
                    if (Truthiness.toBool(yesNo.menuPromptUser(sc))) {
                        continue;
                    } else {
                        break;
                    }
                }
            }

            println("Search by description?");
            if (Truthiness.toBool(yesNo.menuPromptUser(sc))) {
                print("Input the description to search by.\nDescription -> ");
                description = sc.nextLine().trim();
            }

            if (description.equals("1337")) {
                try {
                    Cube.drawCube();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            println("Search by vendor?");
            if (Truthiness.toBool(yesNo.menuPromptUser(sc))) {
                print("Input the vendor to search by.\nVendor -> ");
                vendor = sc.nextLine().trim();
            }

            println("Search by amount?");
            if (Truthiness.toBool(yesNo.menuPromptUser(sc))) {
                print("Input the amount to search by.\nAmount -> ");
                amount = Double.parseDouble(sc.nextLine().trim());
            }

            if (start_date == null && description.isEmpty() && vendor.isEmpty() && amount == 0.0) {
                println("You didn't choose anything to search by!\nTry again?");
                if (!Truthiness.toBool(yesNo.menuPromptUser(sc))) {
                    break;
                }
            }

            println("Matching records:");

            try {
                int num_transactions = ht.numTransactions();
                for (int i = 0; i < num_transactions; i++) {
                    Transaction t = ht.getTransaction(i);
                    boolean filtered_match = false;

                    if (start_date != null) {
                        if (t.getWhen_transacted().isAfter(start_date.atStartOfDay()) &&
                            t.getWhen_transacted().isBefore(end_date.atStartOfDay())) {
                            filtered_match = true;
                        }
                    }

                    if (!description.isEmpty()) {
                        if (t.getDescription().equalsIgnoreCase(description)) {
                            filtered_match = true;
                        }
                    }

                    if (!vendor.isEmpty()) {
                        if (t.getVendor().equalsIgnoreCase(vendor)) {
                            filtered_match = true;
                        }
                    }

                    if (amount != 0.0) {
                        if (isWithinAPenny(t.getAmount(), amount)) {
                            // see implementation of the comparison function above to view rationale
                            filtered_match = true;
                        }
                    }

                    if (filtered_match) {
                        println(t);
                    }
                }

                println("Search again?");

                if (!Truthiness.toBool(yesNo.menuPromptUser(sc))) {
                    break;
                }
            } catch (IOException e) {
                println("Error occurred reading file!");
                throw new RuntimeException(e);
            }
        }
    }

    private void displayFullLedger () throws IOException {
        while (true) {
            int num_transactions = ht.numTransactions();
            for (int i = 0; i < num_transactions; i++) {
                println(ht.getTransaction(i));
            }

            println("Would you like to view these again?");
            if (!Truthiness.toBool(yesNo.menuPromptUser(sc))) {
                break;
            }
        }
    }

    private void displayDepositsOrPaymentsLedger (boolean deposit) throws IOException {
        int num_transactions = ht.numTransactions();
        while (true) {
            for (int i = 0; i < num_transactions; i++) {
                Transaction t = ht.getTransaction(i);

                if (t.getAmount() < 0 && deposit) {
                    // payment, not deposit. skip
                    continue;
                } else if (t.getAmount() > 0 && !deposit) {
                    // deposit, not payment. skip
                    continue;
                }

                println(t);
            }

            println("Display this menu again?");
            if (!Truthiness.toBool(yesNo.menuPromptUser(sc))) {
                break;
            }
        }
    }

    private void ledgerReports() throws IOException {
        while (true) {
            switch (ledgerReportMenu.menuPromptUser(sc)) {
                case 1 -> displayMTDLedger();
                case 2 -> displayPrevMonLedger();
                case 3 -> displayYTDLedger();
                case 4 -> displayPrevYearLedger();
                case 5 -> searchByVendorLedger();
                case 6 -> {
                    return;
                }
            }
        }
    }

    private void displayMTDLedger() throws IOException {
        int num_transactions = ht.numTransactions();
        Month month = LocalDateTime.now().getMonth();
        int year = LocalDateTime.now().getYear();
        while (true) {
            boolean result_found = false;
            for (int i = 0; i < num_transactions; i++) {
                Transaction t = ht.getTransaction(i);

                if (t.getWhen_transacted().getYear() == year && t.getWhen_transacted().getMonth().equals(month)) {
                    println(t);
                    result_found = true;
                }
            }

            if (!result_found) {
                println("Search yielded no results.");
            }

            println("Display this menu again?");
            if (!Truthiness.toBool(yesNo.menuPromptUser(sc))) {
                break;
            }
        }
    }


    private void displayPrevMonLedger () throws IOException {
        LocalDateTime last_month = LocalDateTime.now().minusMonths(1);
        int num_transactions = ht.numTransactions();
        while (true) {
            boolean result_found = false;

            for (int i = 0; i < num_transactions; i++) {
                Transaction t = ht.getTransaction(i);

                if(t.getWhen_transacted().getMonth().equals(last_month.getMonth()) &&
                        t.getWhen_transacted().getYear() == last_month.getYear()) {
                    println(t);
                    result_found = true;
                }
            }

            if (!result_found) {
                println("Search yielded no results.");
            }

            println("Display this menu again?");
            if (!Truthiness.toBool(yesNo.menuPromptUser(sc))) {
                break;
            }
        }
    }

    private void displayYTDLedger() throws IOException {
        LocalDateTime now = LocalDateTime.now();
        int num_transactions = ht.numTransactions();
        while (true) {
            boolean result_found = false;

            for (int i = 0; i < num_transactions; i++) {
                Transaction t = ht.getTransaction(i);

                if (t.getWhen_transacted().getYear() == now.getYear()) {
                    println(t);
                    result_found = true;
                }
            }

            if (!result_found) {
                println("Search yielded no results.");
            }

            println("Display this menu again?");
            if (!Truthiness.toBool(yesNo.menuPromptUser(sc))) {
                break;
            }
        }
    }

    private void displayPrevYearLedger () throws IOException {
        LocalDateTime last_year = LocalDateTime.now().minusYears(1);
        int num_transactions = ht.numTransactions();
        while (true) {
            boolean result_found = false;
            for (int i = 0; i < num_transactions; i++) {
                Transaction t = ht.getTransaction(i);

                if (t.getWhen_transacted().getYear() == last_year.getYear()) {
                    println(t);
                    result_found = true;
                }
            }

            if (!result_found) {
                println("Search yielded no results.");
            }

            println("Display this menu again?");
            if (!Truthiness.toBool(yesNo.menuPromptUser(sc))) {
                break;
            }
        }
    }

    private void searchByVendorLedger () throws IOException {
        int num_transactions = ht.numTransactions();
        while (true) {
            print("What is the name of the vendor you wish to search for?\nChoice -> ");
            String vendor = sc.nextLine().trim();
            boolean result_found = false;
            for (int i = 0; i < num_transactions; i++) {
                Transaction t = ht.getTransaction(i);

                if (t.getVendor().equalsIgnoreCase(vendor)) {
                    println(t);
                    result_found = true;
                }
            }

            if (!result_found) {
                println("Search yielded no results.");
            }

            println("Search again?");
            if (!Truthiness.toBool(yesNo.menuPromptUser(sc))) {
                break;
            }
        }
    }
}