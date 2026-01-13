#!/usr/bin/env groovy

/**
 * Deploy application using Docker with runtime Dockerfile injection
 * 
 * @param appType The type of application to deploy ('spring' or 'reactjs')
 * @param containerName Optional custom container name (defaults to appType-app)
 * @param port Optional custom port mapping (defaults to 8080 for spring, 3000 for reactjs)
 */
def call(String appType, String containerName = null, String port = null) {
    // Validate appType
    if (!['spring', 'reactjs'].contains(appType)) {
        error("Invalid appType: ${appType}. Must be 'spring' or 'reactjs'")
    }
    
    // Set defaults based on appType
    def defaultPorts = [
        'spring': '8080',
        'reactjs': '3000'
    ]
    
    def actualContainerName = containerName ?: "${appType}-app"
    def actualPort = port ?: defaultPorts[appType]
    def imageName = "${appType}-app:latest"
    
    echo "========================================="
    echo "Deploying ${appType} application"
    echo "Container: ${actualContainerName}"
    echo "Port: ${actualPort}"
    echo "Image: ${imageName}"
    echo "========================================="
    
    // Step 1: Inject Dockerfile from library resources
    echo "Step 1: Injecting Dockerfile from shared library..."
    def dockerfileContent = libraryResource("${appType}/dev.Dockerfile")
    writeFile file: 'Dockerfile', text: dockerfileContent
    echo "✓ Dockerfile injected successfully"
    
    // Step 2: Build Docker image
    echo "\nStep 2: Building Docker image..."
    sh """
        docker build -t ${imageName} .
    """
    echo "✓ Docker image built successfully"
    
    // Step 3: Stop and remove existing container (if exists)
    echo "\nStep 3: Stopping existing container (if any)..."
    sh """
        docker stop ${actualContainerName} 2>/dev/null || true
        docker rm ${actualContainerName} 2>/dev/null || true
    """
    echo "✓ Existing container cleaned up"
    
    // Step 4: Run new container
    echo "\nStep 4: Starting new container..."
    sh """
        docker run -d \
            --name ${actualContainerName} \
            -p ${actualPort}:${actualPort} \
            --restart unless-stopped \
            ${imageName}
    """
    echo "✓ Container started successfully"
    
    // Step 5: Verify container is running
    echo "\nStep 5: Verifying deployment..."
    def containerStatus = sh(
        script: "docker ps --filter name=${actualContainerName} --format '{{.Status}}'",
        returnStdout: true
    ).trim()
    
    if (containerStatus) {
        echo "✓ Deployment successful!"
        echo "Container Status: ${containerStatus}"
        echo "Application is accessible at: http://localhost:${actualPort}"
    } else {
        error("Container failed to start. Check Docker logs with: docker logs ${actualContainerName}")
    }
    
    echo "========================================="
}
