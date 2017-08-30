package com.ticketfly.jenkins.spec.global

import com.ticketfly.jenkins.common.JenkinsCLIWrapper
import com.ticketfly.jenkins.spec.JenkinsSpec

/**
 * Verify that Jenkins configuration is correct: Jenkins versions, installed plugins, deployed jobs, startup script executed.
 */
class JenkinsInstanceSpec extends JenkinsSpec {

    void 'check Jenkins version'() {
        given:
        JenkinsCLIWrapper.Output output = cli.queryGroovyScript('''
        import jenkins.model.Jenkins
        println Jenkins.instance.version
        '''.stripIndent().trim())

        expect:
        output.out == '2.75'
    }

    void 'check Jenkins plugins installed'() {
        given:
        Map output = cli.queryGroovyScriptAsJson('''
        import groovy.json.JsonBuilder
        import jenkins.model.Jenkins
        def plugins = Jenkins.instance.pluginManager.plugins.collectEntries {[(it.shortName): it.version]}
        println new JsonBuilder(plugins).toPrettyString()
        '''.stripIndent().trim())

        expect:
        output.containsKey('github')
        output.containsKey('job-dsl')
    }

    void 'check seed job has run successfully'() {
        when:
        def output = cli.getJobApiJson('seed')

        then:
        assert output.firstBuild.number == 1
        assert output.lastBuild.number == 1
        assert output.lastCompletedBuild.number == 1
        assert output.lastStableBuild.number == 1
        assert output.lastSuccessfulBuild.number == 1
    }

    /**
     * Verify that the startup script configureMarkup() bloc was executed successfully
     */
    void "check configured markup is HTML"() {
        when:
        def output = cli.queryGroovyScript('println jenkins.model.Jenkins.instance.markupFormatter.class.simpleName')

        then:
        output.status == 0
        output.out == 'RawHtmlMarkupFormatter'
    }
}
