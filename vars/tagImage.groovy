#!/usr/bin/env groovy

class TagImageParameters {
    String clusterUrl = ""
    String clusterToken = ""
    String srcProject = ""
    String dstProject = ""
    String srcImage
    String srcTag = "latest"
    String dstImage
    String dstTag = "latest"
}

def call(tagImageParameters) {
    call(new TagImageParameters(tagImageParameters))
}

def call(TagImageParameters tagImageParameters) {
    openshift.withCluster(tagImageParameters.clusterUrl, tagImageParameters.clusterToken) {
        openshift.withProject(tagImageParameters.dstProject) {
            openshift.tag("${tagImageParameters.srcProject}/${tagImageParameters.srcImage}:${tagImageParameters.srcTag}", "${tagImageParameters.dstProject}/${tagImageParameters.dstImage}:${tagImageParameters.dstTag}");
        }
    }
}