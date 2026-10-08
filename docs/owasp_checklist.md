# Autoevaluación de Seguridad: OWASP Top 10 - BellaCiao API

Hemos revisado la arquitectura actual de la aplicación contra el estándar de seguridad de OWASP Top 10. A continuación se presentan los resultados de nuestro proyecto:

| OWASP Top 10 | Estado | Justificación técnica |
| :--- | :---: | :--- |
| **A1 - Broken Access Control** | ✅ CUMPLE | Toda la API está protegida con `JwtAuthenticationFilter`. Las restricciones se definieron en `SecurityConfig` mapeando cada verbo HTTP y endpoint a los roles específicos (`hasRole`, `hasAnyRole`), bloqueando el acceso indebido. |
| **A2 - Cryptographic Failures** | ✅ CUMPLE | Las contraseñas en DB usan hash dinámico unidireccional con `BCryptPasswordEncoder`. Los tokens JWT están firmados con `HMAC-SHA256` utilizando una clave robusta en Base64. El tráfico de la aplicación se cifró mediante SSL/TLS (`https` en el puerto `8443`). |
| **A3 - Injection** | ✅ CUMPLE | Las consultas a la base de datos se manejan exclusivamente a través de **Spring Data JPA** (`UsuarioRepository`, etc.) o Hibernate, los cuales parametriza las consultas nativamente y previenen la inyección SQL. |
| **A4 - Insecure Design** | ✅ CUMPLE | Implementamos el patrón arquitectónico DTO y MapStruct (`MesaEntityMapper`). Las contraseñas e información interna jamás escapan por un JSON de respuesta, ya que se filtra al transformar de Entities a DTOs. |
| **A5 - Security Misconfig** | ✅ CUMPLE | En `GlobalExceptionHandler` capturamos explícitamente excepciones fatales devolviendo errores genéricos de tipo `ErrorResponseDTO` sin exponer el *Stack Trace* al cliente, mitigando la filtración de arquitectura subyacente. |
| **A6 - Outdated Components** | ✅ CUMPLE | El proyecto ha sido actualizado con dependencias estables y soportadas, usando la versión moderna `jjwt 0.12.3` recomendada. |
| **A7 - Authentication Fail** | ⚠️ PARCIAL | Nuestros Tokens JWT sí tienen expiración estricta de 1 hora. Sin embargo, en un futuro cercano podríamos necesitar implementar "Rate Limiting" con la librería Bucket4j para limitar intentos de fuerza bruta en `/login`. |
| **A8 - Integrity Failures** | ✅ CUMPLE | Las firmas de los JWT se validan obligatoriamente (`Jwts.parser().verifyWith(getKey())`) y rechazan cualquier alteración. Además las dependencias las descargamos exclusivamente de Maven Central. |
| **A9 - Logging Failures** | ✅ CUMPLE | Utilizamos `SLF4J` y `Lombok`. En nuestro `GlobalExceptionHandler` implementamos un `log.warn` en cada intento fallido de acceso `AccessDeniedException (403)` para que la administración del sistema detecte intrusos. |
| **A10 - SSRF** | ✅ CUMPLE | La API actual recibe IDs numéricos y DTOs de texto, no hace resoluciones de URLs enviadas por el cliente, evitando la manipulación de lado del servidor (SSRF). |

---
**Conclusión:** 
La API cumple rigurosamente con los altos estándares descritos en el Paso 8 de la Guía S10. Está lista para despliegue en ambientes de pruebas controlados.
