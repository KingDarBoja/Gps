# GPS
GPS app that can connect to remote database (AWS RDS) in order to save / read historical records for vehicle tracking, developed on Android Studio. 

First of all, you must download the correct JDBC driver connector based on the sql engine you are using (MySQL, MariaDB, Postgres, Oracle). The tutorial and the URL for each JDBC connector **(MariaDB not included)** can be found at [AWS RDS DB](http://docs.aws.amazon.com/elasticbeanstalk/latest/dg/java-rds.html). Meanwhile, the MariaDB Driver can be found at [MariaDB website](https://downloads.mariadb.org/connector-java/). 

The next step is the bluetooth connection between the App and the vehicle, which I followed this [tutorial](http://blog.lemberg.co.uk/how-guide-obdii-reader-app-development) to make a clickable list of available bluetooth devices close to the app. For the OBD commands, I used the [OBD-II Java API](https://github.com/pires/obd-java-api) to obtain the data (in this case, RPM) from the vehicle. 

Finally, using the **Location Listener** and **Background Task**, managed the database connection and write statements to save the gps location (latitude, longitude), timestamp and rpm every five seconds.

This app was designed for the Final Project of Electronic Design - Electronic Engineering - Universidad del Norte (2017).
