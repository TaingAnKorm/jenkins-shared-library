# Quick Start Guide

## 🚀 5-Minute Setup

### 1. Configure Jenkins (One-time setup)

```
Jenkins → Manage Jenkins → Configure System → Global Pipeline Libraries
```

**Settings**:
- Name: `jenkins-shared-library`
- Default version: `main`
- Retrieval method: Modern SCM → Git
- Project Repository: `/home/kta/Documents/ISTAD/DevOps_ITP/DevOps_shared_library`

### 2. Deploy Spring Boot App

**Create Pipeline Job** → Paste this:

```groovy
@Library('jenkins-shared-library') _

pipeline {
    agent any
    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/tochratana/prodstack.git'
            }
        }
        stage('Deploy') {
            steps {
                script { deployApp('spring') }
            }
        }
    }
}
```

**Run** → Access at `http://localhost:8080`

### 3. Deploy Next.js App

**Create Pipeline Job** → Paste this:

```groovy
@Library('jenkins-shared-library') _

pipeline {
    agent any
    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/tochratana/prodstack-ui.git'
            }
        }
        stage('Deploy') {
            steps {
                script { deployApp('reactjs') }
            }
        }
    }
}
```

> ⚠️ **Important**: Add `output: 'standalone'` to `next.config.ts` first!

**Run** → Access at `http://localhost:3000`

---

## 📝 Common Commands

### Check Running Containers
```bash
docker ps
```

### View Logs
```bash
docker logs spring-app
docker logs reactjs-app
```

### Stop Containers
```bash
docker stop spring-app reactjs-app
docker rm spring-app reactjs-app
```

### Custom Deployment
```groovy
// Custom container name
deployApp('spring', 'my-backend')

// Custom port
deployApp('reactjs', 'my-frontend', '8080')
```

---

## 🎯 What This Does

1. ✅ Clones your app repository
2. ✅ Injects the appropriate Dockerfile
3. ✅ Builds Docker image
4. ✅ Stops old container (if exists)
5. ✅ Starts new container
6. ✅ Verifies deployment

**No Dockerfile needed in your app repo!** 🎉

---

## 📚 Full Documentation

- [README.md](README.md) - Complete guide
- [CONFIGURATION.md](CONFIGURATION.md) - Config notes
- [Walkthrough](../../.gemini/antigravity/brain/fe91af4d-b06f-4689-a608-c9c424409e7c/walkthrough.md) - Implementation details

---

## 🆘 Troubleshooting

### Container won't start?
```bash
docker logs <container-name>
```

### Port already in use?
```bash
# Find what's using the port
sudo lsof -i :8080
sudo lsof -i :3000

# Or use custom port
deployApp('spring', 'spring-app', '9090')
```

### Next.js build fails?
Add to `next.config.ts`:
```typescript
const nextConfig: NextConfig = {
  output: 'standalone',
};
```

### Library not found?
Check Jenkins → Manage Jenkins → Configure System → Global Pipeline Libraries
