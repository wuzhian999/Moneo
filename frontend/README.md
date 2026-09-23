# Moneo 前端

Vue 3、TypeScript、Vite 和 Vant 构建的移动端记账界面，已接入 Moneo Spring Boot 后端。

## 运行

先在项目根目录启动后端与 MySQL：

```bash
docker compose up -d
```

再启动前端开发服务器：

```bash
npm install
npm run dev
```

开发服务器运行在 `http://localhost:5173`，Vite 会把 `/api` 请求代理到 `http://localhost:8080`。

如需直接请求其他后端地址，创建本地 `.env` 并设置：

```bash
VITE_API_BASE_URL=http://your-api-host:8080/api
```

运行 `npm run build` 会先进行 TypeScript 检查，再生成生产构建。

## 数据接口

- `GET /api/categories`：支出和收入分类
- `GET /api/accounts`：默认账户
- `GET /api/bills`：按月份或统计周期查询账单
- `POST /api/bills`：快速记账
- `GET /api/dashboard/summary`：月度收入、支出、结余与预算汇总
- `PUT /api/budgets/{yyyy-MM}`：保存当前月份预算

预算仍是前端本地状态，因为第一阶段后端尚未提供预算模块。

## 目录

```text
src/
├─ api/                    # fetch 请求封装及账本 API
├─ App.vue                 # 应用外壳、全局弹层和初始数据加载
├─ main.ts                 # 入口
├─ router/                 # 账本、AI、统计三个页面的路由
├─ pages/                  # 一级页面
├─ components/
│  ├─ layout/              # 底部导航
│  ├─ ledger/              # 账单分组及记账、月份、预算弹层
│  └─ statistics/          # 趋势与分类明细
├─ composables/            # 共享账本状态、远程加载和统计计算
├─ types/                  # 前后端对齐的业务类型
├─ utils/                  # 金额和日期格式化
└─ styles/                 # 基础、弹层及导航样式
```
