# Moneo

极简 AI 记账工具。目前包含已联调的 Vue 3 移动端前端，以及 Java 21 + Spring Boot 3 的单体后端。

```text
Moneo/
├─ frontend/                 # Vue 3 + TypeScript + Vite
├─ backend/                  # Spring Boot + MyBatis-Plus + MySQL
├─ docker-compose.yml         # MySQL 与后端
└─ .env.example               # Docker 环境变量示例
```

## 前端与后端字段映射

| 前端字段 | 后端 JSON 字段 | 数据库存储 | 说明 |
| --- | --- | --- | --- |
| `LedgerRecord.id` | `id` | `bill.id` | 前端为 number，后端为 `Long` |
| `type: 'expense' \| 'income'` | `type` | `bill.type` | 小写枚举，保持一致 |
| `category` | `category` | 由 `category.name` 组装 | 保持前端展示字段 |
| `icon` / `color` | `icon` / `color` | `category.icon` / `category.color` | 保持前端展示字段 |
| `amount` | `amount` | `bill.amount` | 后端为 `BigDecimal`，JSON 为数值 |
| `date` | `date` | `bill.occurred_at` | `yyyy-MM-dd` |
| `time` | `time` | `bill.occurred_at` | `HH:mm` |
| `note` | `note` | `bill.note` | 备注 |
| `categoryId` | `categoryId` | `bill.category_id` | 保存分类关系 |
| `accountId` | `accountId` | `bill.account_id` | 保存账户关系 |

分类接口返回 `id`、`name`、`type`、`icon`、`color`、`sortOrder`。账户接口返回 `id`、`name`、`icon`、`color`、`initialBalance`、`isDefault`。

前端通过 Vite 将 `/api` 代理到本地后端，并已使用分类、账户、账单和仪表盘接口。AI 页面仍是静态界面，因此后端不创建 AI API。预算仍保留在前端本地状态，后端本阶段没有预算表或接口。

## 后端接口

所有业务接口均返回：

```json
{ "code": 0, "message": "success", "data": {} }
```

| 模块 | 接口 |
| --- | --- |
| 健康 | `GET /api/health` |
| 账户 | `GET/POST /api/accounts`、`GET/PUT/DELETE /api/accounts/{id}` |
| 分类 | `GET /api/categories?type=expense`、`POST /api/categories`、`GET/PUT/DELETE /api/categories/{id}` |
| 账单 | `POST /api/bills`、`GET /api/bills`、`GET/PUT/DELETE /api/bills/{id}` |
| 预算 | `GET /api/budgets/{yyyy-MM}`、`PUT /api/budgets/{yyyy-MM}` |
| 首页 | `GET /api/dashboard/summary?month=2026-09` |

账单列表支持 `startDate`、`endDate`、`type`、`categoryId`、`accountId`、`keyword`、`page`、`pageSize`。首页接口一次返回当前月份的 `month`、`income`、`expense`、`balance` 和 `budget`；账单列表、分类统计与趋势均由前端基于账单查询结果按当前筛选周期计算。

## 本地开发

前端见 [frontend/README.md](frontend/README.md)。后端见 [backend/README.md](backend/README.md)。本地启动后端时配置 `DB_HOST=localhost`；通过 Docker Compose 启动时，服务会使用 `DB_HOST=mysql`。

```bash
docker compose up -d --build
```

复制 `.env.example` 为 `.env` 后再改成真实生产密码。`.env` 已由 Git 忽略。
