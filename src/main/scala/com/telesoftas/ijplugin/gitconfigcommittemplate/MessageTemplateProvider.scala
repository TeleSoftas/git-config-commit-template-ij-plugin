package com.telesoftas.ijplugin.gitconfigcommittemplate
import com.intellij.notification.{Notification, NotificationType, Notifications}
import com.intellij.openapi.project._
import com.intellij.openapi.vcs.changes._
import com.intellij.openapi.vcs.changes.ui._
import com.telesoftas.ijplugin.gitconfigcommittemplate.MessageTemplateProvider.GitLineHandlerProvider
import com.telesoftas.ijplugin.gitconfigcommittemplate.taskid.TaskIdInjector.TaskIdSetter
import git4idea.commands.GitCommand.{BRANCH, CONFIG}
import git4idea.commands.{Git, GitCommand, GitCommandResult, GitLineHandler}

import java.io.File
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
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
    message =>
      Notifications.Bus.notify(
        new Notification(
          "git config commit.template",
          "Can not retrieve commit.template",
          message,
          NotificationType.INFORMATION
        )
      )
  )

  override def getCommitMessage(localChangeList: LocalChangeList, project: Project): String = {
    val comment = Option(localChangeList.getComment).filter(_.nonEmpty)
    comment.orElse(getMessageTemplate(project)).getOrElse("")
  }

  def getMessageTemplate(project: Project): Option[String] = {
    val template = for {
      templatePath    <- runGitConfigCommitTemplateCommand(project)
      template        <- templateFile(project, templatePath).read
      branchName      <- runGitBranchShowCurrent(project)
      enrichedTemplate = template.setTaskId(branchName)
    } yield enrichedTemplate
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

  def runGitConfigCommitTemplateCommand(project: Project): Either[Throwable, String] =
    runGitCommand(project, CONFIG, "commit.template", _.getOutput.asScala.headOption.toEither("path is missing"))

  private def runGitCommand(
      project: Project,
      gitCommand: GitCommand,
      mod: String,
      response: GitCommandResult => Either[Throwable, String]
  ): Either[Throwable, String] = {
    val command = Future(
      for {
        basePath            <- Option(project.getBasePath).toEither("basePath is not found")
        configCommitTemplate = gitLineHandlerProvider(project, new File(basePath), gitCommand)
                                 .wrapMutable(_.addParameters(mod))
        gitCommandResult    <- Try(git.runCommand(configCommitTemplate)).toEither
        responseAsString    <- response(gitCommandResult)
      } yield responseAsString
    )
    Try(Await.result(command, 1.seconds)).toEither.flatten
  }

  def runGitBranchShowCurrent(project: Project): Either[Throwable, String] =
    runGitCommand(
      project,
      BRANCH,
      "--show-current",
      _.getOutput.asScala.headOption.toEither("failed to extract branch")
    )

}

object MessageTemplateProvider {
  type GitLineHandlerProvider = (Project, File, GitCommand) => GitLineHandler
}
