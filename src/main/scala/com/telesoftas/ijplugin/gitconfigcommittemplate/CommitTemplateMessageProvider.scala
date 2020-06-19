package com.telesoftas.ijplugin.gitconfigcommittemplate
import com.intellij.openapi.project._
import com.intellij.openapi.vcs.changes._
import com.intellij.openapi.vcs.changes.ui._

class CommitTemplateMessageProvider extends CommitMessageProvider {
  override def getCommitMessage(localChangeList: LocalChangeList, project: Project): String =
    wrapper.MessageTemplate(project).map(_(localChangeList.getComment)).getOrElse(localChangeList.getComment)
}
