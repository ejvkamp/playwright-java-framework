pipeline {
 agent any // Run on any available "Stage Crew" (Agent)

 tools {
  // Critical: These names must EXACTLY match the "Name" field in 
  // Manage Jenkins > Global Tool Configuration
   maven 'Maven' 
   jdk 'JDK-21'
 }

 stages {
  stage('Checkout') {
    steps {
	// Check out the code from the Git repository
    // IMPORTANT: Without "branch: 'main'", the pipeline defaults to
    // checking out 'master' even if your job config is set to 'main'
    git branch: 'main', url: 'https://github.com/ejvkamp/playwright-java-framework.git'
    }
  }

  stage('Install Browsers') {
    steps {
      script {
		 // OPTIMIZATION: Check if browsers are already installed to save time.
 		// Playwright stores binaries in specific cache folders.
 		def browsersInstalled = fileExists("${env.HOME}/.cache/ms-playwright") || 
 		  fileExists("${env.USERPROFILE}\\AppData\\Local\\ms-playwright")
 
 	if (!browsersInstalled) {
    	 echo "Playwright browsers not found. Installing..."
		
      // Install Playwright browsers (safe to run multiple times)
         if (isUnix()) {
                 sh 'mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install --with-deps"'
              } else {
            bat 'mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install --with-deps"'
			  } else {
				  echo "Playwright browsers already installed. Skipping download."
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
