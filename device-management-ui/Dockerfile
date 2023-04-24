# STAGE 1: Build app
FROM node:16-alpine as build

# Make /app as working directory
WORKDIR /app

COPY ./src /app/src/
COPY *.* /app/

# Install all the dependencies
RUN npm install && npm run build

# STAGE 2: Serve app with NgInx
FROM nginx:alpine

# Copy dist folder from build stage to nginx public folder
COPY  --from=build /app/dist/device-management-ui /usr/share/nginx/html
COPY ./nginx.conf /etc/nginx/conf.d/default.conf

# Start NgInx service
CMD ["/bin/sh",  "-c",  "envsubst < /usr/share/nginx/html/assets/env.template.js > /usr/share/nginx/html/assets/env.js && exec nginx -g 'daemon off;'"]

# Expose port 80
EXPOSE 80
