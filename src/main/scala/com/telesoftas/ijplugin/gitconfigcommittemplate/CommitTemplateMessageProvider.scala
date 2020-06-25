package com.telesoftas.ijplugin.gitconfigcommittemplate
import com.intellij.openapi.project._
import com.intellij.openapi.vcs.changes._
import com.intellij.openapi.vcs.changes.ui._
import com.telesoftas.ijplugin.gitconfigcommittemplate.wrapper.MessageTemplate.CommandExecutor
import com.telesoftas.ijplugin.gitconfigcommittemplate.wrapper.MessageTemplate.gitCommandExecutor
import git4idea.commands.Git

class CommitTemplateMessageProvider extends CommitMessageProvider {

  protected val git: Option[Git]                  = None
  protected val executor: Option[CommandExecutor] = None

  override def getCommitMessage(localChangeList: LocalChangeList, project: Project): String =
    wrapper
      .MessageTemplate(
        project,
        git.getOrElse(Git.getInstance()),
        executor.getOrElse(gitCommandExecutor),
      )
      .map(_(localChangeList.getComment))
      .getOrElse(localChangeList.getComment)
}
