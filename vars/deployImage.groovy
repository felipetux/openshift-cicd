#!/usr/bin/env groovy

class DeployImageParameters {
    String clusterUrl = ""
    String clusterToken = ""
    String project = ""
    String application
    String image
    String tag = "latest"
    String ab
}

def call(deployImageParameters) {
    call(new DeployImageParameters(deployImageParameters))
}

def call(DeployImageParameters deployImageParameters) {
    openshift.withCluster(deployImageParameters.clusterUrl, deployImageParameters.clusterToken) {
        openshift.withProject(deployImageParameters.project) {
            if (deployImageParameters.ab) {
                if (!existsApplicationAB(deployImageParameters.application)) {
                    createApplicationAB(deployImageParameters.application, deployImageParameters.image, deployImageParameters.tag)
                } else {
                    rolloutApplicationAB(deployImageParameters.application, deployImageParameters.image, deployImageParameters.tag)
                }
            } else {
                if (!existsApplication(deployImageParameters.application)) {
                    createApplication(deployImageParameters.application, deployImageParameters.image, deployImageParameters.tag)
                } else {
                    rolloutApplication(deployImageParameters.application, deployImageParameters.image, deployImageParameters.tag)
                }
            }         
        }
    }
}

def createApplicationAB(application, image, tag) {
    if (!existsApplicationAB(application)) {
        openshift.newApp("${image}:${tag}", "--name=${application}-a")
        openshift.selector("svc", "${application}-a").expose()
        openshift.selector("dc", "${application}-a").rollout().status()

        openshift.newApp("${image}:${tag}", "--name=${application}-b")
        openshift.selector("svc", "${application}-b").expose()
        openshift.selector("dc", "${application}-b").rollout().status()

        if (!openshift.selector("route/${application}-ab").exists()) {
            openshift.selector("svc", "${application}-a").expose("--name=${application}-ab")
            openshift.raw("set route-backends ${application}-ab ${application}-a=100 ${application}-b=0")
        }
    }
}

def existsApplicationAB(application) { 
    if (openshift.selector("dc/${application}-a").exists() && openshift.selector("dc/${application}-b").exists()) {
        return true
    }
    
    return false
}

def existsApplication(application) { 
    if (openshift.selector("dc/${application}").exists()) {
        return true
    }
    
    return false
}

def createApplication(application, tag) { 
    if (!existsApplication(application)) {
        openshift.newApp("${image}:${tag}", "--name=${application}")
        openshift.selector("svc", application).expose()  
        openshift.selector("dc", application).rollout().status()
    }
}

def rolloutApplication(application, image, tag) {
    def dc = openshift.selector("dc/${application}").object()

    openshift.set("triggers", "dc/${application}", "--remove-all")
    openshift.set("triggers", "dc/${application}", "--from-image=${image}:${tag}", "-c ${dc.spec.template.spec.containers[0].name}")    
    openshift.selector("dc", application).rollout().status()
}

def rolloutApplicationAB(application, image, tag) {

}