# 根目录 Dockerfile - 项目默认构建入口
# 本项目为前后端分离架构，推荐使用 docker compose 一键启动
# 如需单独构建服务，请使用各子目录下的 Dockerfile

FROM nginx:alpine

LABEL maintainer="blindbox-platform"
LABEL description="Blind Box Vending Machine Platform - Root Dockerfile"

WORKDIR /usr/share/nginx/html

# 说明：根目录 Dockerfile 主要用于项目标识和一键演示
# 完整功能请使用根目录 docker-compose.yml 启动所有服务

COPY README.md /usr/share/nginx/html/

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
