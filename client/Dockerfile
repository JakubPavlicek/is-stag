# Use Node.js Alpine image
FROM node:24-alpine3.21

# Set working directory
WORKDIR /app

# Copy package files
COPY package.json package-lock.json* ./

# Install dependencies
RUN npm ci

# Copy source code
COPY . .

# Build the application
RUN npm run build

# Expose port 5173
EXPOSE 5173

# Start the preview server
CMD ["npm", "run", "dev", "--", "--host"]
