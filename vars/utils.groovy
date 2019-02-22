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
    return env.JOB_NAME.split("/")[0]
}