#!/bin/sh

#upload cloudformation script to public s3 bucket, the s3 URL is used in tech blog article
aws s3 cp cloudformation/jenkins-ecs-stack.json s3://remydewolf-public/cloudformation/ --acl public-read
