pipeline{
  agent any 
  tools{
         maven 'Maven'
        
           }
  stages{
    stage("java version") {
      steps{
        echo "checking the java version..."
        sh 'echo %JAVA_HOME%'
        sh 'java -version'
       
      }
      
    }
    stage("build jar") {
      steps{
        echo "building the application..."
        sh 'mvn package -Dmaven.test.skip'
        echo "building the application done!"
       
      }
      
    }
    stage("build image") {
      steps{
        echo "building the docker image..."
        withCredentials([usernamePassword(credentialsId:'docker-hub-repo',passwordVariable:'PASS',usernameVariable:'USER')])
        {
     
        echo "credentials uploaded!"
        sh 'docker build -t mokhtar194/timesheet:tm-2.0 .'
          echo "docker image built..."
        sh "echo $PASS | docker login -u $USER --password-stdin"
        sh ' docker push  mokhtar194/timesheet:tm-2.0 '
          echo "docker image pushed..."
        }
      }
      
    }
    stage("deploy") {
      steps{
        script{
          echo "deploying the application... "
        }
        
        
      }
      
    }
  
  }
}