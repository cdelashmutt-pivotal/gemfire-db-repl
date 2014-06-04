# GemFire Replicator #

## Description ##
This project provides a Spring Integration flow that is dynamically configured to provide change data capture on a set of relational database tables, and push the changes made to those tables to GemFire.  This could be useful to automate syncing data between a relational database and GemFire proactively, as well as potentially using GemFire as a replication mechanism for the relational database.

Currently this project is somewhat dependent on SQL Server, as the scripts for creating triggers and queue tables use TSQL.

## Build ##
To assemble the replicator, call "mvn package" to create the "target/gemfirerepl-*-dist.zip" file which contains code, scripts, and a configuration file you can use to control the replication process.

## Run ##
To use the replicator, extract the gemfirerepl-*-dist.zip file to the source server you want to replicate from, and modify the application.properties file to the proper settings for your database and GemFire server.  Launch using the gemfirerepl.bat, or gemfirerepl.sh scripts.

If you need to specify additional libraries for the java.library.path (SQL Server Integrated Authentication, for example), set the JAVA_LIBRARY_PATH environment variable before running gemfirerepl.bat or gemfirerepl.sh.