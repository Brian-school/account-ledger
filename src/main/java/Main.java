public class Main {
    /**
     *  structure it like:
     *  UI is separate
     *  all changes to the data go through calls to an "interface" class, which accesses the CSV file
     *  and sends data in the form of a ledger transaction object to be parsed as necessary.
     *  All data is stored in the CSV file and only temporary copies of said data are stored in objects when
     *  they are needed for reading by the program, stored to be written to the file, etc.
     */
    static void main() {

    }
}
