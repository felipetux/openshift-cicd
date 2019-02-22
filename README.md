# OpenShift CI/CD Demo

Basic demonstration of OpenShift CI/CD pipelines.

## Usage

    # Create the development project
    oc new-project hello-thorntail-dev

    # Create the template
    oc create -f templates/java-pipeline.yaml -n hello-thorntail-dev
    
    # Create the pipeline (the application is created by the pipeline during execution)
    oc new-app --template java-pipeline -p PARAM_GIT_REPO=https://github.com/leandroberetta/hello-thorntail.git -p PARAM_GIT_BRANCH=master -p PARAM_APP_NAME=hello-thorntail -n hello-thorntail-dev

    # Create the test and production projects
    oc new-project hello-thorntail-test
    oc new-project hello-thorntail-prod

    # Give permissions to Jenkins service account
    oc adm policy add-role-to-user edit system:serviceaccount:hello-thorntail-dev:jenkins -n hello-thorntail-test
    oc adm policy add-role-to-user edit system:serviceaccount:hello-thorntail-dev:jenkins -n hello-thorntail-prod

    # Start the pipeline 
    oc start-build hello-thorntail-pipeline -n hello-thorntail-dev

## To Do

* Blue/Green deployments
* A/B deployments

For the previous versions check the tags.





