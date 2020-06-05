FROM openjdk:8-jre
MAINTAINER zjxjwxk zjxjwxk@gmail.com

COPY target/guns-order-0.0.1.jar /cinema/workspace/guns-order-0.0.1.jar
COPY ./entrypoint.sh /cinema/sbin/entrypoint.sh

RUN chmod a+x /cinema/sbin/*.sh

#ENTRYPOINT ["/cinema/sbin/entrypoint.sh"]
ENTRYPOINT ["java", "-jar", "/cinema/workspace/guns-order-0.0.1.jar"]