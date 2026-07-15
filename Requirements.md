# Site de Casamento Pedro & Giovana

Eu, Pedro, sou desenvolvedor de software e irei manter o site do meu casamento.

## Tecnologias
- **Frontend:** Angular 22 com Angular Universal (SSR)
- **Backend:** Java 21 com Spring Boot (artefato JAR)
- **Banco de dados:** PostgreSQL
- **Armazenamento:** AWS S3 (fotos da galeria e imagens de presentes)
- **Pagamentos:** Integração com Mercado Pago (Pix)
- **Autenticação:** JWT mas com login e senha definido por env var no backend
- **Deploy:** Frontend na Vercel; backend como JAR em Oracle cloud server

---

## Requisitos Funcionais

### RF01 — Página Inicial (Pública)
- **Galeria de fotos** com imagens do casal, carregadas via administração.
- **Contagem regressiva** dinâmica baseada na data do evento configurada no sistema.
- Layout limpo e romântico, com animações sutis.

### RF02 — Área dos Noivos (Administrativa)
Acesso restrito via login/senha definidos por variáveis de ambiente no backend. Após autenticação, um JWT é gerado e mantido em sessão.

**Recursos administrativos:**
- Editar data do evento (impacta contagem regressiva e validações de confirmação).
- Editar data limite de confirmação de presença.
- CRUD de convidados (nome).
- CRUD de presentes virtuais (nome, descrição, valor, imagem armazenada em S3).
- Gerenciar fotos da galeria (upload para S3, exclusão, ordem).

**Estatísticas em tempo real:**
- Totais de convidados, confirmados e não confirmados.
- Visualizar por convidado qual presente foi selecionado e se o pagamento foi confirmado.

### RF03 — Convite Digital e Confirmação de Presença
- Cada convidado terá um link único (UUID) gerado pelo sistema e a ser enviado pelo noivo ou noiva.
- O link exibe o convite personalizado e permite confirmar ou não presença.
- Regras de negócio:
  - Não permitir confirmar/desconfirmar após a data limite.
  - Não permitir desconfirmar após já ter confirmado.
  - Não permitir confirmar após já ter desconfirmado.
  - Dupla confirmação via modal antes de efetivar a ação.
- Após a ação, redirecionar para a página de lista de presentes virtuais.

### RF04 — Lista de Presentes Virtuais
- Cada presente tem: imagem (URL no S3), nome, descrição e valor.
- Convidado seleciona um presente e é redirecionado para pagamento via Mercado Pago (Pix).
- Após confirmação do pagamento (webhook), o sistema registra o presente como "dado" por aquele convidado.

### RF05 — Galeria de Fotos (Administrável)
- Upload de fotos via área administrativa, com persistência no AWS S3.
- Suporte a múltiplas imagens.
- Ordenação e remoção de fotos (remoção também apaga o objeto no S3).

---

## Requisitos Não Funcionais

- **Responsivo:** Mobile-first, adaptado para smartphones (principal dispositivo dos convidados).
- **SSR/SEO:** Angular Universal para renderização no servidor, meta tags dinâmicas para compartilhamento em redes sociais (Open Graph).
- **Performance:** Tempo de carregamento inicial < 3s (SSR). Imagens otimizadas (WebP, lazy loading).
- **Segurança:** JWT para área dos noivos. Links únicos com UUID para convites. Validação de data no backend (não confiar apenas no frontend).
- **Pagamentos:** Integração com Mercado Pago via webhook para confirmação de Pix.
- **Armazenamento:** Imagens da galeria e dos presentes armazenadas no AWS S3; o backend expõe URLs públicas (ou assinadas) para o frontend.
- **Deploy:**
  - **Frontend:** Vercel (SSR Angular / Open Graph). Variável `API_URL` apontando para o backend.
  - **Backend:** build Maven produzindo um JAR executável (`java -jar`), com variáveis de ambiente para DB, JWT, Mercado Pago e AWS (região, bucket, access key / IAM).
  - **Banco:** PostgreSQL gerenciado (ex.: Neon, RDS, Supabase) acessível pelo backend.
  - Docker Compose apenas para desenvolvimento local opcional (API + Postgres), não como target de produção.
