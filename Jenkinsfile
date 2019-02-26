pipeline {
    agent {
        label env.JENKINS_AGENT
    }
    options {
        skipDefaultCheckout()
        disableConcurrentBuilds()
    }
    stages {
        stage("Initialize") {
            steps {
                library(identifier: "openshift-pipeline-library@master", 
                        retriever: modernSCM([$class: "GitSCMSource", 
                                              remote: "https://github.com/leandroberetta/openshift-cicd-demo.git"]))
                
                script {
                    env.TAG = "latest"
                    env.PROJECT = utils.getPipelineProject()
                }
            }
        }
        stage("Checkout Code") {
            steps {
                gitClone(repository: env.GIT_REPO, branch: env.GIT_BRANCH)
            }
        }
        stage("Compile Code") {
            when {
                not {
                    expression {
                        return env.COMPILE_COMMANDS.equals("none")
                    }
                }  
            }
            steps {
                sh env.COMPILE_COMMANDS
            }
        }
        stage("Test Code") {
            when {
                not {
                    expression {
                        return env.TEST_COMMANDS.equals("none")
                    }
                } 
            }
            steps {
                sh env.TEST_COMMANDS
            }
        }
        stage("Build Image") {
            steps {
                buildImage(project: "${PROJECT}-dev", application: env.APP, image: env.IMAGE, artifactsDir: env.ARTIFACTS_DIR, baseImage: env.BASE_IMAGE)
            }
        }
        stage("Deploy DEV") {
            steps {
                deployImage(project: "${PROJECT}-dev", application: env.APP, image: env.IMAGE, tag: env.TAG)
            }
        }
        stage("Promote TEST") {
            steps {
                input("Promote to TEST?")
                
                script {
                    env.TAG = utils.getTag(env.JENKINS_AGENT)
                }                    

                tagImage(srcProject: "${PROJECT}-dev", 
                         srcImage: env.IMAGE, 
                         srcTag: "latest", 
                         dstProject: "${PROJECT}-test", 
                         dstImage: env.IMAGE,
                         dstTag: env.TAG)
            }
        }
        stage("Deploy TEST") {
            steps {
                deployImage(project: "${PROJECT}-test", application: env.APP, image: env.IMAGE, tag: env.TAG)
            }
        }
        
        stage("Integration Test") {
            steps {
                echo "Integration Testing here, an standard entrypoint is useful to maintain this Jenkinsfile agnostic"
                sleep 5
            }
        }
        stage("Promote PROD") {
            steps {
                input("Promote to PROD?")

                tagImage(srcProject: "${PROJECT}-test", 
                         srcImage: env.IMAGE, 
                         srcTag: env.TAG, 
                         dstProject: "${PROJECT}-prod", 
                         dstImage: env.IMAGE,
                         dstTag: env.TAG)
            }
        }
        stage("Deploy PROD") {
            steps {
                deployImage(project: "${PROJECT}-prod", application: env.APP, image: env.IMAGE, tag: env.TAG, blueGreen: "true")
            }
        }
        stage("Publish Deploy") {
            steps {
                input("Publish new version?")

                publishDeploy(project: "${PROJECT}-prod", application: env.APP)
            }
        }
    }
}