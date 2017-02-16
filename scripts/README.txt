README STRUCTURE
0. Introduction
1. System requirements
2. Installation guide
3. Configure application to start
4. Issues, tips & tricks


==== 0. INTRODUCTION ===
If you read this that means you want to try the XmlParser in action!
XmlParser is open-source project based on JDK 8 and Gradle technologies.
Last version always is available on GitHub: 

Shortly the main idea is to just burn your CPU with tons of XML files and other stuff as soon as possible.
So if you would like to try it - go ahead!



=== 1. System requirements ===
- JDK 8 installed in system
- PostgreSQL 9.4 or newer
- Windows 7 or newer / GNU Linux compatible system


=== 2. INSTALLATION GUIDE ===
2.1 Installation from source
a. Checkout the Project from GitHub
b. Run Gradle task "jar" like 
    gradlew jar
or
    gradle jar
    if you don't use the Gradle Wrapper from project
and voila! You have application directory in build-one.
c. Copy application directory wherever you want, go into dir and start runMe-script (depends on your platform)

2.2 Installation of already built application
a. Just copy application-directory wherever you want and start runMe-script sh/bat - depends on your platform


2.3 Create the schema in PostgreSQL
Run create.sql script from project on desired PostgreSQL instance

=== 3. CONFIGURE APPLICATION TO START ===
Application provides the clean help output and still if you don't like to read output in console (like me) 
here you are the basic configuration parameters:

- to start without configuration file run application
  > runMe --src=/path/to/source/xml/directory --dst=/directory/where/place/processed --failed=/directory/where/place/failed --url=db_connenction_url --username=db_username --password=db_password --period=monitoring_period_in_seconds

- to start application with config file use the 
  > runMe --config=/path/to/config/file

NB! You can specify and parameters and config BUT! Config file has highest priority so will be used instead of other parameters. Keep this in mind!

>>> Here you are the example of config file:
connection.url=jdbc:postgresql://localhost:5432/postgres
connection.username=my_user
connection.password=my_password
config.src=D:/temp/src
config.dst=D:/temp/dst
config.failed=D:/temp/failed
config.threads=8
config.period=60

Feel free to specify threads quantity from 1 to 8. If you will break out this range then this awesome smart application will correct quantity to 4.


=== 4. ISSUES, TIPS AND TRICKS ===
If you struggle with run-scripts then you can just run application like
   java -jar xmlparser-1.0-SNAPSHOT.jar blah-blah

If you have other issues please contact me.

Have fun!
