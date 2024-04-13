@Library('Jenkins_shared_library')_

pipeline{
  agent any
     tools{
         maven 'maven'
           }
  stages{
       stage('Checkout') {
                steps {
                    scmSkip(deleteBuild: true, skipPattern:'.*\\[ci skip\\].*')
                }
            }
        stage(" increment stage"){
                steps{
                      script{
                         echo ' increment app version..'
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
        stage("build jar"){
                 steps{
                     script{
                        buildJar()
                            }
                       }
                           }
     stage("build image"){
             steps{
                 script{
                    buildImage "67.205.176.30:8083/timesheet-app:${IMAGE_NAME}"
                    dockerLogin()
                    dockerPush "67.205.176.30:8083/timesheet-app:${IMAGE_NAME}"
                         }
                     }
                             }

    stage("deploy"){
             steps{
                      script{
                          def dockerCmd = "docker run -p 8085:8085 -d --network=my-net 67.205.176.30:8083/timesheet-app:${IMAGE_NAME}"
                          sshagent(['ec2-server-key']){
                                sh "ssh -o StrictHostKeyChecking=no ec2-user@13.49.78.69 ${dockerCmd}"
                          }
                             }
                     }
                     }

    stage("commit version"){
      steps{
         script{
           withCredentials([string(credentialsId: 'github_tok', variable: 'SECRET_TOKEN')]){
           sh 'git config --global user.email "jenkins@example.com" '
            sh 'git config --global user.name "jenkins"'

             sh 'git status'
             sh 'git branch'
             sh 'git config --list '

             sh "git remote set-url origin https://${SECRET_TOKEN}@github.com/JihadMohamed1/Timsheet_ci-cd.git"
             sh 'git add .'
             sh 'git commit -m "c1:version bump [ci skip] "'

             sh 'git push origin HEAD:main'
           }
               }
            }
         }
  }

}
