FROM maven:3.9.8-amazoncorretto-21 AS build

WORKDIR /shopapp

COPY pom.xml .
COPY src ./src

RUN mvn clean package -P dev

FROM amazoncorretto:21.0.4

WORKDIR /shopapp

COPY --from=build /shopapp/target/*.jar shopapp.jar

ENTRYPOINT ["java", "-jar", "shopapp.jar"]
