# Configuración de SonarCloud para FleetGuard360

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=ElKev118_FleetGuard-FE-2025-2-QA&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=ElKev118_FleetGuard-FE-2025-2-QA)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=ElKev118_FleetGuard-FE-2025-2-QA&metric=coverage)](https://sonarcloud.io/summary/new_code?id=ElKev118_FleetGuard-FE-2025-2-QA)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=ElKev118_FleetGuard-FE-2025-2-QA&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=ElKev118_FleetGuard-FE-2025-2-QA)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=ElKev118_FleetGuard-FE-2025-2-QA&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=ElKev118_FleetGuard-FE-2025-2-QA)

## Análisis de Código Estático

Este proyecto está configurado con SonarCloud para análisis automático de calidad de código.

### Configuración Local

Para ejecutar el análisis localmente:

```bash
mvn clean verify sonar:sonar \
  -Dsonar.projectKey=ElKev118_FleetGuard-FE-2025-2-QA \
  -Dsonar.organization=fe2025-2 \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.login=YOUR_SONAR_TOKEN
```

### Cobertura de Código con JaCoCo

Para generar el reporte de cobertura:

```bash
mvn clean test
```

El reporte se generará en: `target/site/jacoco/index.html`

### CI/CD

El análisis se ejecuta automáticamente en cada push y pull request a las ramas `main` y `develop`.

### Métricas Principales

- **Quality Gate**: Validación de estándares de calidad
- **Code Coverage**: Cobertura de pruebas unitarias
- **Security Hotspots**: Puntos críticos de seguridad
- **Code Smells**: Problemas de mantenibilidad
- **Bugs**: Errores potenciales
- **Vulnerabilities**: Vulnerabilidades de seguridad
