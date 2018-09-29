FROM openjdk:8-jre-alpine

MAINTAINER 'Devaprasadh.Xavier <devatherock@gmail.com>'

ENV PLUGIN_DEBUG false
ADD YamlValidator.jar /scripts/YamlValidator.jar
ADD entry-point.sh /scripts/entry-point.sh

ENTRYPOINT sh /scripts/entry-point.sh