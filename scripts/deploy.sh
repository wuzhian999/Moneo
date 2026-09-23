#!/usr/bin/env bash

set -Eeuo pipefail

PROJECT_DIR="${PROJECT_DIR:-/opt/apps/Moneo}"
BRANCH="${BRANCH:-main}"
ENV_FILE="${ENV_FILE:-$PROJECT_DIR/.env}"
HEALTH_TIMEOUT_SECONDS="${HEALTH_TIMEOUT_SECONDS:-120}"

COMPOSE=()

print_header() {
  echo "======================================"
  echo "🚀 Moneo 开始部署"
  echo "======================================"
}

fail() {
  echo "❌ $*" >&2
  show_diagnostics
  exit 1
}

show_diagnostics() {
  if ((${#COMPOSE[@]})); then
    echo ""
    echo "最近的容器状态与日志：" >&2
    "${COMPOSE[@]}" ps >&2 || true
    "${COMPOSE[@]}" logs --tail=100 frontend backend mysql >&2 || true
  fi
}

on_error() {
  local exit_code=$?
  echo ""
  echo "❌ 部署命令执行失败。" >&2
  show_diagnostics
  exit "$exit_code"
}

require_command() {
  command -v "$1" >/dev/null 2>&1 || fail "缺少命令：$1"
}

read_env_value() {
  local key="$1"
  local value
  value="$(sed -n -E "s/^${key}=(.*)$/\\1/p" "$ENV_FILE" | tail -n 1)"
  printf '%s' "$value"
}

validate_env() {
  [[ -f "$ENV_FILE" ]] || fail "未找到环境变量文件：$ENV_FILE。请先复制 .env.example 为 .env 并填写密码。"

  local key value
  for key in MYSQL_PASSWORD MYSQL_ROOT_PASSWORD; do
    value="$(read_env_value "$key")"
    [[ -n "$value" ]] || fail "$ENV_FILE 缺少 $key"
    [[ "$value" != replace_with_* ]] || fail "$key 仍是示例值，请替换为真实强密码"
  done
}

wait_for_http() {
  local name="$1"
  local url="$2"
  local expected="$3"
  local deadline=$((SECONDS + HEALTH_TIMEOUT_SECONDS))
  local response

  echo "⏳ 等待${name}就绪，最长 ${HEALTH_TIMEOUT_SECONDS} 秒…"
  while ((SECONDS < deadline)); do
    if response="$(curl --fail --silent --show-error --connect-timeout 3 --max-time 8 "$url" 2>/dev/null)"; then
      if [[ -z "$expected" || "$response" == *"$expected"* ]]; then
        echo "✅ ${name}运行正常"
        return 0
      fi
    fi
    sleep 3
  done

  fail "${name}未能在规定时间内就绪：$url"
}

trap on_error ERR

print_header

require_command git
require_command docker
require_command curl

[[ -d "$PROJECT_DIR/.git" ]] || fail "项目目录不存在或不是 Git 仓库：$PROJECT_DIR"
cd "$PROJECT_DIR"

validate_env
COMPOSE=(docker compose --env-file "$ENV_FILE")

echo "📥 1. 拉取 ${BRANCH} 最新代码…"
git fetch --prune origin
git checkout "$BRANCH"
git pull --ff-only origin "$BRANCH"

echo "🔍 2. 校验 Docker Compose 配置…"
"${COMPOSE[@]}" config -q

frontend_port="$(read_env_value FRONTEND_PORT)"
frontend_port="${frontend_port:-80}"
[[ "$frontend_port" =~ ^[0-9]{1,5}$ ]] || fail "FRONTEND_PORT 必须是有效端口号"
base_url="http://127.0.0.1:${frontend_port}"

echo "🐳 3. 构建并启动容器…"
"${COMPOSE[@]}" up -d --build --remove-orphans

echo "📦 4. 当前容器状态："
"${COMPOSE[@]}" ps

echo "🌐 5. 检查前端…"
wait_for_http "前端" "$base_url/" ""

echo "🔧 6. 检查后端健康状态…"
wait_for_http "后端" "$base_url/api/health" '"status":"UP"'

echo "======================================"
echo "🎉 Moneo 部署完成：$base_url"
echo "======================================"
