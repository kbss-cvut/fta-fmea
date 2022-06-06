FROM maven:3.6.1-jdk-11 as MAVEN

COPY . ./fta-fmea
WORKDIR ./fta-fmea/ontology-generator
RUN mvn clean install

FROM gradle:6.4-jdk11
COPY . ./fta-fmea
WORKDIR ./fta-fmea

COPY --from=MAVEN /fta-fmea/src/main/generated/cz/cvut/kbss/analysis/util/Vocabulary.java \
                   ./src/main/generated/cz/cvut/kbss/analysis/util/Vocabulary.java


RUN ./gradlew clean build


# TODO: ADD runtime dependencies