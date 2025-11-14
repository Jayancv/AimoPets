# Build stage
FROM node:20-bullseye  AS build

WORKDIR /usr/src/app

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

# Production stage
FROM nginx:1.25-alpine

COPY --from=build /usr/src/app/dist /usr/share/nginx/html

# Copy custom nginx config if needed
COPY default.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]

