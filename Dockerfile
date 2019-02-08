FROM ubuntu:16.04

EXPOSE 8080

RUN apt-get update && apt install -y git && apt-get install -y default-jdk && apt install -y maven
RUN git clone https://github.com/valtterikodisto/xmltocsv.git
WORKDIR ./xmltocsv
RUN mvn install
CMD mvn spring-boot:run
