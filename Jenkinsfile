pipeline{
  
  agent { label 'agent1' }
  tools{
         maven 'Maven'
        
           }
  stages{
    stage("java version") {
      steps{
        echo "checking the java version...."
        sh 'echo %JAVA_HOME%'
        sh 'java -version'
        sh 'ifconfig -a'
       
      }
      
    }
    stage(" increment stage"){
                steps{
                      script{
                         echo ' increment app version....'
                            sh'mvn -f pom.xml build-helper:parse-version versions:set \
                             -DnewVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.minorVersion}.\\\${parsedVersion.nextIncrementalVersion} \
                             versions:commit'
                            echo 'befor read'
                            def matcher = readFile('pom.xml') =~ '<version>(.+)</version>'
                             echo 'after read'
                            def version = matcher[0][1]
                             echo 'after matcher'
                            env.IMAGE_NAME="$version-$BUILD_NUMBER"
                          echo 'end incre '
                              }
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
        sh "docker build -t mokhtar194/timesheet:${IMAGE_NAME} ."
          echo "docker image built..."
        sh "echo $PASS | docker login -u $USER --password-stdin"
        sh " docker push  mokhtar194/timesheet:${IMAGE_NAME} "
          echo "docker image pushed..."
        }
      }
      
    }
    stage("deploy") {
      steps{
        script{
          echo "deploying the application... qwWxneG6abGr#qq"
          withCredentials([usernamePassword(credentialsId:'docker-hub-repo',passwordVariable:'PASS',usernameVariable:'USER')])
        {
         // echo"remove the images step"
          //sh"sshpass -p 'Ubuntu' ssh root@192.68.100.6 docker rmi \$(docker images 'mokhtar194/t*')"
          //echo"remove the images step done"
          sh"sshpass -p 'Ubuntu' ssh root@192.68.100.6 docker pull mokhtar194/timesheet:${IMAGE_NAME}"
          sh"sshpass -p 'Ubuntu' ssh root@192.68.100.6 docker run -p 8085:8085 -- name=timesheet${IMAGE_NAME} -d --network=mysql-phpmyadmin mokhtar194/timesheet:${IMAGE_NAME}"
        }
        }
        
        
      }
      
    }
  
  }
}
