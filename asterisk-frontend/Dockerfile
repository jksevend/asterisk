# Build the whole nx workspace
FROM node:lts-alpine AS builder

WORKDIR /app

COPY ./ /app

RUN npm i && npm run build

# Setup nginx to serve the website app
FROM nginx:alpine as website

WORKDIR /usr/share/nginx/html

RUN rm -rf ./*

ADD apps/website/nginx-default.conf /etc/nginx/conf.d/default.conf

COPY --from=builder /app/dist/apps/website .

EXPOSE 80
