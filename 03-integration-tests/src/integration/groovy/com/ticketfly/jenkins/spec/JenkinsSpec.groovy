package com.ticketfly.jenkins.spec

import com.ticketfly.jenkins.common.JenkinsCLIWrapper
import spock.lang.Specification

/**
 * Base class for all Specifications requiring the Jenkins CLI
 */
abstract class JenkinsSpec extends Specification {
    protected JenkinsCLIWrapper cli = new JenkinsCLIWrapper(System.properties['jenkinsURL'] ?: 'http://localhost:8080')
}
