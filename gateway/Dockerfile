FROM eclipse-temurin:17-jdk-alpine
COPY ./target/gateway-0.0.1.jar app.jar
ENV CONFIG_SERVER_HOST=config-server
ENV ADMIN_CONSOLE_HOST=admin-console
ENV ACCOUNTS_PROVIDER_HOST=accounts-provider
ENV EMAILS_PROVIDER_HOST=sensor-emails-provider
ENV RANGE_PROVIDER_HOST=sensor-range-provider
ENTRYPOINT [ "java", "-jar", "app.jar" ]