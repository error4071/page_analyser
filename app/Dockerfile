FROM eclipse-temurin:20-jdk

WORKDIR /app

COPY / .

RUN ./gradlew installDist

EXPOSE 7070

CMD ./build/install/app/bin/app
