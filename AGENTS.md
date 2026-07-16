# AGENTS.md — Site de Casamento Pedro & Giovana

## Stack
- **Frontend:** Angular 22 + SSR (Angular Universal) em `frontend/`
- **Backend:** Java 21 + Spring Boot 4.1 (Maven, JAR) em `backend/`
- **Banco:** PostgreSQL 16 (Flyway migrations)
- **Armazenamento:** AWS S3 (fotos e imagens de presentes)
- **Pagamentos:** Stripe (cartão, Pix, Boleto)
- **Auth:** JWT (credenciais admin via env vars), senha criptografada AES-256-GCM no login (chave compartilhada `LOGIN_ENCRYPTION_KEY`)
- **Dev:** Docker Compose (`docker compose up`)
- **Deploy:** Frontend na Vercel, Backend JAR em Oracle Cloud

## Estrutura

```
wedding/
├── frontend/                         # Angular 22 + SSR
│   └── src/app/
│       ├── core/                     # Services, guards, interceptors
│       ├── shared/                   # Componentes reutilizaveis, design system
│       └── features/
│           ├── home/                 # Publica - pagina inicial
│           ├── invite/               # Publica - convite UUID
│           ├── gifts/                # Publica - lista presentes
│           ├── payment/              # Publica - retorno pagamento
│           └── admin/
│               ├── auth/             # Login admin
│               ├── dashboard/        # Estatisticas
│               ├── event/            # Config evento
│               ├── guests/           # CRUD convidados
│               ├── gifts-admin/      # CRUD presentes
│               └── gallery/          # Gerenciar galeria
├── backend/                          # Java 21 + Spring Boot
│   └── src/main/java/com/pedrogio/wedding/
│       ├── config/                   # CorsConfig, SecurityConfig, WeddingProperties
│       ├── auth/                     # Login, JWT filter
│       ├── event/                    # EventConfig CRUD
│       ├── guest/                    # Guest CRUD + UUID invite
│       ├── gift/                     # Gift + GiftPurchase CRUD
│       ├── gallery/                  # GalleryPhoto CRUD
│       ├── payment/                  # Stripe integration + webhook
│       ├── stats/                    # Dashboard stats
│       └── infra/                    # HealthController
├── docker-compose.yml
├── .env.example
└── IMPLEMENTATION_PLAN.md
```

## Como rodar

```bash
# Dev local completo (DB + API)
cp .env.example .env
docker compose up

# Apenas frontend
cd frontend && npm start           # http://localhost:4200

# Apenas backend (requer PostgreSQL rodando)
cd backend && mvnw spring-boot:run  # http://localhost:8080
```

## Como buildar/testar

```bash
# Frontend
cd frontend && npm run build        # SSR build (browser + server)

# Backend
cd backend && mvnw compile          # Compile
cd backend && mvnw test             # Tests
cd backend && mvnw package -DskipTests  # JAR
```

## Variáveis de ambiente

Copiar `.env.example` para `.env`. Principais:

| Variável | Default | Descrição |
|----------|---------|-----------|
| `DB_URL` | `jdbc:postgresql://localhost:5432/wedding` | JDBC URL |
| `ADMIN_USERNAME` | `admin` | Login admin |
| `ADMIN_PASSWORD` | `admin123` | Senha admin |
| `JWT_SECRET` | — | Chave secreta JWT (mín. 256 bits) |
| `AWS_REGION` | `us-east-1` | Região S3 |
| `AWS_BUCKET` | `wedding-gallery` | Bucket S3 |
| `STRIPE_SECRET_KEY` | — | Chave secreta Stripe |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:4200` | Origens CORS |

## Convenções

- **Commits:** convencionais (`feat:`, `fix:`, etc.), em português, atômicos
- **Frontend:** SCSS, design system 100% custom (sem Material/Bootstrap), mobile-first
- **Backend:** controllers no pacote da feature, services/repositories nos mesmos pacotes
- **API:** prefixo `/api/`; admin requer JWT em `/api/admin/**`; público em `/api/event`, `/api/invite/**`, `/api/gifts`
- **Segurança:** validações de data sempre no backend, nunca confiar só no frontend
- **Lombok:** permitido no backend (getters/setters/constructors)
- **Banco:** Snake case (gerado automaticamente pelo Hibernate a partir de camelCase)

## Status atual

| Commit | Descrição | Status |
|--------|-----------|--------|
| 1 | scaffold Angular 22 + SSR | ✅ |
| 2 | scaffold Spring Boot + Maven | ✅ |
| 3 | docker-compose dev | ✅ |
| 4 | migrations + entidades JPA | ✅ |
| 5 | auth JWT + env vars | ✅ |
| 6 | CRUD evento | ✅ |
| 7 | CRUD convidados | ✅ |
| 8 | confirmação de presença | ✅ |
| 9 | CRUD presentes + gift_purchases | ✅ |
| 10 | CRUD galeria | ✅ |
| 5-27 | ... | ⬜ |

## Atualizar AGENTS.md

Ao final de cada commit, atualizar o status acima e adicionar/remover informações relevantes.

**IMPORTANTE:** Sempre ler este arquivo no início de uma nova sessão para entender o estado atual do projeto sem precisar re-ler todos os arquivos.
