FROM m.daocloud.io/docker.io/library/node:20-alpine AS build
WORKDIR /app
COPY StreamVerseWeb/package.json StreamVerseWeb/package-lock.json* ./
RUN npm ci || npm install
COPY StreamVerseWeb ./StreamVerseWeb
WORKDIR /app/StreamVerseWeb
RUN npm run build

FROM m.daocloud.io/docker.io/library/nginx:1.27-alpine
COPY deploy/docker/web/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/StreamVerseWeb/dist /usr/share/nginx/html
EXPOSE 80
