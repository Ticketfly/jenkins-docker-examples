import jenkins.model.*
import java.util.logging.Logger

Logger.global.info("[Running] startup script")

configureSecurity()

configureMarkup()

Jenkins.instance.save()

buildJob('seed')

Logger.global.info("[Done] startup script")

private void configureSecurity() {
    Jenkins.getInstance().disableSecurity()
}

private void configureMarkup() {
    //configure HTML markup (used with job description and build history label)
    Jenkins.instance.setMarkupFormatter(new hudson.markup.RawHtmlMarkupFormatter(false))
}

private def buildJob(String jobName) {
    Logger.global.info("Building job '$jobName")
    def job = Jenkins.instance.getJob(jobName)
    Jenkins.instance.queue.schedule job, 0, new hudson.model.CauseAction(new hudson.model.Cause() {
        @Override
        String getShortDescription() {
            'Jenkins startup script'
        }
    })
}
