# An example ZIO API Server


## How to run
First, compile api and build docker image:
`sbt api/Docker/publishLocal`
Then launch docker-compose:
`docker-compose up`
And that's it, you can now open swagger and test API by launching `0.0.0.0:8080/docs` in your browser.
