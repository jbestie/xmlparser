# README STRUCTURE
0. Introduction
1. Installation guide
2. Configure application to start
3. Issues, tips & tricks 


## 0. INTRODUCTION
If you read this that means you want to try the XmlParser in action!
XmlParser is open-source project based on JDK 8 and Gradle technologies.
Last version always is available on GitHub here! 

Shortly the main idea is to just burn your CPU with tons of XML files and other stuff as soon as possible.
So if you would like to try it - go ahead!

Special thanks to my MKYoung, Google, documentation and StackOverflow. Thanks a lot!

## 1. INSTALLATION GUIDE
### 1.1 Installation from source
* Checkout the Project from GitHub
* Run Gradle task "jar" like 
  > gradle jar
* Voila! You have **application** directory in build-one.
* Copy application directory wherever you want, go into dir and start runMe-script (depends on your platform)

### 1.2 Installation of already built application
* Just copy application-directory wherever you want and start runMe-script sh/bat - depends on your platform

**NB!** You should have defined the **JAVA_HOME** variable in your environment, have permissions to specified directories and feel good.

## 2. CONFIGURE APPLICATION TO START
Application provides the clean help output and still if you don't like to read output in console (like me) 
here you are the basic configuration parameters:

- to start without configuration file run application
  > runMe --src=/path/to/source/xml/directory --dst=/directory/where/place/processed --failed=/directory/where/place/failed --url=db_connenction_url --username=db_username --password=db_password --period=monitoring_period_in_seconds

- to start application with config file use the 
  > runMe --config=/path/to/config/file

**NB!** You can specify and parameters and config BUT! Config file has highest priority so will be used instead of other parameters. Keep this in mind!

 Here you are the example of config file:
>connection.url=jdbc:postgresql://localhost:5432/postgres
connection.username=my_user
connection.password=my_password
config.src=D:/temp/src
config.dst=D:/temp/dst
config.failed=D:/temp/failed
config.threads=8
config.period=60

Feel free to specify threads quantity from 1 to 8. If you will break out this range then this awesome smart application will correct quantity to 4.


The structure of incoming XML file is similar to this
``` 
<Entry>
	<content>Содержимое записи</content>
	<creationDate>2014-01-01 00:00:00</creationDate> 
</Entry>
```
## 3. ISSUES, TIPS AND TRICKS 
* If you struggle with run-scripts without any luck then you can just run application like
> java -jar xmlparser-1.0-SNAPSHOT.jar --program_parameters

* If you have other issues please contact me.

Have fun!

