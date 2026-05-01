FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy project files
COPY . .

# Build the project
RUN ./mvnw clean package -DskipTests

# Run the jar
CMD ["java","-jar","target/*.jar"]