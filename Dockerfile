# Use a base image with Java 17 installed
FROM gradle:8.2.1-jdk17

# Set the working directory inside the container
WORKDIR /app

ARG SPRING_PROFILE

# Copy the Spring Boot JAR file into the container
ARG JAR_FILE
COPY build/libs/${JAR_FILE} app.jar

ENV SPRING_PROFILES_ACTIVE $SPRING_PROFILE

# Command to run the Spring Boot application
CMD ["java", "-jar", "app.jar"]

# Expose the port that your Spring Boot app will listen on
EXPOSE 8081

