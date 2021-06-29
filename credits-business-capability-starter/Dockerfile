FROM alpine:3.13
RUN apk add --no-cache openjdk8
WORKDIR /opt
ADD build/libs/credits-business-capability-starter-*SNAPSHOT.jar /opt/app.jar
ADD build/resources/main/newrelic/newrelic.jar /opt/newrelic.jar

RUN sh -c 'touch /opt/app.jar'

ENTRYPOINT ["java", "-javaagent:newrelic.jar", "-Xmx256m",  "-Xms128m", "-Dloader.path=/opt", "-Djava.security.egd=file:/dev/./urandom","-jar","app.jar"]

