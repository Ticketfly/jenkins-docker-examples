# job-dsl-for-github-org
This example uses the [Job DSL Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Job+DSL+Plugin) and the [Java API for GitHub](https://github.com/kohsuke/github-api) to generate Jenkins jobs based on github repos.

Check out the [blog article](https://tech.ticketfly.com/our-journey-to-continuous-delivery-chapter-3-automate-your-configuration-with-jenkins-dsl-1ff14d7de4c4) for this example.

# How does it work?
1. When the docker image is built, the seed job and the dsl script are copied over to Jenkins home.
2. When running the docker image, the startup script builds the seed job.
3. The seed job runs the DSL job `managedJobs.groovy` which generates the job `generate-org-jobs`
4. When running the job `generate-org-jobs` for a github organization, it searches the github organization for gradle files and generate Jenkins jobs for each repo found.

# Project structure
    .
    ├── jenkins-home
    │   ├── init.groovy.d 
    │   │   └── startup.groovy                  # install gradle, deploy config files, run the seed job
    │   ├── jobs
    │   │   └── seed                            # seed job definition (should be the only config.xml)
    │   ├── config-file-provider
    │   │   └── generate-jobs-for-org.groovy    # script to generate Jenkins jobs for a given org
    │   │   └── github-lib-build.gradle         # build file to download github-api jars so it can be used by DSL
    │   ├── dsl
    │   │   └── managedJobs.groovy              # dsl script to create `generate-org-jobs`
    │   └── plugins.txt                         # jenkins plugins
    └── Dockerfile                              # build the Docker image

# Quick start

- `./gradlew dockerBuild dockerRun` Build the docker image locally and start Jenkins at http://localhost:8080/

# Video Tutorials
[![Video: Automate Jenkins configuration from Github repos](http://img.youtube.com/vi/lHgvrFZBqvM/0.jpg)](http://www.youtube.com/watch?v=lHgvrFZBqvM)

# Why generating Jenkins jobs from github repos.

Using the ability to programmatically generate Jenkins jobs from your github source repos unblocks the ability to fully automate the configuration of your Jenkins.
Instead of having a gate keeper that is manually managing Jenkins when needed, the jobs are automatically updated based on github repos, giving developers the ability to manage their builds.

Some of the use cases that can be addressed are:
- When creating a new github repo, a Jenkins job needs to be configured -> run the `generate-org-jobs` on a periodic schedule to automatically create the new jobs for new repos.
- Every job configuration becomes slightly different, creating inconsistency across the build pipeline -> all jobs configurations are identical for a project type, defined in `generate-jobs-for-org.groovy`.
- Not every github repo in your organization follows the same conventions when it comes to continuous integration -> defining some common standards on how to build your apps is a pre-requisite for build automation.
- When configuring a plugin, all the existing jobs have to be updated -> perform batch update by re-runing `generate-org-jobs`.
- Some applications need some tools installed to build -> install the tools through the startup script.
- Jenkins crashed and need to be recovered -> all configuration being automated, Jenkins can be deployed on a new instance and the configuration will be regenerated.

# Dockerhub
Hosted at [ticketfly/jenkins-example-job-dsl-for-github-org](https://hub.docker.com/r/ticketfly/jenkins-example-job-dsl-for-github-org/)

# Requirements

- Docker Version 1.10 or higher.

# Resources

- [Official Jenkins Docker image](https://github.com/jenkinsci/docker)
- [Job DSL Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Job+DSL+Plugin)
- [Java API for GitHub](https://github.com/kohsuke/github-api)
- [Ticketfly Tech: Automate your configuration with Jenkins DSL](https://tech.ticketfly.com/our-journey-to-continuous-delivery-chapter-3-automate-your-configuration-with-jenkins-dsl-1ff14d7de4c4)
