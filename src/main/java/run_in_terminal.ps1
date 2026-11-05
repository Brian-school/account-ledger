javac *.java
jar cfe Account_Ledger.Jar Main *.class
rm *.class
java -jar .\Account_Ledger.Jar
rm Account_Ledger.jar