uh-sql-export
===========

SQL Export

##### Overview
A command-line program to create a SQL export file.

##### Build Tool
First, you need to download and install maven (version 3.2.3+).

##### Java
You'll need a Java JDK to build and run the project (version 1.8).

The files for the project are kept in a code repository,
available from here:

https://github.com/fduckart/uh-sql-export

##### Building
Install the necessary project dependencies:

    $ mvn install

To build a deployable jar file:

    $ mvn clean package

Usage:

    $ java -jar exporter.jar --spring.config.location=exporter.properties

