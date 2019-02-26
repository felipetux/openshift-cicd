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
            def activeApp = openshift.raw("get route/${publishDeployParameters.application}-blue-green", "-o jsonpath='{.spec.to.name}'").out.trim()
            def nextApp = "${publishDeployParameters.application}-green"
            def route = openshift.selector("route/${publishDeployParameters.application}-blue-green").object()
            
            echo "antes"
            echo "${publishDeployParameters.application}-green"
            if (activeApp.equals("${publishDeployParameters.application}-green")) {
                echo "cambio"
                nextApp = "${publishDeployParameters.application}-blue"
                echo nextApp
            } 
            echo nextApp
            route.spec.to.name = nextApp
            
            openshift.apply(route)        
        }
    }
}