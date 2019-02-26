#!/usr/bin/env groovy

class PublishVersionParameters {
    String clusterUrl = ""
    String clusterToken = ""
    String project = ""
    String application
}

def call(publishVersionParameters) {
    call(new PublishVersionParameters(publishVersionParameters))
}

def call(PublishVersionParameters publishVersionParameters) {
    openshift.withCluster(publishVersionParameters.clusterUrl, publishVersionParameters.clusterToken) {
        openshift.withProject(publishVersionParameters.project) {
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