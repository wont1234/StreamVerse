# 从 0 到公网：CentOS 7.8（NAT）用 Docker Compose 部署 Spring Boot + Vue，并用 Cloudflare Tunnel 暴露域名

> 本文记录一次完整的实战部署：在 **CentOS 7.8 虚拟机（NAT，无公网 IP）** 上，通过 **Docker Compose** 部署 **Spring Boot 后端 + Vue/Vuetify 前端**，并使用 **Cloudflare Tunnel** 将站点以域名方式发布到公网。
>
> 如果你只需要“最终可复制的命令清单”，可以直接看文末的「命令汇总」。

---

## 1. 背景与目标

我的环境：

- Windows 本机开发
- 一台 CentOS 7.8 VM（NAT 网络，外网能出、但没有公网入方向端口映射）
- 目标：
  - 用 Docker 一键启动 `mysql + redis + backend + web(nginx)`
  - 通过 Cloudflare Tunnel 把 `http://127.0.0.1:80` 映射到公网域名 `https://sv.example.com`

最终访问效果：

- 本机（VM）访问：`curl -I http://127.0.0.1/` 返回 `200`
- 公网访问：浏览器打开 `https://sv.example.com/` 正常加载页面

---

## 2. 最终架构

### 2.1 服务架构

- `mysql`：数据库，初始化 SQL 通过 volume 挂载注入
- `redis`：缓存/限流等
- `backend`：Spring Boot（容器内 8080，仅 Docker 网络可达）
- `web`：nginx + 前端静态文件，对外暴露 80，并反代：
  - `/api` -> `backend:8080`
  - `/ws` -> `backend:8080/ws`

### 2.2 网络与入口

- 宿主机：只开放 `80`（实际上是 docker 映射 `80:80`）
- 公网入口：Cloudflare Tunnel
  - `sv.example.com` -> `http://127.0.0.1:80`

---

## 3. 把项目传到 VM（大量小文件的正确姿势）

一开始直接 SFTP 传目录经常失败（小文件太多）。最终用“压缩包”方案稳定解决：

- Windows 把项目目录打包成 `VideoWeb-master.zip`
- 上传到 VM `/opt/`
- 解压：

```bash
cd /opt
unzip VideoWeb-master.zip
```

---

## 4. Docker Compose 部署

### 4.1 `.env` 配置

进入部署目录：

```bash
cd /opt/VideoWeb-master/deploy/docker
cp .env.example .env
vi .env
```

重点配置 MySQL 账号密码。

### 4.2 SQL 初始化脚本路径

Compose 中 MySQL 会挂载：

- `../../sql/tik_tube.sql:/docker-entrypoint-initdb.d/01_init.sql:ro`

所以 VM 需要存在：

- `/opt/VideoWeb-master/sql/tik_tube.sql`

### 4.3 启动与验证

```bash
cd /opt/VideoWeb-master/deploy/docker
docker compose up -d --build
docker compose ps
curl -I http://127.0.0.1/
```

---

## 5. 部署过程踩坑记录（以及怎么解决）

### 5.1 80 端口被占用

症状：web 容器启动失败 `bind: address already in use`。

原因：宿主机 nginx 占用了 80。

解决：停止并禁用宿主机 nginx：

```bash
systemctl stop nginx || true
systemctl disable nginx || true
```

### 5.2 MySQL 版本 / 参数不兼容

症状：MySQL 启动报错 `unknown variable 'default-authentication-plugin=...'`。

原因：`mysql:8` 可能解析到较新版本，部分参数行为变化。

解决：固定版本（例如 `8.0.40`）。

### 5.3 Docker Hub 拉镜像失败（网络/DNS 污染）

症状：`docker pull` 超时。

解决：改用镜像源前缀（示例 DaoCloud）：

- `m.daocloud.io/docker.io/library/mysql:8.0.40`

### 5.4 `/api` 全 404：nginx 反代路径被“吃掉”

症状：访问 `http://127.0.0.1/api/...` 全 404。

原因：nginx `proxy_pass` 末尾带 `/` 会改变 URI 拼接行为。

修复：保留 `/api` 前缀：

```nginx
location /api/ {
  proxy_pass http://backend:8080;
}
```

验证方式：

```bash
curl -i http://127.0.0.1/api/admin/system/info
# 返回 401 也算 OK（说明已转发到后端，只是需要登录）
```

---

## 6. Cloudflare Tunnel：无公网 IP 的关键

### 6.1 为什么用 Tunnel

因为 VM 是 NAT：

- 能出网
- 但没有公网入方向端口映射

Tunnel 的好处：

