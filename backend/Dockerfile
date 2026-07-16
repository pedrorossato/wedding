FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN dos2unix mvnw 2>/dev/null; sed -i 's/\r$//' mvnw; chmod +x mvnw && ./mvnw dependency:resolve -B -q
COPY src src
RUN ./mvnw package -DskipTests -B -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]