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

def getNextBGApp(application, blueGreenApplication) { 
    def nextApp = "${application}-green"
    
    echo application
    echo nextApp
    echo blueGreenApplication
    echo "${application}-green"
    println blueGreenApplication.compareTo("${application}-green")
    println "${application}-green".compareTo("${application}-green")
    
    if (blueGreenApplication.compareTo("${application}-green") == 0) {
        nextApp = "${application}-blue"
        echo nextApp
    } 

    return nextApp
}