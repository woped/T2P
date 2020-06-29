pipeline {
    environment {
        MVN_SET = credentials('nexus-credentials')
    }
    agent {
        docker {
            image 'maven:3.6.3-jdk-11'
            args '-u root'
        }
    }

    stages {
        stage('build') {
            steps {
                sh 'mvn install -Dmaven.test.skip=true'
            }
        }
    }
}