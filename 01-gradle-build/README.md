# jenkins-example-gradle-build
Jenkins based on a docker image configured as a gradle project.

This example provides a fully working Jenkins server based on the offial docker image.
Jenkins comes up with a job defined in the docker image (in a config.xml) and a simple groovy startup script.
A set of gradle tasks maps to useful docker commands such as build, run and push.

# Project structure
    .
    ├── jenkins-home            # Jenkins files to be deployed in docker image
    │   ├── init.groovy.d       # groovy scripts executed when Jenkins starts
    │   ├── jobs                # jobs deployed on Jenkins
    │   ├── userContent         # these files are served from http://yourhost/jenkins/userContent
    │   ├── ListPlugins.groovy  # utility script to generate the content for plugins.txt
    │   └── plugins.txt         # list of Jenkins plugins to install
    ├── Dockerfile              # command lines to assemble the docker image
    └── build.gradle            # gradle build file

# Quick start

- `./gradlew dockerBuild dockerRun` Build the docker image locally and start Jenkins at http://localhost:8080/

# Video Tutorials
[![Video: Run Jenkins on Docker](http://img.youtube.com/vi/LUgF9kOW4u4/0.jpg)](http://www.youtube.com/watch?v=LUgF9kOW4u4)

[![Video: Upgrade Jenkins version using gradle docker commands](http://img.youtube.com/vi/2JTxROGphdw/0.jpg)](http://www.youtube.com/watch?v=2JTxROGphdw)

# Deployment

## Deploy using the dockerhub image
The image is hosted at https://hub.docker.com/r/ticketfly/jenkins-example-gradle-build
```shell
docker run --name jenkins -p 8080:8080 -v /var/jenkins_home ticketfly/jenkins-example-gradle-build
```

## Configuring a volume for Jenkins home
```shell
#use a directory for which you have permission
JENKINS_HOME='/data/jenkins'
mkdir -p $JENKINS_HOME
#chown required for linux, ignore this line for mac or windows
chown 1000:1000 $JENKINS_HOME
docker run --name jenkins -p 8080:8080 -v $JENKINS_HOME:/var/jenkins_home ticketfly/jenkins-example-gradle-build
```

# Docker gradle commands

This project provides a set of gradle tasks mapping to docker commands.
- `./gradlew dockerBuild` Build the docker image, tag as current version.
- `./gradlew dockerRun` Run the Jenkins docker container locally.
- `./gradlew dockerRun -PdockerDetached=true` detached mode, to run on CI.
- `./gradlew dockerStop` Stop the Jenkins docker container, to run on CI.
- `./gradlew dockerStatus` Display the process status of Jenkins docker container.
- `./gradlew dockerPush` Push the docker image with the current tag.

# Why Jenkins docker?

Using the docker image allows to pre-package Jenkins with some desired configuration.
It includes the versions to use for Jenkins and the list of plugins, some startup scripts, and the user content folder.
Once Jenkins is configured in the docker image, the deployment to your server instance is simplified, just install docker and run your image in a container.
This can be achieved by either building your image on your server directly or by pushing the image to a docker repo and pulling it from your server.

Another great advantage of using docker is the ability to bring up a Jenkins server locally very quickly.
This enables staging the changes before deploying on your Jenkins server.

# Why Gradle?

Gradle simplifies the use of docker by providing a list of tasks mapping to Docker commands with arguments.
Running this project on CI is simplified as gradle wrapper will self install itself.
It will allow adding integration tests for Jenkins, see other examples provided by Ticketfly.

# Dockerhub
Hosted at [ticketfly/jenkins-example-gradle-build](https://hub.docker.com/r/ticketfly/jenkins-example-gradle-build/)

# Requirements

- Docker Version 1.10 or higher.

# Resources

- [Official Jenkins Docker image](https://github.com/jenkinsci/docker)
