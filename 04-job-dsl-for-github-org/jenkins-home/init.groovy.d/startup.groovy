import jenkins.model.Jenkins
import hudson.model.*
import java.util.logging.Logger
import hudson.plugins.gradle.*
import hudson.tools.*

Logger.global.info("[Running] startup script")

configureSecurity()
deployConfigFiles()
installGradle('4.0.2')

Jenkins.instance.save()

buildJob('seed')

Logger.global.info("[Done] startup script")

private void configureSecurity() {
    Jenkins.getInstance().disableSecurity()
}

private def buildJob(String jobName) {
    Logger.global.info("Building job '$jobName")
    def job = Jenkins.instance.getJob(jobName)
    Jenkins.instance.queue.schedule(job, 0, new CauseAction(new Cause() {
        @Override
        String getShortDescription() {
            'Jenkins startup script'
        }
    }))
}

private def deployConfigFiles() {
    //deploy scripts
    String jenkinsHome = System.env.get("JENKINS_HOME")
    new File("$jenkinsHome/config-file-provider").listFiles().each { file ->
        deployConfigFile(file.name, file.text, file.name)
    }
}

private void deployConfigFile(String name, String content, String comment) {
    Logger.global.info "Deploy config file '${name}'"
    String configProviderId = 'org.jenkinsci.plugins.configfiles.custom.CustomConfig'
    def provider = org.jenkinsci.lib.configprovider.ConfigProvider.all().find { it.getProviderId() == configProviderId }
    def config = provider.newConfig(name, name, comment, content)
    provider.save(config)
}

private void installGradle(String gradleVersion) {
    def gradleInstallationDescriptor =
            Jenkins.getActiveInstance().getDescriptorByType(GradleInstallation.DescriptorImpl.class)
    GradleInstallation[] installations = [new GradleInstallation("gradle", "", [new InstallSourceProperty([new GradleInstaller(gradleVersion)])])]
    gradleInstallationDescriptor.setInstallations(installations)
}
