# A log4j2 appender for Clojure

This library exposes the appender implementation for log4j2 that calls
a user defined function for each log.

Requires JDK >= 11.

## Getting Started ##

Add dependencies:

```clojure
org.apache.logging.log4j/log4j-api {:mvn/version "2.13.3"}
org.apache.logging.log4j/log4j-core {:mvn/version "2.13.3"}
org.clojure/tools.logging {:mvn/version "1.1.0"}
funcool/log4j2-clojure {:mvn/version "2020.11.23-1"}
```

Configure logging (log4j2.xml):

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="info">
  <Appenders>
    <Console name="main" target="SYSTEM_OUT">
      <PatternLayout pattern="[%d{YYYY-MM-dd HH:mm:ss.SSS}] [%t] %level{length=1} %logger{36} - %msg%n"/>
    </Console>

    <CljFn name="test" ns="some.ns" fn="print-log">
      <PatternLayout pattern="[%d{YYYY-MM-dd HH:mm:ss.SSS}] [%t] %logger{36} - %msg%n"/>
    </CljFn>
  </Appenders>

  <Loggers>
    <Root level="info">
      <AppenderRef ref="main" />
      <AppenderRef ref="test" />
    </Root>
  </Loggers>
</Configuration>
```

This is a simple configuration that logs info (or greater levels) to
the console and to the `CljFn` appender. The `CljFn` appender will try
ti import the `some.ns/print-log` function and call it for each log
entry.

Example:

```clojure
(ns some.ns)

(defn print-log
  [event]
  (println (str event)))
```

The `event` is just a wrapper to the log4j2 LogEvent instance and it implementes IDeref
for obtain the original LogEvent and `.toString()` to format the log using the configured
layout (in this case the PatternLayout).

```clojure
user=> (require '[clojure.tools.logging :as log])
user=> (log/info "foobar")
[2020-11-23 22:12:39.648] [main] user - foobar
```
