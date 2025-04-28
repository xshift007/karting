pipeline {
    agent any

    tools {
        maven 'maven'
    }

    environment {
        // Forzar Docker CLI a usar el contexto 'default'
        DOCKER_CONTEXT = 'default'
    }

    stages {
        stage('Checkout') {
            steps {
                // Clona todo el repo, incluidas las carpetas frontend/backend
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/main']],
                    doGenerateSubmoduleConfigurations: false,
                    userRemoteConfigs: [[
                        url: 'https://github.com/xshift007/karting.git',
                        credentialsId: 'git-credentials'
                    ]]
                ])
            }
        }

        stage('Build JAR File') {
            steps {
                dir('kartingrm') {
                    // Compila el backend
                    bat 'mvn clean install'
                }
            }
        }

        stage('Test') {
            steps {
                dir('kartingrm') {
                    // Ejecuta todos los tests (los tests de Spring usan @ActiveProfiles("test"))
                    bat 'mvn test'
                }
            }
        }

        stage('Build & Push Docker Image') {
            steps {
                dir('kartingrm') {
                    // Asegura que Docker use el contexto 'default'
                    bat 'docker context use default || true'
                    script {
                        // Aquí se usará la credencial ID 'docker-credentials' que ya tienes en Jenkins
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
