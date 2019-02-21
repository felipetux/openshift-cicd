#!/usr/bin/env groovy

def getTag(tech) {
    if (tech.equals("java"))
        return readMavenPom().getVersion()
}