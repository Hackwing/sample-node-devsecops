pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'myapp-frontend'
        SONAR_PROJECT_KEY = 'my-angular-frontend'
    }

    stages {
        stage('Clean Workspace') {
            steps {
                cleanWs()
            }
        }

        // stage('Checkout Code') {
        //     steps {
        //         git branch: 'main', url: 'https://github.com/Hackwing/sample-node-devsecops.git'
        //     }
        // }

        stage('Install Dependencies') {
            steps {
                dir('frontend') {
                    sh 'npm install'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                dir('frontend') {
                    withSonarQubeEnv('SonarQube') {
                        sh 'npx sonar-scanner'
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                dir('frontend') {
                    sh 'docker build -t $DOCKER_IMAGE .'
                }
            }
        }

        stage('Security Scan (Trivy)') {
            steps {
                sh 'trivy image --exit-code 0 --severity LOW,MEDIUM $DOCKER_IMAGE'
                sh 'trivy image --exit-code 1 --severity HIGH,CRITICAL $DOCKER_IMAGE || true'
            }
        }

        stage('Run Frontend in Docker') {
            steps {
                sh 'docker run -d -p 4200:80 $DOCKER_IMAGE'
            }
        }
    }
}
