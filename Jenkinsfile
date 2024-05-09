pipeline{
  
  agent { label 'agent1' }
  tools{
         maven 'Maven'
        
           }
  stages{
    stage("Java version") {
      steps{
        echo "checking the java version..."
        sh 'echo %JAVA_HOME%'
        sh 'java -version'
        sh 'ifconfig -a'
       
      }
      
    }
    stage('Checkout') {
                steps {
                    scmSkip(deleteBuild: true, skipPattern:'.*\\[ci skip\\].*')
                }
            }
    stage("Increment stage"){
                steps{
                      script{
                         echo ' increment app version...'
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
    stage("Build jar") {
      steps{
        echo "building the application..."
        sh"rm -rf target/"
        sh 'mvn package -Dmaven.test.skip'
        echo "building the application done!"
       
      }
      
    }
    stage("Build image") {
      steps{
        echo "building the docker image..."
        withCredentials([usernamePassword(credentialsId:'nexus-cred',passwordVariable:'PASS',usernameVariable:'USER')])
        {
     
        echo "credentials uploaded!"
        sh "docker build -t 192.68.100.5:8443/timesheet:${IMAGE_NAME} ."
          echo "docker image built..."
        sh "echo $PASS | docker login -u $USER --password-stdin 192.68.100.5:8443"
        sh " docker push  192.68.100.5:8443/timesheet:${IMAGE_NAME} "
          echo "docker image pushed..."
        }
      }
      
    }
    stage("Deployment") {
      steps{
        script{
          echo "deploying the application... "
          withCredentials([usernamePassword(credentialsId:'nexus-cred',passwordVariable:'PASS',usernameVariable:'USER')])
        {
         // echo"remove the images step"
          //sh"sshpass -p 'Ubuntu' ssh root@192.68.100.6 docker rmi \$(docker images 'mokhtar194/t*')"
          //echo"remove the images step done"
          sh"sshpass -p 'Ubuntu' ssh root@192.68.100.6 echo $PASS | docker login -u $USER   --password-stdin 192.68.100.5:8443"
          sh"sshpass -p 'Ubuntu' ssh root@192.68.100.6 docker pull 192.68.100.5:8443/timesheet:${IMAGE_NAME}"
          //def cont_id = sh(script:"sshpass -p 'Ubuntu' ssh root@192.68.100.6 docker ps -aq ",returnStdout:true).trim()
          //env.CONTAINER_NAME="$cont_id"
          //sh"sshpass -p 'Ubuntu' ssh root@192.68.100.6 docker stop ${CONTAINER_NAME}"
          //sh"sshpass -p 'Ubuntu' ssh root@192.68.100.6 docker run -p 8085:8085  --network host --name=timesheet${IMAGE_NAME}  -d  192.68.100.5:8443/timesheet:${IMAGE_NAME}"
         //sh"sshpass -p 'Ubuntu' ssh root@192.68.100.6 microk8s kubectl set image deployment.apps/tm tm=192.68.100.5:8443/timesheet:${IMAGE_NAME} -n nexus-namespace"
          sh"ansible-playbook -v playbook.yml"
          sh """sed -i 's|image: 192.68.100.5:8443/timesheet:[^"]*|image: 192.68.100.5:8443/timesheet:${IMAGE_NAME}|g' k8s.yml""" 
          sh"sshpass -p 'Ubuntu' scp k8s.yml root@192.68.100.6:~/workspace"
          sh"sshpass -p 'Ubuntu' ssh root@192.68.100.6  microk8s kubectl apply -f ~/workspace/k8s.yml"
          sh"sshpass -p 'Ubuntu' ssh root@192.68.100.6  rm ~/workspace/k8s.yml"
          sh" scp k8s.yml root@192.68.100.7:~/workspace"
          sh"ssh root@192.68.100.7  microk8s kubectl apply -f ~/workspace/k8s.yml"
          sh"ssh root@192.68.100.7 rm ~/workspace/k8s.yml"

        }
        }
        
        
      }
      
    }
    stage("Test deployment"){
      steps{
        script{
          echo"//////////////////////////////////////////////////////////"
          echo"Testing the deployment"
          sleep time: 30, unit: 'SECONDS'
          sh"sshpass -p 'Ubuntu' ssh root@192.68.100.6 curl 10.152.183.167/login"
          //sleep time: 30, unit: 'SECONDS'
          sh"ssh root@192.68.100.7 curl 10.152.183.167/login"
           def podNames = sh(script: "sshpass -p 'Ubuntu' ssh root@192.68.100.6 microk8s kubectl get pods -n nexus-namespace", returnStdout: true).trim().split(" ")
                    def randomIndex = Math.abs(new Random().nextInt() % podNames.size())
                    env.SELECTED_POD = podNames[randomIndex]
                    echo "Selected Pod: ${SELECTED_POD}"
          sh"sshpass -p 'Ubuntu' ssh root@192.68.100.6 microk8s kubectl get pods -n nexus-namespace"
          sh"sshpass -p 'Ubuntu' ssh root@192.68.100.6 microk8s kubectl delete pod ${env.SELECTED_POD} -n nexus-namespace"
          sh"sshpass -p 'Ubuntu' ssh root@192.68.100.6 microk8s kubectl get pods -n nexus-namespace"
        }
      }
    }
     stage("Commit version"){
      steps{
         script{
           withCredentials([usernamePassword(credentialsId:'github_token',passwordVariable:'PASS',usernameVariable:'USER')]){
           sh 'git config --global user.email "jenkins@example.com" '
            sh 'git config --global user.name "jenkins"'

             sh 'git status'
             sh 'git branch'
             sh 'git config --list '

             sh "git remote set-url origin https://$PASS@github.com/mokhtar194/timesheet.git"
             
             sh 'git add .'
             sh 'git commit -m "c1:version bump [ci skip] "'

             sh 'git push origin HEAD:main'
           }
               }
            }
         }
  
  }
}
