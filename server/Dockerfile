FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY . .
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/lumen-coin-api.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", \
  "-Xmx200m", "-Xms64m", \
  "-XX:MaxMetaspaceSize=120m", \
  "-XX:+UseSerialGC", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
