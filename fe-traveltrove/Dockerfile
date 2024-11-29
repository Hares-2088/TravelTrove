# Use an official Node.js Runtime as a base image
FROM node:21 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy package.json and package-lock.json to the working directory
COPY package*.json ./

# Install Project dependencies
RUN npm install

# Copy the rest of the application code to the working directory
COPY . .

# Build the React app (default is production mode)
RUN npm run build

# Use a smaller, lightweight base image for the final image
FROM nginx:alpine

# Adjust permissions for Nginx
RUN chown -R nginx:nginx /var/cache/nginx /var/run /var/log/nginx
RUN chown -R nginx:0 /usr/share/nginx/html && \
    chmod -R g+r+rwX /usr/share/nginx/html

# Copy the build output to NGINX's default static file serving directory
COPY --from=build /app/build /usr/share/nginx/html

# Copy the default NGINX configuration file
COPY nginx.conf /etc/nginx/nginx.conf

# Expose port 80 for the NGINX web server
EXPOSE 80

# Command to run NGINX
CMD ["nginx", "-g", "daemon off;"]