- 不用公网 IP
- 不用开防火墙端口
- Cloudflare 会把你的域名流量安全转发到本机服务

### 6.2 登录卡住：VM 访问 `login.cloudflareaccess.org` 超时

症状：

- `cloudflared tunnel login` 一直 `Waiting for login...`
- 最终提示无法写入证书 `cert.pem`

原因：VM 网络访问 Cloudflare 登录域名不稳定/超时。

解决：用 Windows 浏览器授权后下载 `cert.pem`，再上传到 VM：

```bash
mkdir -p /root/.cloudflared
mv /root/cert.pem /root/.cloudflared/cert.pem
chmod 600 /root/.cloudflared/cert.pem
```

### 6.3 创建 tunnel + 配置 ingress

创建：

```bash
cloudflared tunnel create streamverse
```

写配置：

```yml
tunnel: <TUNNEL_UUID>
credentials-file: /root/.cloudflared/<TUNNEL_UUID>.json

protocol: http2
edge-ip-version: "4"

ingress:
  - hostname: sv.example.com
    service: http://127.0.0.1:80
  - service: http_status:404
```

绑定 DNS：

```bash
cloudflared tunnel route dns streamverse sv.example.com
```

### 6.4 QUIC 超时：为什么要用 http2

症状：

- `Initial protocol quic`
- 大量 `timeout: no recent network activity`

原因：`quic` 基于 UDP，在 NAT/某些网络环境下 UDP 长连接不稳定。

解决：

- 用 `protocol: http2`（TCP）提高稳定性
- 同时 `edge-ip-version` 建议固定 IPv4（注意：某些版本要求字符串类型，否则报 `expected string found int`）

### 6.5 systemd 常驻

安装成服务：

```bash
cloudflared service install
systemctl enable --now cloudflared
systemctl status cloudflared --no-pager
```

验证协议：

```bash
journalctl -u cloudflared -n 50 --no-pager | grep -E "Initial protocol|protocol=" || true
```

---

## 7. VM 重启后的自动恢复

### 7.1 Docker 开机自启

```bash
systemctl enable --now docker
```

### 7.2 Compose 容器自动启动

Compose 里配置了 `restart: unless-stopped`，所以：

- VM 重启后 Docker 起了，容器会自动起

重启后检查：

```bash
cd /opt/VideoWeb-master/deploy/docker
docker compose ps
systemctl status cloudflared --no-pager
```

---

## 8. 视频很慢：COS 直链 + 签名 URL 的现实

视频存储在 COS，播放器请求是 `206 Partial Content`（Range 正常），但仍然慢。

常见原因：

- 跨地域/跨运营商直连 COS 慢
- URL 带 `X-Amz-*` 签名参数，导致边缘缓存难命中

建议：

- 用 **腾讯云 CDN 加速 COS**
- 使用 CDN 的 **URL 鉴权/Token 鉴权** 或 **Referer 防盗链**，尽量避免每次生成不同的带签名 Query URL

---

## 9. 命令汇总（可复制）

### 9.1 Docker

```bash
cd /opt/VideoWeb-master/deploy/docker
cp .env.example .env
vi .env

docker compose up -d --build

docker compose ps

docker compose logs -f --tail=200

docker compose up -d --build web

docker compose up -d --build backend

# 慎用：清空数据库
docker compose down -v
```

### 9.2 Cloudflare Tunnel

```bash
sudo mkdir -p /usr/local/bin
curl -L -o /usr/local/bin/cloudflared https://github.com/cloudflare/cloudflared/releases/latest/download/cloudflared-linux-amd64
sudo chmod +x /usr/local/bin/cloudflared

cloudflared tunnel login

# cert.pem 手动放置
mkdir -p /root/.cloudflared
mv /root/cert.pem /root/.cloudflared/cert.pem
chmod 600 /root/.cloudflared/cert.pem

cloudflared tunnel create streamverse

mkdir -p /etc/cloudflared
vi /etc/cloudflared/config.yml

cloudflared tunnel route dns streamverse sv.example.com

cloudflared service install
systemctl enable --now cloudflared
systemctl status cloudflared --no-pager
journalctl -u cloudflared -f
```

---

## 10. 总结

这次部署的关键经验：

- 大量文件上传用压缩包最稳
- nginx 的 `proxy_pass` 末尾 `/` 会影响转发路径，容易导致 `/api` 全 404
- NAT 环境下 Cloudflare Tunnel 是“无公网 IP 上线”的利器
- QUIC/UDP 在某些网络不稳定，切到 http2(TCP) 会更可靠
- 视频慢通常不是 Tunnel 的锅，而是对象存储链路/缓存策略问题，COS 配合 CDN 才是正解
