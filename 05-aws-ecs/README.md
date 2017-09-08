# jenkins-example-aws-ecs
This example deploys Jenkins infrastructure to [Amazon EC2 Container Service](https://aws.amazon.com/ecs/) cluster.

Check out the [blog article](https://tech.ticketfly.com/our-journey-to-continuous-delivery-chapter-4-run-jenkins-infrastructure-on-aws-container-service-ef37e0304b95)) for this example.

# How does it work?
1. Run the deploy script, it creates a CloudFormation stack that contains all the infrastructure components. It includes network configuration, security, storage, auto-scaling group and Jenkins deployment.
2. Once the CloudFormation stack is executed, retrieve the Jenkins URL in the 'Outputs' tab under the property 'JenkinsELB'.
3. To trigger auto-scaling, Jenkins comes pre-configured with 10 projects, run the project called '_Run_All_Jobs_' to simulate a spike of activity.
4. As the builds are queued in, the ECS cluster becomes full and auto-scaling kicks off. More EC2 instances are added to the cluster to process the waiting builds.
5. Once the builds are done, the cluster becomes idle. After 10 minutes of low activity, auto-scaling kicks off again but this time to reduce the size of ECS cluster.

# How much does it cost?
This example is designed to run on micro instances (t2.micro) and the maximum size of the auto-scaling group has been set to 5 instances.
Including storage and data transfer, it costs approximately $0.10/hour to run this example.

To prevent any further billing, the CloudFormation stack should be deleted when done.
If you change the instance type and the maximum number of instances, check [EC2 Pricing](https://aws.amazon.com/ec2/pricing/on-demand/).

# Project structure
    .
    ├── jenkins-home
    │   ├── init.groovy.d 
    │   │   └── startup.groovy.override         # configure Jenkins ECS plugin
    │   ├── jobs
    │   │   └── seed                            # seed job definition
    │   ├── dsl
    │   │   └── managedJobs.groovy.override     # dsl script to generate the jenkins projects
    │   └── plugins.txt                         # jenkins plugins (including amazon-ecs)
    └── Dockerfile                              # build the Docker image
    └── build.gradle                            # gradle wrapper tasks to execute docker commands
    └── jenkins-ecs-stack.json                  # CloudFormation template to create the stack on AWS
    └── deploy-stack-to-aws.sh                  # run CloudFormation from command line

# Quick start

## Using the AWS Console
[![Launch Stack](https://s3.amazonaws.com/cloudformation-examples/cloudformation-launch-stack.png)](https://console.aws.amazon.com/cloudformation/home?region=us-east-1#/stacks/new?stackName=jenkins-ecs-stack&templateURL=https:%2F%2Fs3.amazonaws.com%2Fremydewolf-public%2Fcloudformation%2Fjenkins-ecs-stack.json)

## (or) Running the deploy script (require CLI)
- Run `./deploy-stack-to-aws.sh`

# Use cases addressed by using AWS to host Jenkins CI.
- Auto-recovery of Jenkins server in case of instance crash, by running it as an ECS Service.
- Scale up and down your build infrastructure based on usage using CloudWatch to control the size of the Auto Scaling group.
- Prevent disk space issues related to growing number of builds in Jenkins by using [EFS]((https://github.com/jenkinsci/docker)).
- Protect Jenkins infrastructure by hosting the resources in a [Virtual Private Cloud](https://aws.amazon.com/vpc/).

# Dockerhub Image
Hosted at [ticketfly/jenkins-example-aws-ecs](https://hub.docker.com/r/ticketfly/jenkins-example-aws-ecs/)

# Requirements
- AWS account

# Resources

- [Official Jenkins Docker image](https://github.com/jenkinsci/docker)
- [Amazon EC2 Container Service Plugin](https://wiki.jenkins.io/display/JENKINS/Amazon+EC2+Container+Service+Plugin)
- [Job DSL Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Job+DSL+Plugin)
- [Amazon EC2 Container Service](https://aws.amazon.com/ecs/)
- [Amazon Elastic File System](https://aws.amazon.com/efs/)
- [Ticketfly Tech: Run Jenkins Infrastructure on AWS Container Service](https://tech.ticketfly.com/our-journey-to-continuous-delivery-chapter-4-run-jenkins-infrastructure-on-aws-container-service-ef37e0304b95)
