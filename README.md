# E-Commerce Backend

Spring Boot + PostgreSQL əsaslı e-ticarət backend API-si.
Modular Monolith

## Texnologiyalar

- Java 21
- Spring Boot 4.0.2
- PostgreSQL
- JWT Authentication (HttpOnly Cookie)
- MapStruct
- Stripe Payment
- Cloudinary (şəkil yükləmə)
- Docker
- Swagger / OpenAPI

## Qurulum

### Tələblər

- Java 21
- PostgreSQL

### Environment Variables
DB_URL=jdbc:postgresql://localhost:5432/ecomm_db
DB_USERNAME=postgres
DB_PASSWORD=your_password
JWT_SECRET_KEY=your_secret_key
MAIL_USERNAME=your_email
MAIL_PASSWORD=your_app_password
STRIPE_SECRET_KEY=your_stripe_secret_key
STRIPE_WEBHOOK_SECRET=your_stripe_webhook_secret
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
CORS_ALLOWED_ORIGINS=http://localhost:3000

### İşə salma

```bash
./gradlew bootRun
```

### Docker ilə işə salma

```bash
docker-compose up --build
```

## API Sənədləşməsi

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## API Endpoints

| Modul | Endpoint |
|-------|----------|
| Auth | `/api/v1/auth/**` |
| Users | `/api/v1/users/**` |
| Products | `/api/v1/products/**` |
| Categories | `/api/v1/categories/**` |
| Cart | `/api/v1/cart/**` |
| Orders | `/api/v1/orders/**` |
| Addresses | `/api/v1/addresses/**` |
| Payments | `/api/v1/payments/**` |
| Reviews | `/api/v1/reviews/**` |

## Modullar

- **Auth** — JWT, 2FA, refresh token, şifrə sıfırlama
- **User** — profil, rol idarəetməsi
- **Product** — CRUD, şəkil yükləmə (Cloudinary), filtr
- **Category** — CRUD, parent-child ağac strukturu
- **Cart** — dynamic cart, real-time qiymət yeniləmə
- **Order** — sifariş idarəetməsi, status izləmə
- **Payment** — Stripe inteqrasiyası, scalable gateway arxitekturası
- **Review** — yalnız alınmış məhsula rəy, ortalama reytinq
- **Address** — çoxlu ünvan, default ünvan

## Təhlükəsizlik

- JWT HttpOnly Cookie
- BCrypt şifrələmə
- 2FA (Email OTP)
- Brute-force qoruması
- Role-based access control (USER, STAFF, ADMIN)
- CORS konfiqurasiyası
- Rate Limiting (60 sorğu / 15 dəqiqə)