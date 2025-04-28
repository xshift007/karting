# ---------- build ----------
    FROM node:20-alpine AS build
    WORKDIR /app
    COPY package*.json ./
    RUN npm ci
    COPY . .
    RUN npm run build          # genera /dist
    
    # ---------- runtime ----------
    FROM nginx:stable-alpine
    COPY --from=build /app/dist /usr/share/nginx/html
    # Nginx que sirve el estático y reenvía /api →
    RUN rm /etc/nginx/conf.d/default.conf
    COPY nginx.conf /etc/nginx/conf.d/default.conf
    EXPOSE 80
    