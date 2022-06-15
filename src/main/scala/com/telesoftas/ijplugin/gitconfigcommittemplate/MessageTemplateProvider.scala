package com.telesoftas.ijplugin.gitconfigcommittemplate
import com.intellij.openapi.project._
import com.intellij.openapi.vcs.changes._
import com.intellij.openapi.vcs.changes.ui._
import com.telesoftas.ijplugin.gitconfigcommittemplate.MessageTemplateProvider.GitLineHandlerProvider
import git4idea.commands.{Git, GitCommand, GitCommandResult, GitLineHandler}

import java.io.File
import scala.jdk.CollectionConverters._
import scala.util.Try

class MessageTemplateProvider(git: Git, gitLineHandlerProvider: GitLineHandlerProvider) extends CommitMessageProvider {

  def this() = this(Git.getInstance(), new GitLineHandler(_, _, _))

  override def getCommitMessage(localChangeList: LocalChangeList, project: Project): String = {
    val comment = Option(localChangeList.getComment).filter(_.nonEmpty)
    comment.orElse(getMessageTemplate(project)).getOrElse("")
  }

  def getMessageTemplate(project: Project): Option[String] =
    runGitConfigCommitTemplateCommand(project).flatMap(_.getOutput.asScala.headOption).flatMap(new File(_).read)

  def runGitConfigCommitTemplateCommand(project: Project): Option[GitCommandResult] =
    project.repoRoot.flatMap { repoRoot =>
      val configCommitTemplate = gitLineHandlerProvider(project, repoRoot, GitCommand.CONFIG)
      configCommitTemplate.addParameters("commit.template")
      Try(git.runCommand(configCommitTemplate)).toOption
    }

}

object MessageTemplateProvider {
  type GitLineHandlerProvider = (Project, File, GitCommand) => GitLineHandler
}
