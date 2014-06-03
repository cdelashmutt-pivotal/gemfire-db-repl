# GemFire Replicator #

This project provides a Spring Integration flow that is dynamically configured to provide change capture on a set of relational database tables, and push the changes made to those tables to GemFire.  This could be useful to automate syncing data between a relational database and GemFire proactively, as well as potentially using GemFire as a replication mechanism for the relational database.

To use the replicator, modify the application.properties file to the proper settings for your database and GemFire server.  Review the provided application.properties file for examples.