# Imagens multi-arch (o Temurin Alpine e' publicado apenas para amd64;
# as tags nao-alpine suportam amd64 e arm64 — necessario em Apple Silicon).
FROM maven:3.9-eclipse-temurin-17 AS build

# Credenciais do Artifactory injetadas no build (nunca commitadas).
# Preferir BuildKit secrets em producao; build-args ficam no historico da imagem.
ARG ARTIFACTORY_URL=https://artifactory.mpms.mp.br/artifactory
ARG ARTIFACTORY_USER
ARG ARTIFACTORY_PASSWORD
ENV ARTIFACTORY_URL=${ARTIFACTORY_URL} \
    ARTIFACTORY_USER=${ARTIFACTORY_USER} \
    ARTIFACTORY_PASSWORD=${ARTIFACTORY_PASSWORD}

# CA do WAF/proxy TLS institucional (FAC.crt) importado no truststore do JDK,
# necessario para acessar repositorios externos (ex.: Maven Central) atras do WAF.
COPY FAC.crt /usr/local/share/ca-certificates/mpms-fac.crt
RUN keytool -importcert -noprompt -alias mpms-fac-waf \
      -file /usr/local/share/ca-certificates/mpms-fac.crt \
      -keystore "$JAVA_HOME/lib/security/cacerts" -storepass changeit

WORKDIR /app
COPY .mvn/settings.xml /root/.m2/settings.xml
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:17-jre
# Mesmo CA no runtime (o app acessa servicos internos atras do WAF, ex.: Keycloak).
COPY FAC.crt /usr/local/share/ca-certificates/mpms-fac.crt
RUN keytool -importcert -noprompt -alias mpms-fac-waf \
      -file /usr/local/share/ca-certificates/mpms-fac.crt \
      -keystore "$JAVA_HOME/lib/security/cacerts" -storepass changeit

# Usuario nao-root (sintaxe Debian/Ubuntu da imagem base nao-alpine).
RUN groupadd -r appgroup && useradd -r -g appgroup appuser
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
USER appuser
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget --spider -q http://localhost:8080/api/exemplo-servico/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
