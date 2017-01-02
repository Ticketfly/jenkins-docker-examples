package com.ticketfly.jenkins.common

import groovy.json.JsonSlurper
import groovy.util.slurpersupport.GPathResult
import hudson.cli.CLI

import java.nio.charset.StandardCharsets

/**
 * Wrapper for the Jenkins CLI.
 */
class JenkinsCLIWrapper {
    private final CLI cli
    private URL jenkinsUrl

    JenkinsCLIWrapper(String jenkinsUrl) {
        this.jenkinsUrl = jenkinsUrl.toURL()
        this.cli = new CLI(this.jenkinsUrl)
    }

    /**
     * Console output of a CLI query.
     */
    static class Output {
        int status
        ByteArrayOutputStream out
        ByteArrayOutputStream err

        String getOut() { return out.toString('UTF-8').trim() }
        String getErr() { return err.toString('UTF-8').trim() }
    }

    private Output query(List<String> args, String input = null) {
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        ByteArrayOutputStream err = new ByteArrayOutputStream()
        int status = cli.execute(args, input ? new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)): null, out, err)
        return new Output([status:status, out:out, err:err])
    }

    Output queryGroovyScript(String script) {
        return query(['groovy', '='], script)
    }

    Object queryGroovyScriptAsJson(String script) {
        String output = queryGroovyScript(script).out
        return new JsonSlurper().parseText(output)
    }

    /**
     * Inquires about a specific job.  Requires the anonymous user to have hudson.model.Item.ExtendedRead permissions,
     * and Jenkins to have the extended-read-permission plugin installed.
     */
    GPathResult getJob(String jobName) {
        def query = query(['get-job', jobName])
        String output = query.out

        return new XmlSlurper().parseText(output)
    }

    Object getJobApiJson(String jobName) {
        String output = "$jenkinsUrl/job/$jobName/api/json".toURL().text
        return new JsonSlurper().parseText(output)
    }

    /**
     * Inquires about a specific view.  Requires the anonymous user to have hudson.model.View.Read permissions.
     */
    GPathResult getView(String viewName) {
        def query = query(['get-view', viewName])
        String output = query.out

        return new XmlSlurper().parseText(output)
    }

    int buildJob(String jobName) {
        cli.execute(["build", jobName, '-v', '-w', '-s'])
    }

    String getLastBuiltConsoleOutput(String jobName){
        "$jenkinsUrl/job/$jobName/lastBuild/consoleText".toURL().text
    }

    int createJob(String jobName, String jobXml) {
        return execute(["create-job", jobName], jobXml)
    }

    int updateJob(String jobName, String jobXml) {
        return execute(["update-job", jobName], jobXml)
    }

    int deleteJob(String jobName) {
        return cli.execute(["delete-job", jobName])
    }

    int enableJob(String jobName) {
        return cli.execute(["enable-job", jobName])
    }

    int disableJob(String jobName) {
        return cli.execute(["disable-job", jobName])
    }

    int buildJobWithParams(String jobName, Map paramsMap) {
        List params = ["build", jobName, '-v', '-w', '-s']
        paramsMap.each { k, v ->
            params.add('-p')
            params.add("$k=$v".toString())
        }
        cli.execute(params)
    }

    private int execute(List<String> args, String input) {
        return cli.execute(args,
                new ByteArrayInputStream(input.getBytes("UTF-8")),
                System.out,
                System.err)
    }
}
