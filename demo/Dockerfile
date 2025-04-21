# 使用 Java 17 為基底（Render 支援 Java 17）
FROM openjdk:17

# 設定 app 目錄
WORKDIR /app

# 複製編譯後的 jar 檔到容器
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar

# 開放 port 8080
EXPOSE 8080

# 啟動指令
ENTRYPOINT ["java", "-jar", "app.jar"]
