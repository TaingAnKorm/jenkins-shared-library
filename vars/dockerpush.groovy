def call(String imageName, String imageTag = 'latest', String registry = '') {
    // Build the full image name with optional registry
    def fullImageName = registry ? "${registry}/${imageName}:${imageTag}" : "${imageName}:${imageTag}"
    
    echo "Pushing Docker image: ${fullImageName}"
    
    sh """
        docker push ${fullImageName}
    """
    
    echo "Successfully pushed Docker image: ${fullImageName}"
}
