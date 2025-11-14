# Use OpenJDK 17
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

# Install maven
RUN apt-get update && apt-get install -y maven

# Copy Maven files
COPY pom.xml .
COPY src ./src

# Build Spring Boot app
RUN mvn clean package -DskipTests

# Runtime image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy built jar
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Run the application
ENTRYPOINT ["java","-jar","app.jar"]
