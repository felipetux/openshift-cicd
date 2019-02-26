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
            def activeApp = openshift.raw("get route/${application}-blue-green", "-o jsonpath='{.spec.to.name}'").out.trim()
            def nextApp = "${application}-green"
            def route = openshift.selector("route/${application}-blue-green").object()
            
            if (activeApp.equals("${application}-green")) {
                nextApp = "${application}-blue"
            } 
            
            route.spec.to.name = nextApp
            
            openshift.apply(route)        
        }
    }
}