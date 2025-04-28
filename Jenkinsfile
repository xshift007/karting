pipeline {
    agent any
    tools {
        maven "maven"
    }
    environment {
        // Force Docker CLI to use the default context instead of desktop-linux
        DOCKER_CONTEXT = 'default'
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
                    // Activa el profile "test" para que cargue application-test.properties
                    bat "mvn test -Ptest"
                }
            }
        }

        stage("Build and Push Docker Image") {
            steps {
                dir("kartingrm") {
                    script {
                        docker.withRegistry('https://index.docker.io/v1/', 'dockerhub-credentials-id') {
                            def img = docker.build("xsh1ft/kartingrm:${env.BUILD_NUMBER}")
                                img.push()
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
