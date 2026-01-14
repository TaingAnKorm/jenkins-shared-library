def call(String imageName, String imageTag = 'latest', String dockerUsername, String credentialsId = 'DOCKERHUB', String registry = 'docker.io') {
    // Build the full image name with registry
    def fullImageName = "${registry}/${imageName}:${imageTag}"
    
    echo "Pushing Docker image: ${fullImageName}"
    
    // Use Jenkins secret text credential (Docker token) to login and push
    withCredentials([string(credentialsId: credentialsId, variable: 'DOCKER_TOKEN')]) {
        sh """
            echo \$DOCKER_TOKEN | docker login ${registry} -u ${dockerUsername} --password-stdin
            docker push ${fullImageName}
            docker logout ${registry}
        """
    }
    
    echo "Successfully pushed Docker image: ${fullImageName}"
}
