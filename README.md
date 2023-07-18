# multipart-camel-reproducer

This is a reproducer for [CAMEL-19568](https://issues.apache.org/jira/browse/CAMEL-19568)

## Run the application

This is a Quarkus Camel application, to start it you can just:

```bash
mvn quarkus:dev
```

The application will start a REST endpoint `GET /` that will return a `multipart/mixed` response containing a text 
and an attached HTML.

```
------=_Part_0_1584025906.1689674736129
Content-Type: application/octet-stream
Content-Transfer-Encoding: binary

Hello Camel
------=_Part_0_1584025906.1689674736129
Content-Type: text/html
Content-Transfer-Encoding: 8bit
Content-Disposition: attachment; filename=report.html

<html><body>Hello Camel</body></html>
------=_Part_0_1584025906.1689674736129--
```

After starting, a timer will make one HTTP request to that endpoint and log the response.

## Configuring the application

The default behaviour is to use HTTP/2 which is the failing scenario. In order to use HTTP/1.1 you can start the application with `-Dversion=1.1`

```
mvn quarkus:dev -Dversion=1.1
```

## Build the application

As a normal Maven application just use the `package` target.

```bash
mvn clean package
```

Then you can run the built application normally.

```bash
java -Dversion=1.1 -jar target/quarkus-app/quarkus-run.jar
```