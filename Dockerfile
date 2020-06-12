FROM lucksoft/tomcat:9-jdk8
ENV TZ Asia/Shanghai

WORKDIR /usr/local/tomcat

RUN rm -rf /usr/local/tomcat/webapps/*

ARG WAR_FILE

COPY server.xml /usr/local/tomcat/conf/

COPY target/${WAR_FILE} /usr/local/tomcat/webapps/

RUN mv /usr/local/tomcat/webapps/${WAR_FILE} /usr/local/tomcat/webapps/ROOT.war

CMD ["catalina.sh", "run"]
