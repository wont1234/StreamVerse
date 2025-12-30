# StreamVerse/TikTube Docker + Cloudflare Tunnel 部署说明（CentOS 7.8 + NAT）

本文档记录将本项目（Spring Boot + Vue/Vuetify）使用 **Docker Compose** 部署到 **CentOS 7.8 虚拟机（NAT）**，并通过 **Cloudflare Tunnel** 暴露到公网的完整步骤、命令与注意事项。

适用场景：

- 你只有一台本地/内网 VM（没有公网 IP、没有端口映射）
- 希望通过 Cloudflare Tunnel 让域名公网可访问

---

## 目录

- [1. 目录结构与端口](#1-目录结构与端口)
- [2. 前置条件](#2-前置条件)
- [3. Windows -> VM 上传项目（推荐压缩包）](#3-windows---vm-上传项目推荐压缩包)
- [3.1（可选）在 Windows 本机构建 Docker 镜像并导入到 VM](#31可选在-windows-本机构建-docker-镜像并导入到-vm)
- [4. Docker 部署（Compose）](#4-docker-部署compose)
- [4.4 Docker 如何封装（镜像构建说明）](#44-docker-如何封装镜像构建说明)
- [5. 常用运维命令](#5-常用运维命令)
- [5.1 停服/重启（Docker Compose）](#51-停服重启docker-compose)
- [5.2 后续代码更新与重新部署](#52-后续代码更新与重新部署)
- [6. Cloudflare Tunnel 暴露公网](#6-cloudflare-tunnel-暴露公网)
- [6.8 为什么建议使用 http2（禁用 QUIC）](#68-为什么建议使用-http2禁用-quic)
- [6.9 cloudflared 常驻与运维（systemd）](#69-cloudflared-常驻与运维systemd)
- [7. 常见问题（排障）](#7-常见问题排障)
- [8. 视频（COS）访问慢的说明与建议](#8-视频cos访问慢的说明与建议)

---

## 1. 目录结构与端口

项目在 VM 的建议目录（示例）：

- 项目根目录：`/opt/VideoWeb-master/`
- Docker 部署目录：`/opt/VideoWeb-master/deploy/docker/`
- SQL 初始化文件：`/opt/VideoWeb-master/sql/tik_tube.sql`

服务端口（容器内）：

- **web（nginx + 前端静态）**：80
- **backend（Spring Boot）**：8080（仅容器网络内可访问）
- **mysql**：3306（仅容器网络内可访问）
- **redis**：6379（仅容器网络内可访问）

宿主机暴露端口：

- `80:80`（宿主机 80 对外提供前端 + 反代 API/WS）

---

## 2. 前置条件

### 2.1 VM（CentOS 7.8）

- 已安装 Docker 与 docker compose 插件
- 建议关闭/停止宿主机自身 nginx（避免占用 80 端口）

可用检查：

```bash
docker --version
docker compose version
```

### 2.2 Cloudflare

- 域名在 Cloudflare 账号中可管理（Zone 可选）
- 计划使用的公网域名示例：`sv.zpc666.dpdns.org`

---

## 3. Windows -> VM 上传项目（推荐压缩包）

如果你用 SFTP 传大量小文件容易失败，推荐在 Windows 先打包：

1) 在 Windows 项目目录的上级目录执行（PowerShell/右键压缩也行）：

- 生成 `VideoWeb-master.zip`

2) 用 FinalShell / SFTP 上传到 VM（例如 `/opt/`）

3) VM 解压：

```bash
cd /opt
unzip VideoWeb-master.zip
```

### 3.1（可选）在 Windows 本机构建 Docker 镜像并导入到 VM

 默认推荐在 VM 上执行 `docker compose up -d --build` 现场构建（最省事）。

 如果你希望 **在 Windows 本机先构建好镜像**，然后把镜像文件传到 VM（避免 VM 构建慢/避免拉取镜像困难），可以按下面流程。

 前置要求：

 - Windows 已安装 Docker Desktop
 - Docker Desktop 使用 Linux 容器（默认就是）
 - VM 是 x86_64（amd64）架构（大多数 CentOS 7.8 VM 都是）

 1) 在 Windows 打开 PowerShell，进入部署目录：

 ```powershell
 cd .\VideoWeb-master\deploy\docker
 ```

 2) 为了让镜像名称一致，建议固定 Compose 项目名（可选但推荐）：

 ```powershell
 $env:COMPOSE_PROJECT_NAME = "streamverse"
 ```

 3) 构建镜像（只构建 web/backend 即可）：

 ```powershell
 docker compose build backend web
 docker compose images
 ```

 4) 导出镜像为一个 tar 包（先用 `docker compose images` 看实际镜像名）：

 ```powershell
 # 常见镜像名形如：streamverse-backend、streamverse-web
 docker save streamverse-backend streamverse-web -o streamverse-images.tar
 ```

 5) 把 `streamverse-images.tar` 上传到 VM（例如 `/opt/`），在 VM 上导入：

 ```bash
 docker load -i /opt/streamverse-images.tar
 ```

 6) 在 VM 启动时禁止自动构建（使用导入的镜像）：

 ```bash
 cd /opt/VideoWeb-master/deploy/docker
 COMPOSE_PROJECT_NAME=streamverse docker compose up -d --no-build
 ```

 > 如果你不设置 `COMPOSE_PROJECT_NAME`，Windows 构建出的镜像名可能和 VM 运行时不一致，导致 VM 仍然会尝试重新 build。

---

## 4. Docker 部署（Compose）

### 4.1 准备 `.env`

部署目录：`VideoWeb-master/deploy/docker/`

在 VM 上进入目录：

```bash
cd /opt/VideoWeb-master/deploy/docker
```

复制示例文件并编辑：

```bash
cp .env.example .env
vi .env
```

重点变量：

- `MYSQL_DATABASE`
- `MYSQL_USER`
- `MYSQL_PASSWORD`
- `MYSQL_ROOT_PASSWORD`

### 4.2 确认 SQL 初始化文件路径

`docker-compose.yml` 中 MySQL 会挂载：

- `../../sql/tik_tube.sql:/docker-entrypoint-initdb.d/01_init.sql:ro`

因此确保 VM 上存在：

- `/opt/VideoWeb-master/sql/tik_tube.sql`

### 4.3 启动服务

```bash
cd /opt/VideoWeb-master/deploy/docker
docker compose up -d --build
```

检查状态：

```bash
docker compose ps
```

本机验证（VM 内）：

```bash
curl -I http://127.0.0.1/
# 下面 401 表示 API 反代已通，只是需要登录
curl -i http://127.0.0.1/api/admin/system/info
```

---

## 4.4 Docker 如何封装（镜像构建说明）

本项目的 Docker 镜像由 `docker compose up -d --build` 在 VM 上本地构建，主要有两个自定义镜像：

- `backend`：Spring Boot 后端
- `web`：Vue 前端 + nginx 静态托管（同时反代 `/api`、`/ws`）

关键点：

- **构建上下文（build context）**：Compose 配置为项目根目录（`../../`），因此 Dockerfile 里 `COPY` 使用的是仓库根目录相对路径。
- **后端 Dockerfile**：多阶段构建（Maven 构建 jar -> JRE 运行）。
- **前端 Dockerfile**：多阶段构建（Node 构建静态文件 -> nginx 运行）。
- **数据持久化**：
  - MySQL 数据在 `mysql_data` 卷中，重启/升级不会丢。
  - Redis 数据在 `redis_data` 卷中。
  - 后端日志在 `backend_logs` 卷中。
  - 只有执行 `docker compose down -v` 才会删除这些卷（会清空数据库）。

---

## 5. 常用运维命令

进入部署目录：

```bash
cd /opt/VideoWeb-master/deploy/docker
```

查看日志：

```bash
docker compose logs -f --tail=200
# 单个服务
docker compose logs -f --tail=200 backend
```

重启：

```bash
docker compose restart
```

重新构建某个服务（例如 web）：

```bash
docker compose up -d --build web
```

彻底清理（会删除数据库数据，请谨慎）：

```bash
docker compose down -v
```

### 5.1 停服/重启（Docker Compose）

进入部署目录：

```bash
cd /opt/VideoWeb-master/deploy/docker
```

停止（不删除容器/不删除数据卷）：

```bash
docker compose stop
```

再次启动（使用原有镜像，不重新构建）：

```bash
docker compose start
```

停止并删除容器（**不删除卷**，数据库数据还在）：

```bash
docker compose down
```

停止并删除容器 + 删除卷（**会清空数据库**，慎用）：

```bash
docker compose down -v
```

重启某个服务（例如后端）：

```bash
docker compose restart backend
```

### 5.2 后续代码更新与重新部署

常见更新场景：

#### 5.2.1 更新前端代码（只重建 web）

1) Windows 重新打包并上传项目（或只上传前端目录）
2) VM 解压覆盖后，在部署目录执行：

```bash
cd /opt/VideoWeb-master/deploy/docker
docker compose up -d --build web
```

#### 5.2.2 更新后端代码（只重建 backend）

```bash
cd /opt/VideoWeb-master/deploy/docker
docker compose up -d --build backend
```

#### 5.2.3 同时更新前后端（整体重建）

```bash
cd /opt/VideoWeb-master/deploy/docker
docker compose up -d --build
```

#### 5.2.4 “重新上传”推荐流程（避免缺文件/上传失败）

- Windows 端把整个 `VideoWeb-master` 重新压缩为 `VideoWeb-master.zip`
- 上传到 VM（如 `/opt/`)
- 建议先备份旧目录再覆盖：

```bash
cd /opt
mv VideoWeb-master VideoWeb-master_bak_$(date +%F_%H%M%S)
unzip VideoWeb-master.zip
```

然后进入新目录继续 `docker compose up -d --build`。

### 5.3 VM 重启后自动恢复（推荐设置）

#### 5.3.1 让 Docker 服务开机自启

```bash
systemctl enable --now docker
systemctl status docker --no-pager
```

#### 5.3.2 让 Compose 服务随 Docker 自动拉起（已配置）

本项目 `docker-compose.yml` 中各服务已配置：

- `restart: unless-stopped`

含义：

- VM 重启后，只要 Docker 服务起来，容器会自动启动
- 如果你手动执行过 `docker compose stop`，则需要你手动 `docker compose start` 才会再次启动

重启后检查：

```bash
cd /opt/VideoWeb-master/deploy/docker
docker compose ps
```

#### 5.3.3 （可选）用 systemd 管理整套 docker compose（更可控）

如果你希望像管理服务一样管理整套 Compose（开机自动 `up -d`、关机自动 `down`），可以创建 systemd unit。

创建文件：`/etc/systemd/system/streamverse-compose.service`

```ini
[Unit]
Description=StreamVerse Docker Compose
Requires=docker.service
After=docker.service

[Service]
Type=oneshot
RemainAfterExit=yes
WorkingDirectory=/opt/VideoWeb-master/deploy/docker
ExecStart=/usr/bin/docker compose up -d
ExecStop=/usr/bin/docker compose down

[Install]
WantedBy=multi-user.target
```

启用并启动：

```bash
systemctl daemon-reload
systemctl enable --now streamverse-compose
systemctl status streamverse-compose --no-pager
```

注意：

- 该 unit 的 `ExecStop` 会执行 `docker compose down`（不删卷），数据库数据不会丢。
- 如果你不希望停止时自动 down，可以不启用该 unit，继续使用 `restart: unless-stopped` 即可。

---

## 6. Cloudflare Tunnel 暴露公网

目标：让公网域名 `https://sv.zpc666.dpdns.org` 指向 VM 的 `http://127.0.0.1:80`

> 说明：如果 VM 网络环境对 QUIC(UDP) 不稳定，建议使用 `http2(TCP)`。

### 6.1 安装 cloudflared（CentOS 7 x86_64）

```bash
sudo mkdir -p /usr/local/bin
curl -L -o /usr/local/bin/cloudflared https://github.com/cloudflare/cloudflared/releases/latest/download/cloudflared-linux-amd64
sudo chmod +x /usr/local/bin/cloudflared
cloudflared --version
```

### 6.2 登录（如果 VM 访问 login.cloudflareaccess.org 超时：用 Windows 浏览器下载 cert.pem）

在 VM 执行：

```bash
cloudflared tunnel login
```

它会输出一个 URL。

- 用 **Windows 浏览器** 打开该 URL，选择 Zone 并授权
- 如果 VM 网络导致登录超时，Cloudflare 会让浏览器下载 `cert.pem`

将 `cert.pem` 上传到 VM：

```bash
mkdir -p /root/.cloudflared
mv /root/cert.pem /root/.cloudflared/cert.pem
chmod 600 /root/.cloudflared/cert.pem
```

### 6.3 创建 Tunnel

```bash
cloudflared tunnel create streamverse
```

记下输出中的：

- Tunnel UUID（示例：`25b13eda-05f3-...`）
- credentials 文件路径（示例：`/root/.cloudflared/<uuid>.json`）

### 6.4 写配置 `/etc/cloudflared/config.yml`

```bash
mkdir -p /etc/cloudflared
cat >/etc/cloudflared/config.yml <<'EOF'
tunnel: 25b13eda-05f3-4fc1-be09-362a3f7cac28
credentials-file: /root/.cloudflared/25b13eda-05f3-4fc1-be09-362a3f7cac28.json

# 重要：NAT/部分网络 UDP 不稳定时，建议使用 http2(TCP)
protocol: http2
# 注意：某些版本要求 edge-ip-version 必须是字符串，否则会报：expected string found int
edge-ip-version: "4"

ingress:
  - hostname: sv.zpc666.dpdns.org
    service: http://127.0.0.1:80
  - service: http_status:404
EOF
```

> 上面 UUID/credentials 请替换成你自己的。

### 6.5 绑定域名到 Tunnel（自动写入 Cloudflare DNS CNAME）

```bash
cloudflared tunnel route dns streamverse sv.zpc666.dpdns.org
```

### 6.6 前台启动验证

```bash
cloudflared tunnel --config /etc/cloudflared/config.yml run streamverse
```

浏览器访问：

- `https://sv.zpc666.dpdns.org/`

验证成功后，按 `Ctrl + C` 停掉前台进程。

### 6.7 systemd 常驻（开机自启）

```bash
cloudflared service install
systemctl enable --now cloudflared
systemctl status cloudflared --no-pager
```

验证当前 cloudflared 的连接协议是否为 http2：

```bash
journalctl -u cloudflared -n 50 --no-pager | grep -E "Initial protocol|protocol=" || true
```

重启验证闭环（可选但推荐）：

```bash
reboot
# 重启后登录
systemctl status docker --no-pager
systemctl status cloudflared --no-pager
cd /opt/VideoWeb-master/deploy/docker && docker compose ps
```

查看日志：

```bash
journalctl -u cloudflared -f
```

### 6.8 为什么建议使用 http2（禁用 QUIC）

cloudflared 默认可能会优先使用 `quic`（基于 UDP）。在很多 NAT/校园网/企业网环境里，UDP 长连接容易被丢弃或限速，典型现象是日志出现：

- `timeout: no recent network activity`

因此建议改为 `http2`（基于 TCP），通常更稳定。

推荐做法：写进 `/etc/cloudflared/config.yml`：

```yml
protocol: http2
edge-ip-version: 4
```

### 6.9 cloudflared 常驻与运维（systemd）

安装为服务并开机自启：

```bash
cloudflared service install
systemctl enable --now cloudflared
systemctl status cloudflared --no-pager
```

常用命令：

```bash
systemctl status cloudflared --no-pager
systemctl restart cloudflared
systemctl stop cloudflared
systemctl start cloudflared
journalctl -u cloudflared -f
```

如果发现服务启动后又回到了 `quic`，优先确认：

- `/etc/cloudflared/config.yml` 已写入 `protocol: http2`
- 服务确实读取了该配置（看日志/或查看服务定义）：

```bash
systemctl cat cloudflared
```

---

## 7. 常见问题（排障）

### 7.1 web 端口 80 被占用

症状：`bind: address already in use`

解决：停止宿主机 nginx 或改 Compose 映射端口。

```bash
systemctl stop nginx || true
systemctl disable nginx || true
```

### 7.2 MySQL 初始化 SQL 没有生效

- 确认 `tik_tube.sql` 在 VM 上真实存在且路径正确
- 如果之前启动过并已生成数据卷，需要清理卷后才会重新初始化：

```bash
docker compose down -v
docker compose up -d
```

### 7.3 Docker Hub 拉镜像失败（DNS 污染/网络问题）

本项目部署文件已将基础镜像切换到 DaoCloud 镜像源前缀（示例：`m.daocloud.io/docker.io/library/...`）。

如果仍失败：

- 优先检查 DNS/IPv6
- 或为 Docker 配置镜像加速（`/etc/docker/daemon.json`）

### 7.4 /api 全 404（API 反代问题）

Nginx 的 `proxy_pass` 尾部是否带 `/` 会影响路径转发。

正确示例（保留 `/api` 前缀）：

```nginx
location /api/ {
  proxy_pass http://backend:8080;
}
```

验证：

```bash
curl -i http://127.0.0.1/api/admin/system/info
# 返回 401 也算 OK（说明反代到后端了）
```

### 7.5 cloudflared 出现 QUIC 超时

症状：`timeout: no recent network activity`、频繁断线重连。

解决：改用 `http2`：

- 在 `config.yml` 写入 `protocol: http2`（推荐）
- 或启动命令加 `--protocol http2`

---

## 8. 视频（COS）访问慢的说明与建议

你的视频存储在 COS，并且播放器请求已显示 `206 Partial Content`（Range 正常）。

**仍然慢**通常原因：

- 用户直连 COS 跨网/跨地域链路慢
- URL 带 `X-Amz-*` 签名参数导致边缘缓存难命中

推荐优化：

1) **腾讯云 CDN 加速 COS Bucket**（国内用户体验通常最好）
2) 给 CDN 绑定自定义域名（如 `media.zpc666.dpdns.org`）并启用 HTTPS
3) 优化鉴权方式：

- 推荐使用 **CDN URL 鉴权/Token 鉴权** 或 **Referer 防盗链**
- 尽量避免每次播放都生成不同的带签名 Query 的 URL（会降低缓存命中）

---

## 完成标准

当满足以下条件时，部署完成：

- `docker compose ps` 显示 mysql/redis/backend/web 均为 Running（mysql healthy）
- VM 内 `curl -I http://127.0.0.1/` 返回 200
- 公网 `https://sv.zpc666.dpdns.org/` 可访问
- `systemctl status cloudflared` 显示 active (running)
