# daf-research

This is the DAF-Research RI prototype for C1 capability.

* Implementation Guide can be found at : http://hl7.org/fhir/us/daf-research/STU2/index.html 
* Implementation Guidance can be found at : http://hl7.org/fhir/us/daf-research/STU2/guidance.html 

Steps to recreate the project and run it:

1. Use your IDE (Tested with Eclipse) and import the Maven Project using the pom.xml. 
(If you are using git, then do a git clone/pull and then import the project into your workspace).
2. Do a Maven Clean. (mvn clean)
3. Do a Maven install. (mvn install)
4. The Maven Install command will produce a JAR file.

5. Running the Project.

* Use a command line interface and type "java -jar daf-research-0.0.1-SNAPSHOT.jar -s site"
* The system will run and will create a database called PCORnetDB in C:\\Temp

6. Inspecting the database: To inspect the database, if you have not installed derby please follow derby installation instruction below.

* Connect to the database using verification instructions below and verify the C1 capability by executing select statements. 

Note: The PCORnetDB is present in C:\Temp, if not it is easier if you are in the directory where the DB is present before you execute this command.

*** Verification Instructions:

* C:\Temp>%DERBY_INSTALL%\bin\ij  
* connect 'jdbc:derby:PCORnetDB';
* select * from demographic;
* select * from condition;
* select * from task;


** DERBY INSTALLATION INSTRUCTIONS

a. Install the derby tools following instructions at: 
http://db.apache.org/derby/papers/DerbyTut/install_software.html#derby_configure 

Specifically execute the following sections.
* Download Derby
* Install Derby
* Set DERBY_INSTALL
* Configure Embedded Derby (Optional)
* Verify Derby.

b. You can look at http://db.apache.org/derby/papers/DerbyTut/ij_intro.html#ij_setup  for instructions on how to use ij tool to access derby.


