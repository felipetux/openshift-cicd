#!/usr/bin/env groovy

class CreateBuilderParameteres {
    String clusterUrl = ""
    String clusterToken = ""
    String project = ""
    String application
    String baseImage
}

def call(createBuilderParameteres) {
    call(new CreateBuilderParameteres(createBuilderParameteres))
}

def call(CreateBuilderParameteres createBuilderParameteres) {
    openshift.withCluster(createBuilderParameteres.clusterUrl, createBuilderParameteres.clusterToken) {
        openshift.withProject(createBuilderParameteres.project) {
            openshift.newBuild("--image-stream=${createBuilderParameteres.baseImage}", "--name=${createBuilderParameteres.application}", "--binary=true");                   
        }
    }
}