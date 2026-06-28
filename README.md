# Gestion de Guias de Despacho

Backend Spring Boot para la actividad de Semana 6: sistema de gestion de pedidos y generacion de guias de despacho con endpoints securitizados, carga/descarga de archivos en S3 y preparacion para API Gateway + Azure AD B2C.

El proyecto sigue el estandar usado en `CloudNativeS4`: Java 21, Spring Boot 3.5.x, H2 persistente en `./data`, `schema.sql` para estructura, `DataInitializer` para datos demo, Docker multi-stage y workflow de GitHub Actions para DockerHub + EC2.

## Roles

- `GESTION_GUIAS`: permite crear guias, subir archivos a S3, actualizar, eliminar y consultar.
- `DESCARGA_GUIAS`: permite solo descargar guias mediante `GET /api/guias/{id}/descargar`.

Los roles se leen principalmente desde el claim JWT `extension_Role`, por ejemplo `"extension_Role": "GESTION_GUIAS"`, y se convierten a authorities `ROLE_*` de Spring Security. Tambien se mantiene compatibilidad con `extension_role`, `role`, `roles` y `extension_Roles`.

## Endpoints

| Metodo | Ruta | Rol |
| --- | --- | --- |
| POST | `/api/guias` | `GESTION_GUIAS` |
| POST | `/api/guias/{id}/archivo` | `GESTION_GUIAS` |
| GET | `/api/guias/{id}/descargar` | `DESCARGA_GUIAS` o `GESTION_GUIAS` |
| PUT | `/api/guias/{id}` | `GESTION_GUIAS` |
| DELETE | `/api/guias/{id}` | `GESTION_GUIAS` |
| GET | `/api/guias?transportista=...&fecha=YYYY-MM-DD` | `GESTION_GUIAS` |

## Ejecutar local

Configura el issuer real de Azure AD B2C:

```bash
cp .env.example .env
```

Luego construye y ejecuta la imagen:

```bash
docker build -t gestion-guias-despacho:local .
docker run --rm -p 8080:8080 --env-file .env -v "$(pwd)/data:/app/data" gestion-guias-despacho:local
```

Health check:

```bash
curl http://localhost:8080/actuator/health
```

## Variables principales

- `AZURE_AD_B2C_ISSUER_URI`: issuer de la politica/sign-in flow de Azure AD B2C.
- `AZURE_AD_B2C_JWK_SET_URI`: URL de llaves publicas de la politica Azure AD B2C.
- `AWS_REGION`: region AWS.
- `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY` y `AWS_SESSION_TOKEN`: credenciales para S3 en AWS Academy.
- `S3_BUCKET`: bucket donde se suben las guias generadas.

## GitHub Actions

El workflow `.github/workflows/main.yml` ejecuta tests y, al hacer push a `main` o ejecutarlo manualmente, construye la imagen Docker, la publica en DockerHub y despliega el contenedor en EC2.

Secrets requeridos en GitHub:

- `DOCKERHUB_USERNAME`
- `DOCKERHUB_TOKEN`
- `EC2_SSH_KEY`
- `USER_SERVER`
- `EC2_HOST`
- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`
- `AWS_SESSION_TOKEN`
- `S3_BUCKET`
- `AZURE_AD_B2C_ISSUER_URI`
- `AZURE_AD_B2C_JWK_SET_URI`

## Ejemplo de creacion de guia

```bash
curl -X POST http://localhost:8080/api/guias \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "transportista": "Transporte Norte",
    "destinatario": "Cliente Demo",
    "direccionDestino": "Av. Siempre Viva 123",
    "fechaDespacho": "2026-06-27",
    "detallePedido": "Pedido 1001 con 3 bultos"
  }'
```

## API Gateway

En AWS API Gateway se deben registrar las rutas anteriores apuntando a la URL publica del backend desplegado en EC2. Para cumplir la guia, protege el API Gateway con el authorizer JWT/OIDC conectado al tenant de Azure AD B2C y valida los mismos roles usados por el backend.
