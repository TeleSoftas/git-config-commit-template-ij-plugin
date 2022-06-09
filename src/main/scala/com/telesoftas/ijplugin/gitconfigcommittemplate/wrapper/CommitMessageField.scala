package com.telesoftas.ijplugin.gitconfigcommittemplate.wrapper

import com.intellij.openapi._
import com.intellij.vcs.commit._
import com.telesoftas.ijplugin.gitconfigcommittemplate.wrapper.CommitMessageField._

class CommitMessageField private (changeListHandler: CommitMessageHandler) {
  def getText: String                = changeListHandler.getCommitMessage
  def setText(message: String): Unit = changeListHandler.setCommitMessage(message)
}

object CommitMessageField {

  type CommitMessageHandler = AbstractCommitWorkflowHandler[AbstractCommitWorkflow, CommitWorkflowUi]

  def apply(disposable: Disposable): Option[CommitMessageField] = disposable match {
    case handler: CommitMessageHandler => Some(new CommitMessageField(handler))
    case _                             => None
  }
}
