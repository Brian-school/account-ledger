import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.IO.print;
import static java.lang.IO.println;

public class MenuOptions {
    private String [] options;
    private boolean yes_or_no;

    public MenuOptions(boolean yes_or_no) {
        this.yes_or_no = yes_or_no;
        options = null;
    }

    public MenuOptions(String [] options) {
        this.options = options;
        this.yes_or_no = false;
    }

    public void printMenu () {
        if (options != null) {
            for (int i = 0; i < options.length; i++) {
                println((i + 1) + " - " + options[i]);
            }
        }
    }

    public int menuPromptUser(Scanner sc) {
        while (true) {
            printMenu();
            if (!yes_or_no) {
                print("Choice -> ");
                final int choice = Integer.parseInt(sc.nextLine().trim());
                if (choice < 0 || choice > options.length) {
                    if (options.length == 1) {
                        println("Option for selection is 1. Please select 1.");
                    } else {
                        println("Options are between 1 and "+options.length+". Please choose again.");
                    }
                } else {
                    return choice;
                }
            } else {
                print("Y/N -> ");
                return Truthiness.toInt(sc.nextLine().trim().equalsIgnoreCase("y"));
            }
        }
    }
}
