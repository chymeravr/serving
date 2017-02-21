FROM ubuntu:16.04

ENV BASE=/home/serving

RUN mkdir $BASE

RUN apt-get update

# Common system tools
RUN apt-get install -y vim curl openjdk-8-jre

# Create logging directory
RUN mkdir /var/log/serving/
RUN mkdir /home/serving/config

ARG JAR

# Copy all the project files. Excluded files are present in .dockerignore
COPY $JAR $BASE/
COPY config $BASE/config/

# Change directory to project root
WORKDIR $BASE

RUN ls $BASE
CMD java -jar \
    -Dlogback.configurationFile=config/logback.xml \
    -Dcom.sun.management.jmxremote.port=9010 \
    -Dcom.sun.management.jmxremote.authenticate=false \
    serving-assembly-*-jar-with-dependencies.jar\
    -p $PORT \
    -c config/serving.config

