FROM openjdk:19-jdk-oracle
EXPOSE 8085
COPY ./target/timesheet-*.jar /usr/app/
WORKDIR /usr/app
CMD java -jar timesheet-*.jar
