# Step 1: Use lightweight Java 17 image
FROM eclipse-temurin:17-jre-alpine

# Step 2: Set working directory inside container
WORKDIR /app

# Step 3: Copy the built JAR into container
COPY target/*.jar app.jar

# Step 4: Expose application port (documentation purpose)
EXPOSE 8081

# Step 5: Run Spring Boot app with limited memory
ENTRYPOINT ["java", "-Xms128m", "-Xmx256m", "-jar", "app.jar"]
