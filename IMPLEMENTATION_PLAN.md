# Plano de Implementação — Site de Casamento Pedro & Giovana

## Stack Tecnológica

- **Frontend:** Angular 22 + Angular Universal (SSR)
- **Backend:** Java 21 + Spring Boot (Maven, artefato JAR)
- **Banco:** PostgreSQL (migrations via Flyway)
- **Armazenamento:** AWS S3 (fotos galeria + imagens presentes)
- **Pagamentos:** Stripe (cartão crédito + Pix + Boleto)
- **Autenticação:** JWT (credenciais admin por env vars)
- **Dev local:** Docker Compose (PostgreSQL + backend)
- **Deploy:** Vercel (frontend) / Oracle Cloud (backend JAR)

## Estrutura de Pastas

```
wedding/
├── frontend/                    # Angular 22 + SSR
│   ├── src/
│   │   ├── app/
│   │   │   ├── core/            # Services, guards, interceptors
│   │   │   ├── shared/          # Componentes reutilizáveis, design system
│   │   │   ├── features/
│   │   │   │   ├── home/        # Página inicial (pública)
│   │   │   │   ├── invite/      # Convite UUID (pública)
│   │   │   │   ├── gifts/       # Lista de presentes (pública)
│   │   │   │   ├── payment/     # Retorno pagamento (pública)
│   │   │   │   └── admin/       # Área administrativa
│   │   │   │       ├── auth/        # Login
│   │   │   │       ├── dashboard/   # Estatísticas
│   │   │   │       ├── event/       # Configuração do evento
│   │   │   │       ├── guests/      # CRUD convidados
│   │   │   │       ├── gifts-admin/ # CRUD presentes
│   │   │   │       └── gallery/     # Gerenciar galeria
│   │   │   └── ...
│   │   └── server.ts            # SSR
│   └── vercel.json
├── backend/
│   └── src/main/java/.../
│       ├── config/              # Security, S3, Stripe, CORS, Flyway
│       ├── auth/                # Login, JWT filter, DTOs
│       ├── event/               # EventConfig entity + controller + service
│       ├── guest/               # Guest entity + CRUD + UUID invite
│       ├── gift/                # Gift + GiftPurchase entities + CRUD
│       ├── gallery/             # GalleryPhoto entity + CRUD
│       ├── payment/             # Stripe integration + webhook
│       └── stats/               # Dashboard stats endpoint
├── docker-compose.yml
├── .env.example
└── IMPLEMENTATION_PLAN.md
```

## Modelo de Dados

### event_config
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| id | SERIAL PK | |
| wedding_date | TIMESTAMP | Data do casamento |
| rsvp_deadline | TIMESTAMP | Data limite confirmação |
| created_at | TIMESTAMP | |
| updated_at | TIMESTAMP | |

### guests
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| id | SERIAL PK | |
| name | VARCHAR | Nome do convidado |
| uuid | UUID UNIQUE | Link único do convite |
| confirmed | BOOLEAN nullable | null=pendente, true=confirmado, false=recusou |
| confirmed_at | TIMESTAMP nullable | |
| created_at | TIMESTAMP | |
| updated_at | TIMESTAMP | |

### gifts
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| id | SERIAL PK | |
| name | VARCHAR | Nome do presente |
| description | TEXT | |
| value | DECIMAL | Valor em R$ |
| image_url | VARCHAR | URL pública S3 |
| s3_key | VARCHAR | Chave no S3 |
| created_at | TIMESTAMP | |
| updated_at | TIMESTAMP | |

### gift_purchases
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| id | SERIAL PK | |
| gift_id | FK -> gifts.id | |
| guest_id | FK -> guests.id | |
| paid | BOOLEAN default false | |
| payment_intent_id | VARCHAR | ID do PaymentIntent Stripe |
| created_at | TIMESTAMP | |
| updated_at | TIMESTAMP | |

