#--------------------------------
#Dockerfile for api-gateway
#--------------------------------

#-------------
#Build Stage
#-------------

#base image
FROM gradle:8.4-jdk17-alpine AS builder

#Copy the application files from the host machine to the image filesystem
COPY --chown=gradle:gradle . /home/gradle/src

#Set the directory within the image, for executing future commands
WORKDIR /home/gradle/src

#Run gradle build within the container - NOT THE HOST MACHINE
RUN gradle build -x test

#----------------
#Package Stage
#----------------
FROM openjdk:17-alpine

RUN mkdir /app

COPY --from=builder /home/gradle/src/build/libs/*.jar /app/api-gateway.jar

EXPOSE 8181

ENTRYPOINT ["java", "-jar", "/app/api-gateway.jar"]
