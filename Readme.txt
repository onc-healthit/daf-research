This is the DAF-Research RI prototype for C1 capability.

Implementation Guide can be found at : http://hl7.org/fhir/us/daf-research/STU2/index.html 
Implementation Guidance can be found at : http://hl7.org/fhir/us/daf-research/STU2/guidance.html 

Steps to recreate the project and run it:

1. Use your IDE (Tested with Eclipse) and import the Maven Project using the pom.xml. 
(If you are using git, then do a git clone/pull and then import the project into your workspace).
2. Do a Maven Clean. (mvn clean)
3. Do a Maven install. (mvn install)
4. The Maven Install command will produce a JAR file.

4. Running the Project.

a. Use a command line interface and type "java -jar daf-research-ri-0.0.1-SNAPSHOT.jar -s site"
b. The system will run and will create a database called PCORnetDB in C:\\Temp

5. Inspecting the database:

a. Install the derby tools following instructions at: 
http://db.apache.org/derby/papers/DerbyTut/install_software.html#derby_configure 
b. Connect to the database using instructions

http://db.apache.org/derby/papers/DerbyTut/ij_intro.html#ij_setup 
C:\Temp>%DERBY_INSTALL%\bin\ij
connect 'jdbc:derby:PCORnetDB';
select * from demographic;
select * from condition;
select * from task;

Specifically execute the following sections.
a. Download Derby
b. Install Derby
c. Set DERBY_INSTALL
d. Configure Embedded Derby (Optional)
e. Verify Derby.