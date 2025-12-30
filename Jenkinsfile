pipeline {
 agent any // Run on any available "Stage Crew" (Agent)

 tools {
   maven 'Maven-3.9' // Must match Global Tool Config name
   jdk 'JDK-21'
 }

 stages {
  stage('Checkout') {
    steps {
    git 'https://github.com/ejvkamp/playwright-java-framework.git'
    }
  }

  stage('Install Browsers') {
    steps {
      script {
      // Install Playwright browsers (safe to run multiple times)
         if (isUnix()) {
                 sh 'mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install --with-deps"'
              } else {
            bat 'mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install --with-deps"'
         }
             }
          }
  }

  stage('Run Tests') {
    steps {
      script {
    if (isUnix()) {
            sh 'mvn clean test'
         } else {
                 bat 'mvn clean test'
                    }
                }
            }
        }
    }

 post {
  always {
  // Publish reports regardless of pass/fail
  junit '**/target/surefire-reports/*.xml'
  archiveArtifacts artifacts: 'traces/**/*.zip, screenshots/**/*.png', allowEmptyArchive: true
    }
  }
}
