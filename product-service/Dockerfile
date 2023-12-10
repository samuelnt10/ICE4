# --------------------
# Dockerfile for product-service
# --------------------

# --------------------
# BUILD stage
# --------------------

# base image
FROM gradle:8.3-jdk17 AS builder

# Copy the application files from the host machine to the image filesystem
COPY --chown=gradle:gradle . /home/gradle/src

# set the directory within the image, for executing furure commands
WORKDIR /home/gradle/src

# Run gradle build within the container - NOT the host machine
RUN gradle build -x test


# --------------------
# PACKAGE stage
# --------------------

FROM openjdk:17

RUN mkdir /app

COPY --from=builder /home/gradle/src/build/libs/*.jar /app/product-service.jar

ENV MONGO_DB_USERNAME=rootadmin \
    MONGO_DB_PWD=password

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "/app/product-service.jar"]
