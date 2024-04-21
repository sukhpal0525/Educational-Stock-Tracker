**1) Initial Project Setup**
- Install and run Intellij IDEA (Community or Ultimate).
- Extract Portfolio_Tracker.zip. Open it in Intellij IDEA.
- Download JDK 17 in Intellij IDEA.
- Click "Setup SDK" -> "Download JDK". (https://gyazo.com/048de35a8993e6e2b68ab63bae533976)
- Select these settings: https://gyazo.com/03687cdaf3539c86b70a8e483e1c14ec
- Wait for it to install.



**2) Database Setup**

a) Install MySQL Server (https://dev.mysql.com/downloads/file/?id=526408).
1) Run the installer.
2) "Choosing a Setup Type": Select "Custom".
3) "Select Products and Features": Select MySQL Server 8.0.36 (click the green arrow). Expand "Applications" and do the same for MySQL Workbench. (https://gyazo.com/47341b8ff88bd351691af0e399d860e6)
4) "Installation": Click execute.
5) Product Configuration: Click next.
- Use defaults for "Group Replication", "Types and Networking", "Authentication Method".
- In "Accounts and "Roles", enter "root" for MySQL Root Password. (Do not include quotations). https://gyazo.com/a0dfe8a2db361ab4a3ac106a2b6d4ebd
- Also use defaults for "Windows Service".
- Click "execute" in "Apply Configuration".


b) Creating the database in MySQL Workbench 8.0
1) Click the plus button next to "MySQL Connections".
2) Enter "localhost" for the connection name.
3) Enter username as "root". Click "store in vault" and enter "root" for the password. https://gyazo.com/df98a8bc359528b6c7217914ee0c080a
4) Press "OK".
5) Click the "localhost" configuration you just made https://gyazo.com/225b73fd2fd8e508bfcafc4138369832
6) In the Schemas tab (left), right-click -> "Create Schema". Call it "aston-stockapp".



**3) Run the App**
a) Run the app in Intellij IDEA
1) Unzip the project .zip.
2) Import the project.
3) Check if JDK is set to 17 in Project Structure -> Project.
4) Right click the java class "StockAppApplication.java". Click "Run".
5) In your browser's address bar, go to: http://localhost:8080/