### gallery_photos
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| id | SERIAL PK | |
| s3_key | VARCHAR | |
| image_url | VARCHAR | URL pública S3 |
| sort_order | INTEGER | Ordem de exibição |
| created_at | TIMESTAMP | |

## Endpoints da API

### Públicos
| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/event` | Dados do evento (data, deadline) |
| GET | `/api/invite/{uuid}` | Dados do convidado |
| POST | `/api/invite/{uuid}/confirm` | Confirmar/recusar presença |
| GET | `/api/gifts` | Lista de presentes disponíveis |

### Admin (JWT)
| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/api/auth/login` | Login |
| GET | `/api/admin/stats` | Dashboard (totais convidados, confirmados, presentes) |
| PUT | `/api/admin/event` | Atualizar data do evento |
| GET | `/api/admin/guests` | Listar convidados |
| POST | `/api/admin/guests` | Criar convidado |
| PUT | `/api/admin/guests/{id}` | Atualizar convidado |
| DELETE | `/api/admin/guests/{id}` | Remover convidado |
| GET | `/api/admin/gifts` | Listar presentes |
| POST | `/api/admin/gifts` | Criar presente (com upload imagem S3) |
| PUT | `/api/admin/gifts/{id}` | Atualizar presente |
| DELETE | `/api/admin/gifts/{id}` | Remover presente (deleta S3) |
| GET | `/api/admin/gallery` | Listar fotos |
| POST | `/api/admin/gallery` | Upload foto(s) S3 |
| PUT | `/api/admin/gallery/reorder` | Reordenar fotos |
| DELETE | `/api/admin/gallery/{id}` | Remover foto (deleta S3) |

