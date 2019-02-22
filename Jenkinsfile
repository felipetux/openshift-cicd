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
                    env.PROJECT = env.APP_NAME
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
                buildImage(project: "${PROJECT}-dev", application: env.APP_NAME, image: env.IMAGE_NAME, artifactsDir: env.ARTIFACTS_DIR, baseImage: env.BASE_IMAGE)
            }
        }
        stage("Deploy (DEV)") {
            steps {
                deployImage(project: "${PROJECT}-dev", application: env.APP_NAME, image: env.IMAGE_NAME, tag: env.TAG)
            }
        }
        stage("Promote (TEST)") {
            steps {
                input("Promote to TEST?")

                tagImage(srcProject: "${PROJECT}-dev", 
                         srcImage: env.IMAGE_NAME, 
                         srcTag: env.TAG, 
                         dstProject: "${PROJECT}-test", 
                         dstImage: env.IMAGE_NAME,
                         dstTag: utils.getTag(env.JENKINS_AGENT))
            }
        }
        stage("Deploy (TEST)") {
            steps {
                deployImage(project: "${PROJECT}-test", application: env.APP_NAME, image: env.IMAGE_NAME, tag: env.TAG)
            }
        }
        /*
        stage("Integration Test") {
            steps {
                sleep 1
            }
        }
        stage("Promote (PROD)") {
            steps {
                input("Promote to PROD?")
            }
        }
        stage("Deploy (PROD)") {
            steps {
                deployImage(project: "${PROJECT}-prod", application: env.APP_NAME, image: env.IMAGE_NAME, tag: env.TAG)
            }
        }
        */
    }
}