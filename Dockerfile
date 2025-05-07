# Sử dụng JDK 17 làm base image
FROM eclipse-temurin:17-jdk-alpine

# Cài đặt Maven
RUN apk add --no-cache maven

# Tạo thư mục app
WORKDIR /app

# Copy toàn bộ source code vào container
COPY . .

# Build ứng dụng bằng Maven
RUN mvn clean package -DskipTests

# Chạy ứng dụng Spring Boot
CMD ["java", "-jar", "target/MyProject-0.0.1-SNAPSHOT.jar"]
