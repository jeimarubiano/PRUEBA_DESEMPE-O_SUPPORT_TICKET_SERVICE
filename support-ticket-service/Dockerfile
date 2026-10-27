FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY support-ticket-service ./support-ticket-service
COPY technician-dispatch-service ./technician-dispatch-service
RUN mvn -q -pl support-ticket-service -am -DskipTests package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/support-ticket-service/target/support-ticket-service-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
