package com.telesoftas.ijplugin.gitconfigcommittemplate
import com.intellij.notification.{Notification, NotificationType, Notifications}
import com.intellij.openapi.project._
import com.intellij.openapi.vcs.changes._
import com.intellij.openapi.vcs.changes.ui._
import com.telesoftas.ijplugin.gitconfigcommittemplate.MessageTemplateProvider.GitLineHandlerProvider
import git4idea.commands.{Git, GitCommand, GitCommandResult, GitLineHandler}

import java.io.File
import scala.jdk.CollectionConverters._
import scala.util.Try

class MessageTemplateProvider(
  git: => Git,
  gitLineHandlerProvider: => GitLineHandlerProvider,
  notificationProducer: String => Unit
) extends CommitMessageProvider {

  def this() = this(
    Git.getInstance(),
    new GitLineHandler(_, _, _),
    message => Notifications.Bus.notify(
      new Notification(
        "git config commit.template",
        "Can not retrieve commit.template",
        message,
        NotificationType.INFORMATION,
      ),
    ),
  )

  override def getCommitMessage(localChangeList: LocalChangeList, project: Project): String = {
    val comment = Option(localChangeList.getComment).filter(_.nonEmpty)
    comment.orElse(getMessageTemplate(project)).getOrElse("")
  }

  def getMessageTemplate(project: Project): Option[String] = {
    val template = for {
      gitCommandResult <- runGitConfigCommitTemplateCommand(project)
      templatePath     <- gitCommandResult.getOutput.asScala.headOption.toEither("path is missing")
      template         <- templateFile(project, templatePath).read
    } yield template
    template match {
      case Right(text) => Some(text)
      case Left(error) =>
        error.printStackTrace()
        notificationProducer(error.getMessage)
        None
    }
  }

  def templateFile(project: Project, templatePath: String): File =
    if (new File(templatePath).isAbsolute) new File(templatePath)
    else new File(project.getBasePath, templatePath)

  def runGitConfigCommitTemplateCommand(project: Project): Either[Throwable, GitCommandResult] = {
    for {
      basePath            <- Option(project.getBasePath).toEither("basePath is not found")
      configCommitTemplate = gitLineHandlerProvider(project, new File(basePath), GitCommand.CONFIG)
                               .wrapMutable(_.addParameters("commit.template"))
      gitCommandResult    <- Try(git.runCommand(configCommitTemplate)).toEither
    } yield gitCommandResult
  }

}

object MessageTemplateProvider {
  type GitLineHandlerProvider = (Project, File, GitCommand) => GitLineHandler
}
