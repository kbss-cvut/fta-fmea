FROM maven:3.6.1-jdk-11
COPY ./ontology-generator ./ontology-generator
WORKDIR ./ontology-generator
RUN mvn clean install

FROM gradle:6.4-jdk11
WORKDIR ./
COPY . .

RUN ./gradlew clean build


# TODO: ADD runtime dependencies