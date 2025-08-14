FROM maven:3.9.9-eclipse-temurin-21-alpine AS maven

COPY . /fta-fmea
WORKDIR /fta-fmea/ontology-generator
RUN mvn clean install

FROM gradle:8.5-jdk21-alpine AS gradle
COPY . /fta-fmea
WORKDIR /fta-fmea

COPY --from=maven /fta-fmea/src/main/generated/cz/cvut/kbss/analysis/util/Vocabulary.java \
                   ./src/main/generated/cz/cvut/kbss/analysis/util/Vocabulary.java

RUN ./gradlew clean bootJar

FROM eclipse-temurin:21-jdk-alpine AS runtime

COPY --from=gradle /fta-fmea/build/libs/fta-fmea-*.jar /fta-fmea.jar

# create entrypoint script
COPY --chmod=755 <<'EOF' /entrypoint.sh
#!/bin/sh
exec java $JAVA_OPTS -jar /fta-fmea.jar
EOF

EXPOSE 8080

ENV JAVA_OPTS=""

ENTRYPOINT ["/entrypoint.sh"]