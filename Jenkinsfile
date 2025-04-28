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
                    // Ejecuta los tests con el profile "test" que levanta application-test.properties
                    bat "mvn test"
                }
            }
        }

        stage("Build and Push Docker Image") {
            environment {
                // Forzar Docker CLI a usar el contexto 'default'
                DOCKER_CONTEXT = 'default'
            }
            steps {
                dir("kartingrm") {
                    // Asegura que no intente usar 'desktop-linux'
                    bat 'docker context use default || true'
                    script {
                        docker.withRegistry('https://index.docker.io/v1/', 'docker-credentials') {
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
