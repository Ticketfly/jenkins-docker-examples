# jenkins-example-job-dsl
This example, based on the [gradle-build example](https://github.com/Ticketfly/jenkins-docker-examples/tree/master/01-gradle-build), uses the [Job DSL Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Job+DSL+Plugin) to deploy the job configuration programmatically.

Instead of copying over the config.xml file for the managed job, the job is defined in a [groovy script](https://github.com/Ticketfly/jenkins-docker-examples/blob/master/02-job-dsl/jenkins-home/dsl/managedJobs.groovy.override) using the provided DSL.

# How does it work?
1. When the docker image is built, the seed job and the dsl script are copied over to Jenkins home.
2. When running the docker image, the startup script builds the seed job.
3. The seed job runs the DSL script, programmatically creating the job 'an-example-of-github-project'.

# Files changed (comparing to gradle-build example)
    .
    ├── jenkins-home
    │   ├── init.groovy.d 
    │   │   └── startup.groovy                  # run the seed job
    │   ├── jobs
    │   │   └── seed                            # seed job definition (should be the only config.xml)
    │   ├── dsl
    │   │   └── managedJobs.groovy.override     # dsl script to create the managed jobs
    │   └── plugins.txt                         # job-dsl plugin was added
    └── Dockerfile                              # copy over the dsl directory

# Quick start

- `./gradlew dockerBuild dockerRun` Build the docker image locally and start Jenkins at http://localhost:8080/

# Video Tutorials
[![Video: Introduction to Jenkins DSL ](http://img.youtube.com/vi/WdSSlQua6bw/0.jpg)](http://www.youtube.com/watch?v=WdSSlQua6bw)

# Why using the DSL?

The Job DSL Plugin allows the programmatic creation of projects using a DSL.
This method is preferred over managing the job config.xml file(s).
The DSL is more compact and readable compared to the XML format.
Since the DSL is executed as a groovy script, it allows runtime logic (docker run time) whereas the XML file is a static content (docker build time).

# Dockerhub
Hosted at [ticketfly/jenkins-example-job-dsl](https://hub.docker.com/r/ticketfly/jenkins-example-job-dsl/)

# Requirements

- Docker Version 1.10 or higher.

# Resources

- [Official Jenkins Docker image](https://github.com/jenkinsci/docker)
- [Job DSL Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Job+DSL+Plugin) ; [API viewer](https://jenkinsci.github.io/job-dsl-plugin/)
