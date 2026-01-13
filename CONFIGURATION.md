# Important Configuration Notes

## Next.js Standalone Output Requirement

For the Next.js Dockerfile to work correctly, the application **must** have standalone output enabled in `next.config.ts`.

### Required Configuration

Add the following to your `next.config.ts` or `next.config.js`:

```typescript
import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  output: 'standalone', // This is required for Docker deployment
  // ... other configurations
};

export default nextConfig;
```

Or for JavaScript config:

```javascript
/** @type {import('next').NextConfig} */
const nextConfig = {
  output: 'standalone', // This is required for Docker deployment
  // ... other configurations
};

module.exports = nextConfig;
```

### Why is this needed?

The `standalone` output mode:
- Creates a minimal production build
- Includes only necessary files
- Reduces Docker image size significantly
- Bundles all dependencies into a single `server.js` file

### Alternative: Standard Next.js Deployment

If you cannot use standalone mode, use this alternative Dockerfile:

```dockerfile
# Stage 1: Install dependencies
FROM node:20-alpine AS deps
WORKDIR /prod-stack-ui
COPY package*.json ./
RUN npm ci

# Stage 2: Build
FROM node:20-alpine AS builder
WORKDIR /prod-stack-ui
COPY --from=deps /prod-stack-ui/node_modules ./node_modules
COPY . .
RUN npm run build

# Stage 3: Production
FROM node:20-alpine AS runner
WORKDIR /prod-stack-ui
ENV NODE_ENV=production

RUN addgroup --system --gid 1001 nodejs
RUN adduser --system --uid 1001 nextjs

COPY --from=builder /prod-stack-ui/public ./public
COPY --from=builder /prod-stack-ui/.next ./.next
COPY --from=builder /prod-stack-ui/node_modules ./node_modules
COPY --from=builder /prod-stack-ui/package.json ./package.json

RUN chown -R nextjs:nodejs /prod-stack-ui
USER nextjs

EXPOSE 3000
ENV HOSTNAME="0.0.0.0"

CMD ["npm", "start"]
```

## Spring Boot Notes

The Spring Boot Dockerfile works out of the box with the current configuration. No additional changes needed to the application.

### Gradle Version

The Dockerfile uses Gradle 8.6 which matches the wrapper version in the repository. If you upgrade Gradle, update the Dockerfile accordingly:

```dockerfile
FROM gradle:X.Y-jdk21 AS builder
```

## Docker Resource Requirements

### Minimum Requirements

- **Spring Boot**: 512MB RAM, 1 CPU
- **Next.js**: 256MB RAM, 1 CPU

### Recommended for Production

- **Spring Boot**: 1GB RAM, 2 CPUs
- **Next.js**: 512MB RAM, 1 CPU

## Port Configuration

Default ports can be customized in the Jenkinsfile or when calling `deployApp()`:

```groovy
// Custom port example
deployApp('spring', 'my-backend', '9090')
deployApp('reactjs', 'my-frontend', '8080')
```
