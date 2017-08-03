//generate Jenkins jobs for a given org
//1 - search for repos containing a specified filename within a github org
//2 - for each repo found, generate a Jenkins job

import org.kohsuke.github.GHContent
import org.kohsuke.github.GitHub
import org.kohsuke.github.GHRepository

//to support a new job type, add an entry to typeToFileName and implement a method generate$typeJob, see generateGradleJob(..) for an example
def typeToFileName = ['gradle': 'gradlew']

repoRegex = "$org/(.*)"
searchedFile = typeToFileName[type]

println("Searching github for repos matching '$repoRegex' containing a file nammed '$searchedFile'. (${githubToken?'using github token':'no github token, public repos only'})")
def results = new GithubSearch(githubToken).searchFiles(org, searchedFile , GithubSearch.isAtRoot, GithubSearch.filterRepo(repoRegex))
def repos = results.collect{it.owner}.unique()

println "Found ${repos.size()} repo(s)."
if(repos){
    folder(org){
        description("${repos.size()} jobs - config generated on ${new Date()}")
    }
    repos.each{repo ->
        println "Generating $type job for $repo.fullName"
        this."generate${type.capitalize()}Job"(repo)
    }
}

class GithubSearch {

    private String gitToken

    GithubSearch(String gitToken = null) {
        this.gitToken = gitToken
    }

    private getGithub() {
        if (gitToken) {
            GitHub.connectToEnterprise('https://api.github.com', gitToken)
        } else {
            GitHub.connectAnonymously()
        }
    }

    /**
     * Search an organization for repos containing a specific file
     * @return list of github results pointing on the searched files.
     */
    Collection<GHContent> searchFiles(String orgName, String filename, Closure<Boolean>... filters) {
        def results = github.searchContent().q(filename).in('path').filename(filename).user(orgName).list().withPageSize(200).collect()

        return results.findAll { result ->
            filters.collect { filter -> filter(result) }.every()
        } as Collection<GHContent>
    }

    static Closure<Boolean> isAtRoot = { GHContent result -> !result.path.contains('/') }

    static Closure<Boolean> filterRepo(String repoRegex) {
        return { GHContent result -> result.owner.fullName.matches(repoRegex) }
    }

    static Collection<GHContent> resultsForBranch(Collection<GHContent> results, String branch) {
        def resultsForBranch = []
        results.each {
            try {
                resultsForBranch.add(it.owner.getFileContent(it.path, branch))
            } catch (_) {
                //file not found for searched branch
            }
        }
        resultsForBranch
    }
}

void generateGradleJob(GHRepository repo){
    job("$repo.fullName") {
        description("${repo.description} - config generated on ${new Date()}")
        label('master')
        scm {
            git {
                remote {
                    github("$repo.fullName", 'https')
                }
                branch('master')
            }
        }
        steps {
            shell('./gradlew build')
        }
        publishers {
            archiveArtifacts 'build/**'
        }
    }
}
