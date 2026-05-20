# E-Commerce Backend

Spring Boot + PostgreSQL əsaslı e-ticarət backend API-si.

## Texnologiyalar

- Java 21
- Spring Boot 4.0.2
- PostgreSQL
- JWT Authentication (HttpOnly Cookie)
- MapStruct
- Stripe Payment

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

### İşə salma

```bash
./gradlew bootRun
```

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
- **Product** — CRUD, şəkil yükləmə, filtr
- **Category** — CRUD
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