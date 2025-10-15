# ✅ 1. JDK 환경
FROM openjdk:17-jdk-slim

# ✅ 2. 작업 폴더 설정
WORKDIR /app

# ✅ 3. Gradle 빌드 (의존성 설치 후 JAR 생성)
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew buildFatJar

# ✅ 4. 앱 실행
EXPOSE 8080
CMD ["java", "-jar", "build/libs/random-jeju-api-all.jar"]