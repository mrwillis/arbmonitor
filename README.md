# Introduction
This project calls various cryptocurrency APIs using the awesome `xchange` library and tries to find arbitrage opportunities (crypto/crypto) pairs only.
It writes to a file called `opportunites.csv` with the opportunities it finds.


## Building from source
1. First you'll need to make sure you have the latest JDK. You can get that [here](http://www.oracle.com/technetwork/java/javase/downloads/index.html).
2. You'll also need to install Maven, you can get that [here](https://maven.apache.org/install.html)
3. `git clone` this repo, picking the branch of your choice. You can use `git clone -b dev [REPO_URL]` if you want the latest changes. Specifically, on this branch, the Maven dependencies
are using snapshots from the sonatype repository, so they may not be stable.
4. If you're using IntelliJ (use it), just say 'yes' to convert the project to a Maven project,
5. Set the project to target JDK 1.8
6. Set up a run configuration to target Main
7. Run!

## Adding logging
1. To add logging, in your `arbmonitor/src/main/resources` folder, create a file called
`log4j2.xml` (exact name).
2. Read up on `log4j2` [here](https://logging.apache.org/log4j/2.x/) to discover how `log4j2` works. 
3. Add the below or some variation of the below. The below configuration has file logging, e-mail logging, and console logging.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Appenders>
        <SMTP name="Mail" subject="ArbMonitor Log" to="[YOUR-EMAIL-HERE]" from="[YOUR-EMAIL-HERE]"
              smtpHost="smtp.gmail.com" smtpUsername="[YOUR-EMAIL-HERE]" smtpPassword="[YOUR-EMAIL-PW-HERE]"
              smtpPort="465" smtpProtocol="smtps" bufferSize="1">
        </SMTP>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
        </Console>
        <File name="FileAppender" fileName="arbmonitor.log" append="true" bufferedIO="true">
            <PatternLayout>
                <Pattern>%d %p %c [%t] %m%n</Pattern>
            </PatternLayout>
        </File>
    </Appenders>
    <Loggers>
        <Root level="info">
            <AppenderRef ref="Console"/>
            <AppenderRef ref="Mail" />
            <AppenderRef ref="FileAppender"/>
        </Root>
    </Loggers>
</Configuration>
```

## Configuration
You must make a `config.properites` file in your resources folder. Currently the supported options are:

| Property Name      | Values    | Description                                                                            |
|--------------------|-----------|----------------------------------------------------------------------------------------|
| arbitrageThreshold | 0 < x < 1 | Program will log opportunities if the spread (fee adjusted) is greater than this value |


