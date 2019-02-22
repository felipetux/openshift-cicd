#!/usr/bin/env groovy

def getTag(tech) {
    if (tech.equals("maven"))
        return readMavenPom().getVersion()
}