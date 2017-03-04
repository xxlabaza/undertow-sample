# Undertow smaple

A tiny REST service powered by pure PostgreSQL JDBC, Undertow and GSON only, which **takes just 3.6 Mb** as a *fat*-jar.

## Features overview

* [Access logging](https://github.com/xxlabaza/undertow-sample/blob/master/src/main/java/com/xxlabaza/test/undertow/Main.java#L42) - simple access logging;

* [JDCB wrapper](https://github.com/xxlabaza/undertow-sample/blob/master/src/main/java/com/xxlabaza/test/undertow/util/SessionUtil.java) - sugar syntax wrapper around **JDBC**-driver;

* [Schema migration](https://github.com/xxlabaza/undertow-sample/blob/master/src/main/java/com/xxlabaza/test/undertow/util/MigrationUtil.java) - database migration util inspired by [flyway](https://flywaydb.org);

* [Exception handling](https://github.com/xxlabaza/undertow-sample/blob/master/src/main/java/com/xxlabaza/test/undertow/ExceptionHandlers.java) - wrapper around routing handlers, which allows catch and handle service different exceptions;

* [JSON encode/decode](https://github.com/xxlabaza/undertow-sample/blob/master/src/main/java/com/xxlabaza/test/undertow/util/JsonUtil.java) - simple wrapper for serializing and deserializing JSON requests/responses;

* [Simple tests](https://github.com/xxlabaza/undertow-sample/blob/master/test.sh) - **bash**-script, which starts [PostgreSQL](https://github.com/xxlabaza/undertow-sample/tree/master/src/test/docker), the REST service and [httpie](https://httpie.org) test-requests.
