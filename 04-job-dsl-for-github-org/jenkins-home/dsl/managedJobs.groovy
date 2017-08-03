job("generate-org-jobs") {
    description("Explore github repos for an organization and generate Jenkins jobs for repos")
    label('master')
    parameters {
        stringParam('org', 'Netflix', 'The name of the github organization to generate jobs for.')
        stringParam('githubToken', '', 'To access private repo, provide a github token. Leave empty for public repos.')
        choiceParam('type', ['gradle'], 'The type of the jobs to generate, you can implement more in generate-jobs-for-org.groovy.')
    }
    wrappers {
        configFiles {
            custom('github-lib-build.gradle') {
                targetLocation('build.gradle')
            }
            custom('generate-jobs-for-org.groovy') {
                targetLocation('GenerateJobsForOrg.groovy')
            }
        }
    }
    steps {
        gradle {
            gradleName('gradle')
            tasks('libs')
            useWrapper(false)
        }
        dsl {
            external 'GenerateJobsForOrg.groovy'
            additionalClasspath 'libs/*.jar'
        }
    }
}
