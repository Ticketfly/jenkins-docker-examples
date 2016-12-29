//to run this script on jenkins, copy and past in script console http://[JENKINS_URL]/script
//after running the script, use the output to update plugins.txt
import hudson.PluginWrapper
import jenkins.model.Jenkins

def pluginList=Jenkins.getInstance().getPluginManager().getPlugins().collect {
        "${it.shortName}:${it.version}"
}.sort()
pluginList.each { println it }
