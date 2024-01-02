FROM maven:3.9.6-eclipse-temurin-17-alpine as MAVEN

COPY . /fta-fmea
WORKDIR /fta-fmea/ontology-generator
RUN mvn clean install

FROM gradle:8.4-jdk17-alpine as GRADLE
COPY . /fta-fmea
WORKDIR /fta-fmea

COPY --from=MAVEN /fta-fmea/src/main/generated/cz/cvut/kbss/analysis/util/Vocabulary.java \
                   ./src/main/generated/cz/cvut/kbss/analysis/util/Vocabulary.java

RUN ./gradlew clean bootJar

FROM eclipse-temurin:17-jdk-alpine as runtime

COPY --from=GRADLE /fta-fmea/build/libs/fta-fmea-*.jar /fta-fmea.jar

EXPOSE 8080
CMD ["java", "-jar", "/fta-fmea.jar"]