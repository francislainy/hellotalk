FROM sonarqube:latest

# Install unzip
RUN apt-get update && apt-get install -y unzip

# Download and install sonar-scanner
RUN curl -L -o /tmp/sonar-scanner.zip https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/sonar-scanner-cli-4.8.0.12008-linux.zip && \
    unzip /tmp/sonar-scanner.zip -d /usr/share && \
    mv /usr/share/sonar-scanner-4.8.0.12008-linux /usr/share/sonar-scanner && \
    ln -s /usr/share/sonar-scanner/bin/sonar-scanner /usr/bin/sonar-scanner && \
    rm /tmp/sonar-scanner.zip

# Set the sonar-scanner executable permissions
RUN chmod +x /usr/bin/sonar-scanner
