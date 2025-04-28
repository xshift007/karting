pipeline {
    agent any
    tools {
        maven "maven"
    }
    stages {
        stage("Build JAR File") {
            steps {
                // Clonar tu repo (rama main)
                checkout scmGit(
                    branches: [[name: '*/main']],
                    extensions: [],
                    userRemoteConfigs: [[
                        url: 'https://github.com/xshift007/karting.git'
                    ]]
                )
                // Entrar en el módulo backend y compilar
                dir("kartingrm") {
                    bat "mvn clean install"
                }
            }
        }
        stage("Test") {
            steps {
                dir("kartingrm") {
                    bat "mvn test"
                }
            }
        }
        stage("Build and Push Docker Image") {
            steps {
                dir("kartingrm") {
                    script {
                        // Autenticarse en DockerHub con la credencial 'docker-credentials'
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
