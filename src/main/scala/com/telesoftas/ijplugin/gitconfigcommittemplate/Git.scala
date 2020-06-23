package com.telesoftas.ijplugin.gitconfigcommittemplate

import java.io.File

import com.intellij.openapi.project._
import git4idea.commands.{Git => Git4idea, _}

import scala.jdk.CollectionConverters._
import scala.util.Try

object Git {

  def commitTemplate(project: Project): Option[String] = {
    val git = Git4idea.getInstance()
    project.repoRoot().flatMap { repoRoot =>
      val config = new GitLineHandler(project, repoRoot, GitCommand.CONFIG)
      config.addParameters("commit.template")
      Try(git.runCommand(config)).toOption.flatMap(_.getOutput.asScala.headOption).flatMap(new File(_).read)
    }
  }

}
