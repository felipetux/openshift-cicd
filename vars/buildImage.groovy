#!/usr/bin/env groovy

class BuildImageParameters {
    String clusterUrl = ""
    String clusterToken = ""
    String project = ""
    String application
    String baseImage
    String artifactsDir = "./"
}

def call(buildImageParameters) {
    call(new BuildImageParameters(buildImageParameters))
}

def call(BuildImageParameters buildImageParameters) {
    openshift.withCluster(buildImageParameters.clusterUrl, buildImageParameters.clusterToken) {
        openshift.withProject(buildImageParameters.project) {
            if (!openshift.selector("bc", buildImageParameters.application).exists()) {                
                createBuilder(project: buildImageParameters.project, application: buildImageParameters.application, baseImage. buildImageParameters.baseImage)
            }
            openshift.selector("bc", buildImageParameters.application).startBuild("--from-dir=${buildImageParameters.artifactsDir}", "--wait=true")
        }
    }
}