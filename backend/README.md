# Moneo Backend

Java 21、Spring Boot 3、MyBatis-Plus 和 MySQL 8 的单体后端。

## 本地运行

先准备 MySQL 8，并使用 [schema.sql](src/main/resources/db/schema.sql) 初始化 `moneo` 数据库。环境变量默认指向本机示例配置：

```powershell
$env:DB_HOST = 'localhost'
$env:DB_PORT = '3306'
$env:DB_NAME = 'moneo'
$env:DB_USERNAME = 'moneo'
$env:DB_PASSWORD = 'moneo'
mvn spring-boot:run
```

访问 `GET http://localhost:8080/api/health` 验证服务。

## API

- `GET /api/health`
- `GET|POST|PUT|DELETE /api/accounts`
- `GET|POST|PUT|DELETE /api/categories`
- `GET|POST|PUT|DELETE /api/bills`
- `GET|PUT /api/budgets/{yyyy-MM}`
- `GET /api/dashboard/summary?month=2026-09`

账单列表支持 `startDate`、`endDate`、`type`、`categoryId`、`accountId`、`keyword`、`page`、`pageSize`。
`GET /api/dashboard/summary` 返回指定月份的 `month`、`income`、`expense`、`balance` 和 `budget`。预算通过 `PUT /api/budgets/{yyyy-MM}` 保存，金额使用 `BigDecimal`。

## Docker Compose

在项目根目录将 `.env.example` 复制为 `.env` 并修改密码，然后运行：

```bash
docker compose up -d --build
```

容器内后端使用 `DB_HOST=mysql`；本地运行 Spring Boot 时使用 `DB_HOST=localhost`。`.env` 不提交到 Git。
