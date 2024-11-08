pipeline {
    agent any
    tools {
        maven 'maven_3_8_1'
    }
    stages {

        stage('Checkout repository') {
            steps {
                checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/Ghox19/Tingeso-1']])
            }
        }

        stage('Build backend') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'cd prestaBancoBackend && docker build -t ghox19/backend-presta-banco:latest .'
                    } else {
                        bat 'cd prestaBancoBackend && docker build -t ghox19/backend-presta-banco:latest .'
                    }
                }
                
                withCredentials([string(credentialsId: 'dhpswid', variable: 'dhpsw')]) {
                    script {
                        if (isUnix()) {
                            sh 'docker login -u ghox19 -p $dhpsw'
                        } else {
                            bat 'docker login -u ghox19 -p %dhpsw%'
                        }
                    }
                }

                script {
                    if (isUnix()) {
                        sh 'docker login'
                        sh 'docker push ghox19/backend-presta-banco:latest'
                    } else {
                        bat 'docker push ghox19/backend-presta-banco:latest'
                    }
                }
            }
        }

        stage('Test backend') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'cd prestaBancoBackend && mvn test'
                    } else {
                        bat 'cd prestaBancoBackend && mvn test'
                    }
                }
            }
        }

        stage('Build frontend') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'cd prestabancofrontend && docker build -t ghox19/frontend-presta-banco:latest .'
                    } else {
                        bat 'cd prestabancofrontend && docker build -t ghox19/frontend-presta-banco:latest .'
                    }
                }

                withCredentials([string(credentialsId: 'dhpswid', variable: 'dhpsw')]) {
                    script {
                        if (isUnix()) {
                            sh 'docker login -u ghox19 -p $dhpsw'
                        } else {
                            bat 'docker login -u ghox19 -p %dhpsw%'
                        }
                    }
                }

                script {
                    if (isUnix()) {
                        sh 'docker push ghox19/frontend-presta-banco:latest'
                    } else {
                        bat 'docker push ghox19/frontend-presta-banco:latest'
                    }
                }
            }
        }

    }
}