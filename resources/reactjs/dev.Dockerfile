# Stage 1: Install dependencies
FROM node:20-alpine AS deps

WORKDIR /prod-stack-ui

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm ci --only=production

# Stage 2: Build the application
FROM node:20-alpine AS builder

WORKDIR /prod-stack-ui

# Copy dependencies from deps stage
COPY --from=deps /prod-stack-ui/node_modules ./node_modules

# Copy all source files
COPY . .

# Build the Next.js application
RUN npm run build

# Stage 3: Production runtime
FROM node:20-alpine AS runner

WORKDIR /prod-stack-ui

# Set environment to production
ENV NODE_ENV=production

# Create a non-root user
RUN addgroup --system --gid 1001 nodejs
RUN adduser --system --uid 1001 nextjs

# Copy necessary files from builder
COPY --from=builder /prod-stack-ui/public ./public
COPY --from=builder /prod-stack-ui/.next/standalone ./
COPY --from=builder /prod-stack-ui/.next/static ./.next/static

# Change ownership to nextjs user
RUN chown -R nextjs:nodejs /prod-stack-ui

# Switch to non-root user
USER nextjs

# Expose application port
EXPOSE 3000

# Set hostname
ENV HOSTNAME="0.0.0.0"

# Start the application
CMD ["node", "server.js"]
