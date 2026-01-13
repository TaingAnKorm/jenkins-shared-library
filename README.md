# Jenkins Shared Library for Docker Injection

This Jenkins Shared Library provides runtime Dockerfile injection for Spring Boot and Next.js applications, keeping infrastructure code separate from application repositories.

## 📁 Directory Structure

```
jenkins-shared-library/
├── resources/
│   ├── spring/
│   │   └── dev.Dockerfile          # Multi-stage Dockerfile for Spring Boot
│   └── reactjs/
│       └── dev.Dockerfile          # Multi-stage Dockerfile for Next.js
├── vars/
│   └── deployApp.groovy            # Global variable for deployment
├── examples/
│   ├── Jenkinsfile.backend         # Sample Jenkinsfile for Spring Boot
│   └── Jenkinsfile.frontend        # Sample Jenkinsfile for Next.js
└── README.md
```

## 🚀 Features

- **Runtime Dockerfile Injection**: Dockerfiles are stored in the shared library and injected at build time
- **Multi-Stage Builds**: Optimized Dockerfiles for production deployments
- **Automated Container Management**: Handles Docker build, stop, and run operations
- **Zero Infrastructure in App Repos**: Application repositories remain clean of Docker files

## 📋 Prerequisites

- Jenkins with Pipeline plugin
- Docker installed on Jenkins agent
- Git access to application repositories

## ⚙️ Setup

### 1. Configure Shared Library in Jenkins

1. Go to **Manage Jenkins** → **Configure System**
2. Scroll to **Global Pipeline Libraries**
3. Click **Add** and configure:
   - **Name**: `jenkins-shared-library`
   - **Default version**: `main` (or your branch name)
   - **Retrieval method**: Modern SCM
   - **Source Code Management**: Git
   - **Project Repository**: URL to this repository
   - **Behaviors**: Discover branches

### 2. Use in Your Jenkinsfile

#### For Spring Boot Application

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
                script {
                    deployApp('spring')
                }
            }
        }
    }
}
```

#### For Next.js Application

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
                script {
                    deployApp('reactjs')
                }
            }
        }
    }
}
```

## 📖 API Reference

### `deployApp(appType, containerName, port)`

Deploys an application using Docker with runtime Dockerfile injection.

**Parameters:**

- `appType` (String, required): Type of application
  - `'spring'` - Spring Boot application
  - `'reactjs'` - Next.js application
- `containerName` (String, optional): Custom container name
  - Default: `${appType}-app`
- `port` (String, optional): Custom port mapping
  - Default: `8080` for Spring, `3000` for Next.js

**Examples:**

```groovy
// Basic usage
deployApp('spring')

// Custom container name
deployApp('spring', 'my-backend-app')

// Custom port
deployApp('reactjs', 'my-frontend-app', '3001')
```

## 🐳 Dockerfile Details

### Spring Boot Dockerfile

- **Base Images**: 
  - Builder: `gradle:8.6-jdk21`
  - Runtime: `eclipse-temurin:21-jre`
- **Build Tool**: Gradle 8.6
- **Java Version**: JDK 21
- **Port**: 8080
- **Optimization**: Multi-stage build with minimal runtime image

### Next.js Dockerfile

- **Base Image**: `node:20-alpine`
- **Build Strategy**: 
  - Stage 1: Install production dependencies
  - Stage 2: Build application
  - Stage 3: Run with standalone output
- **Port**: 3000
- **Security**: Runs as non-root user
- **Optimization**: Standalone output for minimal image size

## 🔧 Customization

### Adding New Application Types

1. Create a new Dockerfile in `resources/<app-type>/dev.Dockerfile`
2. Update `deployApp.groovy` to include the new app type in validation
3. Add default port mapping in the `defaultPorts` map

### Modifying Dockerfiles

Edit the Dockerfiles in the `resources/` directory. Changes will automatically apply to all pipelines using the shared library.

## 📝 Example Jenkinsfiles

Complete example Jenkinsfiles are available in the `examples/` directory:

- [`Jenkinsfile.backend`](examples/Jenkinsfile.backend) - Spring Boot deployment
- [`Jenkinsfile.frontend`](examples/Jenkinsfile.frontend) - Next.js deployment

## 🎯 Target Repositories

This shared library is designed for:

- **Backend**: [prodstack](https://github.com/tochratana/prodstack) - Spring Boot application
- **Frontend**: [prodstack-ui](https://github.com/tochratana/prodstack-ui) - Next.js application

## 🔒 Security Notes

- Dockerfiles are version-controlled in the shared library
- No sensitive information in application repositories
- Container runs with appropriate user permissions
- Automatic cleanup of old containers

## 📚 Additional Resources

- [Jenkins Shared Libraries Documentation](https://www.jenkins.io/doc/book/pipeline/shared-libraries/)
- [Docker Multi-Stage Builds](https://docs.docker.com/build/building/multi-stage/)
- [Spring Boot with Docker](https://spring.io/guides/gs/spring-boot-docker/)
- [Next.js Docker Deployment](https://nextjs.org/docs/deployment#docker-image)

## 📄 License

This project is part of the DevOps ITP curriculum at ISTAD.
