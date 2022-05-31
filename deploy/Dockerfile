FROM tomcat:8-jdk11

RUN rm -rf /usr/local/tomcat/webapps/*
COPY ./build/libs/* /usr/local/tomcat/webapps/fta-fmea.war

CMD ["catalina.sh","run"]
