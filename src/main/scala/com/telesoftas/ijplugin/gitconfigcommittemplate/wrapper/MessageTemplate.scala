package com.telesoftas.ijplugin.gitconfigcommittemplate.wrapper

import java.io._

import com.intellij.openapi.project._
import com.telesoftas.ijplugin.gitconfigcommittemplate.ImplicitProjectApi
import com.telesoftas.ijplugin.gitconfigcommittemplate.ImplicitsFileApi
import git4idea.commands._

import scala.jdk.CollectionConverters._
import scala.util._

case class MessageTemplate private (parts: Array[String]) {
  def apply(message: String): String =
    if (parts.length == 2 && message.startsWith(parts.head) && message.endsWith(parts.last)) message
    else if (parts.length == 2) parts.mkString(if (message.isEmpty) "# message" else message)
    else parts.head
}

object MessageTemplate {

  type CommandExecutor = (Project, Git, GitCommand, Seq[String]) => Option[String]

  val gitCommandExecutor: CommandExecutor = (project, git, command, params) => {
    project.repoRoot.flatMap { repoRoot =>
      val config = new GitLineHandler(project, repoRoot, command)
      params.foreach(config.addParameters(_))
      Try(git.runCommand(config)).toOption.flatMap(_.getOutput.asScala.headOption).flatMap(new File(_).read)
    }
  }

  def apply(
    project: Project,
    git: Git = Git.getInstance(),
    executor: CommandExecutor = gitCommandExecutor,
  ): Option[MessageTemplate] =
    executor(project, git, GitCommand.CONFIG, Seq("commit.template"))
      .map(_.split("#\\{msg}", 2))
      .map(new MessageTemplate(_))

}
