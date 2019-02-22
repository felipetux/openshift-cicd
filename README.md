# OpenShift CI/CD Demo

Working on the migration to declaratives pipelines. If you're looking for the older versions check the tags (v3.9).

Basic demonstration of OpenShift CI/CD pipelines.

## Usage

Suppose you want to deploy a sample application, call 

    oc new-project hello-thorntail-dev

    oc create -f templates/java-pipeline.yaml -n hello-thorntail-dev
    oc new-app --template java-pipeline -p PARAM_GIT_REPO=https://github.com/leandroberetta/hello-thorntail.git -p PARAM_GIT_BRANCH=master -p PARAM_APP_NAME=hello-thorntail -n hello-thorntail-dev

    oc new-project hello-thorntail-test
    oc new-project hello-thorntail-prod

    oc adm policy add-role-to-user edit system:serviceaccount:hello-thorntail-dev:jenkins -n hello-thorntail-test
    oc adm policy add-role-to-user edit system:serviceaccount:hello-thorntail-dev:jenkins -n hello-thorntail-prod

    oc start-build hello-thorntail-pipeline -n hello-thorntail-dev





