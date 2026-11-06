# account-ledger
### what is this?
- my account ledger capstone project!
- with a ridiculous amount of markdown on it to boot!

### but what is it?
This program takes data from a .csv file formatted like:
``` csv
hh:mm:ss|yyyy-mm-dd|Description|Vendor|Transaction_amount
```
and will allow you to search for it by its individual fields, i.e.
- search by what happened last month
- search by what happened this month
- search in a given time period
- search by transaction amount
As well as
- add new transactions, payments, deposits, etc
### how do I run it?
you will need:
- jdk version 27 or later
- all jdk binaries on your PATH environment variable
#### OR
- [Intellij IDEA](https://www.jetbrains.com/idea/download/?section=windows)

### running in Intellij
1. create an empty java project
2. pull down the source files from github
3. hit the big green button on top when you have the main class open
### running in powershell terminal
1. pull down source files from github
2. either
  - run the powershell script, or
  - in root directory run
``` powershell
javac *.java
jar cfe Account_Ledger.Jar Main *.class
rm *.class
java -jar .\Account_Ledger.Jar
rm Account_Ledger.jar
```
