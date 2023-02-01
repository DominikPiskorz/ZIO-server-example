# An example ZIO API Server
A simple local CRUD API written in Scala using ZIO 2, http4s and doobie.
Uses tapir to generate Swagger documentation.

Can be run via docker-compose along with PostgreSQL database containing some test data.
Flyway is used for database migration.

## How to run
First, compile API and build docker image:

`sbt api/Docker/publishLocal`

Then launch docker-compose:

`docker-compose up`

And that's it, you can now open swagger and test API by launching `0.0.0.0:8080/docs` in your browser.
