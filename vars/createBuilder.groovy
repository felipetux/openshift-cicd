#!/usr/bin/env groovy

class CreateBuilderParameters {
    String clusterUrl = ""
    String clusterToken = ""
    String project = ""
    String application
    String image
    String baseImage
}

def call(createBuilderParameters) {
    call(new CreateBuilderParameters(createBuilderParameters))
}

def call(CreateBuilderParameters createBuilderParameters) {
    openshift.withCluster(createBuilderParameters.clusterUrl, createBuilderParameters.clusterToken) {
        openshift.withProject(createBuilderParameters.project) {
            openshift.newBuild("--image-stream=${createBuilderParameters.baseImage}", "--name=${createBuilderParameters.application}", "--binary=true", "--to=${createBuilderParameters.image}");                   
        }
    }
}