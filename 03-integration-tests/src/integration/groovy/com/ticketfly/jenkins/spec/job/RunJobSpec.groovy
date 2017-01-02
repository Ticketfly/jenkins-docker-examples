package com.ticketfly.jenkins.spec.job

import com.ticketfly.jenkins.spec.JenkinsSpec
import javaposse.jobdsl.dsl.JobParent
import javaposse.jobdsl.dsl.MemoryJobManagement

/**
 * Run Jenkins job and verify that console output is correct.
 * Use existing jobs or create jobs on the fly using the DSL.
 */
class RunJobSpec extends JenkinsSpec {

    void "run the deployed job a-simple-shell-script"() {
        //this job is defined in /jenkins-home/dsl/managedJobs.groovy and created when running the seed job
        given:
        String jobName = 'a-simple-shell-script'

        when:
        int status = cli.buildJob(jobName)
        String consoleOutput = cli.getLastBuiltConsoleOutput(jobName)

        then:
        status == 0
        consoleOutput.contains('echo abc')
        consoleOutput.contains('Archiving artifacts')
        consoleOutput.contains('Finished: SUCCESS')
    }

    void "create a job on the fly and run it"() {
        given:
        String jobName = 'a-simple-shell-script-generated-by-test'
        String randomText = UUID.randomUUID().toString()

        when:
        //create a job xml using the DSL
        String jobXml = memoryJobDsl.job(jobName) {
            steps {
                shell "echo $randomText"
            }
        }.xml

        int creationStatus = cli.createJob(jobName, jobXml)
        int buildStatus = cli.buildJob(jobName)
        String consoleOutput = cli.getLastBuiltConsoleOutput(jobName)

        then:
        creationStatus == 0
        buildStatus == 0
        consoleOutput.contains(randomText)

        cleanup:
        cli.deleteJob(jobName)
    }

    private static JobParent memoryJobDsl = {
        def memoryJobManagement = new MemoryJobManagement()
        JobParent jp = new JobParent() {
            @Override
            Object run() {
                return null
            }
        }
        jp.setJm(memoryJobManagement)
        jp
    }

}
