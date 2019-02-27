#! /usr/bin/env bash

oc create -f templates/java-pipeline.yaml -n openshift
oc create -f templates/node-pipeline.yaml -n openshift

oc new-project hello-thorntail-dev
oc new-project hello-thorntail-test
oc new-project hello-thorntail-prod

oc new-app --template=jenkins-ephemeral --name=jenkins -n hello-thorntail-dev

oc adm policy add-role-to-user edit system:serviceaccount:hello-thorntail-dev:jenkins -n hello-thorntail-test
oc adm policy add-role-to-user edit system:serviceaccount:hello-thorntail-dev:jenkins -n hello-thorntail-prod

oc new-app --template java-pipeline -p PARAM_GIT_REPO=https://github.com/leandroberetta/hello-thorntail.git -p PARAM_GIT_BRANCH=master -p PARAM_APP=hello-thorntail -n hello-thorntail-dev

