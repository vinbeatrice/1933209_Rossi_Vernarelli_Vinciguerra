# ---------- BUILD STAGE ----------
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -Dmaven.test.skip=true


# ---------- RUN STAGE ----------
FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=build /app/target/presentationapp.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java","-jar","app.jar"]