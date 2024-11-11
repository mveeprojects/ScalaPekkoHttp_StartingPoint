# ScalaPekkoHttp_StartingPoint

This codebase is designed as a simple starting point for a Pekko HTTP application.

## Modules:

### Api

A simple HTTP API that can be easily extended.

### Performance

Basic performance tests written using Gatling to provide a basic level of traffic and therefore sample metrics.

## Prerequisites

### Docker (inc. docker-compose)

Install this by following [the documentation on the Docker website](https://docs.docker.com/engine/install/)

### SBT

If you're using a Unix OS (MacOS/Linux), I have included a `sbtw` file under the `bin` directory at the root of this
project so you can just replace the usage of `sbt` with `./bin/sbtw` where necessary.

If you're using Windows or would prefer to download sbt regardless,
follow [the documentation on the official SBT website](https://www.scala-sbt.org/1.x/docs/Setup.html)

## Creating and running the API with monitoring containers

### Building and running the API and Performance modules along with monitoring containers

An executable file called called`coldstart.sh` has been added to the root of this project to easily complete the following steps on your behalf:

* clean down any API, Performance or Monitoring containers using docker-compose
* build fat JARs of both API and Performance modules
* build docker images of both API and Performance modules
* run the API, Performance and Monitoring containers using docker-compose
    * this automatically triggers the performance tests therefore generating traffic on the API, resulting in metrics
      available to our Monitoring containers immediately!

Run it as shown below.

```shell
./coldstart.sh
```

When you're finished, run the below command from the root of the project to stop the API, Performance and Monitoring
containers

```
docker-compose -f ./docker/docker-compose.yml down
```

### Endpoints

* [API: "Hello, World!" endpoint (...:8080/)](http://localhost:8080).
* [API: metrics endpoint (...:9095/)](http://localhost:9095).
* [Prometheus (...:9090/)](http://localhost:9090).
* [Grafana (...:3000/)](http://localhost:3000) (username: `admin`, password `admin`).
  * [List of dashboards](http://localhost:3000/dashboards).


### Auth path cURL requests

cURL with basic auth
```
curl -v --user name:password localhost:8080/authpath
```

### Very basic PromQL queries for use in Grafana

Show the total number of hits to each endpoint over the past minute.
```
sum(increase(myendpointhits_total[1m])) by (endpoint)
```

Show the rate of hits/transactions per second to each endpoint over the past minute.
```
sum(rate(myendpointhits_total[1m])) by (endpoint)
```

Show the rate of 2xx responses per second over the past minute. 
```
rate(http_server_requests_total{http_status_code="2xx"}[1m])
```

Show the rate of 4xx responses per second over the past minute.
```
rate(http_server_requests_total{http_status_code="4xx"}[1m])
```

Show the rate of requests per status code over the past minute.
```
sum(rate(http_server_requests_total[1m])) by (http_status_code)
```

Show the percentage of all responses that were 404s per
minute.
```
(sum(rate(http_server_requests_total{http_status_code="4xx"}[1m])) / sum(rate(http_server_requests_total[1m]))) * 100
```

### References/Sources

* [Dzone: dockerizing-your-scala-app](https://dzone.com/articles/dockerizing-your-scala-app).
* [SBT docs: multi-project setup](https://www.scala-sbt.org/1.x/docs/Multi-Project.html).
* [Stack Overflow: SBT assembly strategy issue](https://stackoverflow.com/a/58024050/3059314).
* [Medium: Grafana Docker setup](https://medium.com/swlh/easy-grafana-and-docker-compose-setup-d0f6f9fcec13).
  * [GitHub: annea-ai/grafana-infrastructure](https://github.com/annea-ai/grafana-infrastructure).
* [Robust Perception: Rate of sum not sum of rate](https://www.robustperception.io/rate-then-sum-never-sum-then-rate)
* [Timber: PromQL for Humans](https://timber.io/blog/promql-for-humans/).
* [YouTube: PromCon EU 2019: PromQL for Mere Mortals](https://www.youtube.com/watch?v=hTjHuoWxsks).
* [YouTube: Monitoring, the Prometheus Way](https://www.youtube.com/watch?v=PDxcEzu62jk).
* [YouTube: The 4 Types Of Prometheus Metrics (Around 9m 30s for histogram quantile prometheus queries)](https://www.youtube.com/watch?v=nJMRmhbY5hY).
* [YouTube: Prometheus Example Queries (Good explanation of rate())](https://www.youtube.com/watch?v=_nZSrY784sY).
* [YouTube: Grafana Prometheus with Heat Maps | Grafana Heatmap Histograms](https://www.youtube.com/watch?v=6oSlgW6EKK4).
* [Akka docs: HTTP Source Streaming](https://doc.akka.io/docs/akka-http/current/routing-dsl/source-streaming-support.html).
  * [GitHub: Akka HTTP Source Streaming raw code](https://github.com/akka/akka-http/blob/master/akka-http-tests/src/test/scala/akka/http/scaladsl/server/EntityStreamingSpec.scala).
* [Medium: Telemetry with Scala, part 1: OpenTelemetry](https://ivan-kurchenko.medium.com/telemetry-with-scala-part-1-opentelemetry-758c72a136e7)
