FROM eclipse-temurin:21-jdk

# Instalar dependencias y repositorio de Google Chrome
RUN apt-get update && apt-get install -y wget gnupg curl unzip \
    && curl -fsSL https://dl.google.com/linux/linux_signing_key.pub | gpg --dearmor -o /etc/apt/trusted.gpg.d/google.gpg \
    && echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" \
       > /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update \
    && apt-get install -y \
       google-chrome-stable \
       chromium-driver \
       xvfb \
       libxi6 libnss3 libxss1 libatk-bridge2.0-0 libgtk-3-0 fonts-liberation \
    && rm -rf /var/lib/apt/lists/*


# Copiar el JAR a la imagen
COPY target/selenium-rag-api-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto de Spring Boot
EXPOSE 80

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app.jar"]
