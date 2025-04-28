pipeline {
    agent any
    tools {
        maven "maven"
    }
    stages {
        stage("Checkout") {
            steps {
                checkout scmGit(
                    branches: [[name: '*/main']],
                    userRemoteConfigs: [[url: 'https://github.com/xshift007/karting.git']]
                )
            }
        }
        stage("Build JAR & Skip Tests") {
            steps {
                dir("kartingrm") {
                    // limpia, empaqueta y omite tests
                    bat "mvn clean install -DskipTests"
                }
            }
        }
        stage("Build and Push Docker Image") {
            steps {
                dir("kartingrm") {
                    script {
                        withDockerRegistry(credentialsId: 'docker-credentials') {
                            bat "docker build -t xsh1ft/kartingrm-backend:latest ."
                            bat "docker push xsh1ft/kartingrm-backend:latest"
                        }
                    }
                }
            }
        }
    }
    post {
        always {
            cleanWs()
        }
    }
}
