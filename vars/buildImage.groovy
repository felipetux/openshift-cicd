#!/usr/bin/env groovy

class BuildImageParameters {
    String clusterUrl = ""
    String clusterToken = ""
    String project = ""
    String application
    String image
    String artifactsDir = "./"
}

def call(buildImageParameters) {
    call(new BuildImageParameters(buildImageParameters))
}

def call(BuildImageParameters buildImageParameters) {
    openshift.withCluster(buildImageParameters.clusterUrl, buildImageParameters.clusterToken) {
        openshift.withProject(buildImageParameters.project) {
            openshift.selector("bc", buildImageParameters.application).startBuild("--from-dir=${buildImageParameters.artifactsDir}", "--wait=true")
        }
    }
}