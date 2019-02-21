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
                    env.PROJECT = getPipelineProject()

                    if (!openshift.selector("bc", "${APP_NAME}").exists()) {                
                        createBuilder(project: env.PROJECT, application: env.APP_NAME, baseImage. env.BASE_IMAGE)
                    }
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
                buildImage(project: env.PROJECT, application: env.APP_NAME, image: env.IMAGE_NAME, artifactsDir: env.ARTIFACTS_DIR)
            }
        }
        stage("Deploy Image") {
            steps {
                deployImage(project: env.PROJECT, application: env.APP_NAME, image: env.IMAGE_NAME, tag: env.TAG)
            }
        }
    }
}