#!/usr/bin/env groovy

class GitInfo {
    String repository
    String branch
    String credentialsId
}

def call(gitInfo) {
    call(new GitInfo(gitInfo))
}

def call(GitInfo gitInfo) {
    def gitRemoteConfig = [:]

    gitRemoteConfig['url'] = gitInfo.repository

    if (gitInfo.credentialsId)
        gitRemoteConfig['credentialsId'] = gitInfo.credentialsId

    checkout([$class: 'GitSCM',
             branches: [[name: gitInfo.branch]], 
             userRemoteConfigs: [gitRemoteConfig]])
}