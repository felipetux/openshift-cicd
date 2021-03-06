#!/usr/bin/env groovy

class DeployImageParameters {
    String clusterUrl = ""
    String clusterToken = ""
    String project = ""
    String application
    String image
    String tag = "latest"
    String blueGreen
}

def call(deployImageParameters) {
    call(new DeployImageParameters(deployImageParameters))
}

def call(DeployImageParameters deployImageParameters) {
    openshift.withCluster(deployImageParameters.clusterUrl, deployImageParameters.clusterToken) {
        openshift.withProject(deployImageParameters.project) {
            if (deployImageParameters.blueGreen) {
                if (!existsApplicationBlueGreen(deployImageParameters.application)) {
                    createBlueGreenApplication(deployImageParameters.application, deployImageParameters.image, deployImageParameters.tag)
                } else {
                    rolloutBlueGreenApplication(deployImageParameters.application, deployImageParameters.image, deployImageParameters.tag)
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

def createBlueGreenApplication(application, image, tag) {
    createApplication("${application}-green", image, tag)
    createApplication("${application}-blue", image, tag)
    
    if (!openshift.selector("route/${application}-blue-green").exists()) {
        openshift.selector("svc", "${application}-green").expose("--name=${application}-blue-green")
    }
}

def existsApplicationBlueGreen(application) { 
    if (openshift.selector("dc/${application}-green").exists() && openshift.selector("dc/${application}-blue").exists()) {
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

def createApplication(application, image, tag) { 
    if (!existsApplication(application)) {
        openshift.newApp("${image}:${tag}", "--name=${application}")
        openshift.selector("svc", application).expose()  
        openshift.selector("dc", application).rollout().status()
        openshift.set("triggers", "dc/${application}", "--remove-all")
    }
}

def rolloutApplication(application, image, tag) {
    def dc = openshift.selector("dc/${application}").object()

    openshift.set("triggers", "dc/${application}", "--remove-all")
    openshift.set("triggers", "dc/${application}", "--from-image=${image}:${tag}", "-c ${dc.spec.template.spec.containers[0].name}")    
    openshift.selector("dc", application).rollout().status()
}

def rolloutBlueGreenApplication(application, image, tag) {
    def route = openshift.selector("route/${application}-blue-green").object()

    rolloutApplication(utils.getNextBlueGreenApplication(application, route.spec.to.name), image, tag)    
}