# bamboobank
A basic bank application

Requirements:
Java 11

To run:
At src, to compile, run
javac -d .\out .\com\bambank\Main.java
to run the progrm, run
java -cp out com.bambank.Main

There is a default user of "admin/admin".
You can log in, register a new account, see amount of money you hold, see transactions made, and send money to another account.

Assumptions and comments
The structure is meant to replicate a web client / server setup, with ui.Interface as the web interface stand in, and store.StoreService as the API
I opted out of implementing an actual database, instead stored everything into store.Database as variables
The interface is pretty messy because Java is not ideal for such things
