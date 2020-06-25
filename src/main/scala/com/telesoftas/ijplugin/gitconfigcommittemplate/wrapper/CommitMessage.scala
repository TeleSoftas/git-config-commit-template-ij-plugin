package com.telesoftas.ijplugin.gitconfigcommittemplate.wrapper

import com.intellij.openapi._
import com.intellij.vcs.commit._
import com.telesoftas.ijplugin.gitconfigcommittemplate.wrapper.CommitMessage._

class CommitMessage private (changeListHandler: CommitMessageHandler) {
  def get: String                = changeListHandler.getCommitMessage
  def set(message: String): Unit = changeListHandler.setCommitMessage(message)
}

object CommitMessage {

  type CommitMessageHandler = AbstractCommitWorkflowHandler[AbstractCommitWorkflow, CommitWorkflowUi]

  def apply(disposable: Disposable): Option[CommitMessage] = disposable match {
    case handler: CommitMessageHandler => Some(new CommitMessage(handler))
    case _                             => None
  }
}
