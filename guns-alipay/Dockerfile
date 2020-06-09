FROM openjdk:8-jre
MAINTAINER zjxjwxk zjxjwxk@gmail.com

USER root
COPY target/guns-alipay-0.0.1.jar /cinema/workspace/guns-alipay-0.0.1.jar
COPY ./entrypoint.sh /cinema/sbin/entrypoint.sh

RUN chmod a+x /cinema/sbin/*.sh

RUN mkdir qrcode

#ENTRYPOINT ["/cinema/sbin/entrypoint.sh"]
ENTRYPOINT ["java", "-jar", "/cinema/workspace/guns-alipay-0.0.1.jar"]