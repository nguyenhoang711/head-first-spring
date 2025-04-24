# Dùng JDK làm base image
FROM openjdk:21-jdk-slim

# Cài đặt Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Thư mục làm việc trong container
WORKDIR /app

# Copy mã nguồn ban đầu
COPY . .

# Expose port
EXPOSE 8080

# Chạy ứng dụng ở chế độ dev với Maven
CMD ["mvn", "spring-boot:run"]