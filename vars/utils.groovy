#!/usr/bin/env groovy

def getTag(tech) {
    if (tech.equals("maven"))
        return readMavenPom().getVersion()
    else if (tech.equals("nodejs")) 
        return getVersionFromPackageJSON()
}

def getVersionFromPackageJSON() {
    sh "cat package.json | grep version | head -1 | awk -F: '{ print \$2 }' | sed 's/[\",]//g' | tr -d '[[:space:]]' > version"

    return readFile("version").trim()
}

def getPipelineProject() {
    def rawProject = env.JOB_NAME.split("/")[0]
    
    return rawProject.substring(0, rawProject.lastIndexOf("-"))
}

def getNextBGApp(application) { 
    def activeApp = openshift.raw("get route/${application}-blue-green", "-o jsonpath='{.spec.to.name}'").out.trim()
    def nextApp = "${application}-green"
    
    if (activeApp.equals("${application}-green")) {
        nextApp = "${application}-blue"
    } 

    return nextApp
}