### Pagamento
| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/api/payments/create` | Criar PaymentIntent Stripe (retorna client_secret) |
| POST | `/api/webhooks/stripe` | Webhook confirmação pagamento |

## Plano de Commits (27 steps)

### Setup Inicial
| # | Commit | Descrição |
|---|--------|-----------|
| 1 | `feat: scaffold Angular 22 + SSR` | `ng new frontend` com SSR, estrutura de pastas |
| 2 | `feat: scaffold Spring Boot + Maven` | Projeto base com `spring-boot-starter-web`, `data-jpa`, `security`, `validation`, `flyway`, `aws-sdk-s3`, `stripe-java` |
| 3 | `feat: docker-compose dev` | `docker-compose.yml` com PostgreSQL + backend, `.env.example` |

### Backend — Core
| # | Commit | Descrição |
|---|--------|-----------|
| 4 | `feat: migrations e entidades JPA` | Scripts Flyway para as 5 tabelas + entidades `EventConfig`, `Guest`, `Gift`, `GiftPurchase`, `GalleryPhoto` + repositories |
| 5 | `feat: auth com JWT + env vars` | Filtro JWT, login endpoint, credenciais lidas de `ADMIN_USERNAME`/`ADMIN_PASSWORD`, `JWT_SECRET` |
| 6 | `feat: CRUD evento` | Controller + Service para `GET/PUT /api/admin/event` + `GET /api/event` público |
| 7 | `feat: CRUD convidados` | CRUD completo + geração de UUID + endpoint público `GET /api/invite/{uuid}` |
| 8 | `feat: confirmação de presença` | `POST /api/invite/{uuid}/confirm` com regras (data limite, toggle único, dupla confirmação no backend) |
| 9 | `feat: CRUD presentes + gift_purchases` | CRUD gifts + upload imagem S3 + tabela associativa `gift_purchases` |
| 10 | `feat: CRUD galeria` | Upload múltiplo S3, listagem, reordenação, remoção (deleta do S3) |
| 11 | `feat: integração Stripe` | `POST /api/payments/create` (cria PaymentIntent com cartão/Pix/Boleto) + webhook handler `POST /api/webhooks/stripe` |
| 12 | `feat: stats dashboard` | Endpoint `GET /api/admin/stats` com totais de convidados, confirmados, presentes dados |

### Frontend — Design System
| # | Commit | Descrição |
|---|--------|-----------|
| 13 | `feat: design system + tema romântico` | Paleta personalizada (terrosos, off-white, dourado suave), tipografia elegante, componentes visuais artesanais (sem libs UI prontas), animações sutis com `@angular/animations`, ícones SVG custom |

### Frontend — Admin
| # | Commit | Descrição |
|---|--------|-----------|
| 14 | `feat: login admin` | Tela de login, `AuthService`, guard de rota, interceptor JWT |
| 15 | `feat: dashboard admin` | Página com cards de estatísticas em tempo real |
| 16 | `feat: admin — evento` | Formulário editar data do casamento e data limite RSVP |
| 17 | `feat: admin — convidados` | Lista, CRUD, botão copiar link UUID para área de transferência |
| 18 | `feat: admin — presentes` | CRUD com upload de imagem, visualização de quem comprou |
| 19 | `feat: admin — galeria` | Upload drag-and-drop, grid com reordenação e exclusão |

### Frontend — Páginas Públicas
| # | Commit | Descrição |
|---|--------|-----------|
| 20 | `feat: página inicial pública` | Galeria de fotos + contagem regressiva + animações sutis |
| 21 | `feat: página de convite UUID` | Dados do convidado, botões confirmar/recusar com modal de dupla confirmação, redirect para presentes |
| 22 | `feat: lista de presentes pública` | Grid de gifts com seleção → checkout Stripe (cartão crédito / Pix / Boleto) |
| 23 | `feat: retorno pagamento` | Tela de confirmação ou falha após webhook Stripe |

### Polimento & Deploy
| # | Commit | Descrição |
|---|--------|-----------|
| 24 | `feat: SSR + meta tags Open Graph` | Configuração Angular Universal, meta tags dinâmicas por rota para compartilhamento social |
| 25 | `feat: responsivo + animações` | Ajustes mobile-first (foco em smartphones), lazy loading de imagens, refinamento de animações |
| 26 | `feat: config deploy Vercel` | `vercel.json` com configuração SSR, variável de ambiente `API_URL` |
| 27 | `feat: config deploy backend` | `application-prod.yml`, script de build Maven, instruções de deploy Oracle Cloud |

## Variáveis de Ambiente

### Backend (.env)
```
# Admin
ADMIN_USERNAME=admin
ADMIN_PASSWORD=senha-segura
JWT_SECRET=chave-segura-jwt

# Database
DB_URL=jdbc:postgresql://localhost:5432/wedding
DB_USER=postgres
DB_PASSWORD=postgres

# AWS S3
AWS_REGION=us-east-1
AWS_BUCKET=wedding-gallery
AWS_ACCESS_KEY=...
AWS_SECRET_KEY=...

# Stripe
STRIPE_SECRET_KEY=sk_live_...
STRIPE_WEBHOOK_SECRET=whsec_...
STRIPE_PUBLISHABLE_KEY=pk_live_...
```

### Frontend (Vercel)
```
API_URL=https://api.seusite.com
STRIPE_PUBLISHABLE_KEY=pk_live_...
```

## Docker Compose (dev)

```yaml
services:
  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: wedding
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"

  backend:
    build: ./backend
    ports:
      - "8080:8080"
    env_file: .env
    depends_on:
      - postgres
```

## Considerações Finais

- **Presentes:** modelo `gift_purchases` permite que N convidados comprem o mesmo presente
- **Stripe:** usar `payment_intent` com `automatic_payment_methods` habilitando `card`, `pix`, `boleto` — o próprio Stripe Checkout ou Elements exibe as opções para o usuário escolher
- **Frontend:** design system 100% custom, sem bibliotecas UI prontas (ex.: Material), para evitar aspecto genérico
- **Segurança:** validações de data no backend (nunca confiar apenas no frontend)
- **AGENTS.md:** manter arquivo atualizado a cada commit (adicionar/remover informações, atualizar status) para reduzir tokens em prompts futuros
