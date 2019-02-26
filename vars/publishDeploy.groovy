#!/usr/bin/env groovy

class PublishDeployParameters {
    String clusterUrl = ""
    String clusterToken = ""
    String project = ""
    String application
}

def call(publishDeployParameters) {
    call(new PublishDeployParameters(publishDeployParameters))
}

def call(PublishDeployParameters publishDeployParameters) {
    openshift.withCluster(publishDeployParameters.clusterUrl, publishDeployParameters.clusterToken) {
        openshift.withProject(publishDeployParameters.project) {
            def route = openshift.selector("route/${publishDeployParameters.application}-blue-green").object()
            
            route.spec.to.name = utils.getNextBGApp(publishDeployParameters.application)
            openshift.apply(route)        
        }
    }
}