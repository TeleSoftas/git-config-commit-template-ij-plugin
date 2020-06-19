package com.telesoftas.ijplugin.gitconfigcommittemplate.wrapper

import com.telesoftas.ijplugin.gitconfigcommittemplate._
import com.intellij.openapi.Disposable
import com.intellij.vcs.commit.SingleChangeListCommitWorkflowHandler

class CommitMessage private (changeListHandler: SingleChangeListCommitWorkflowHandler) {
  def get: String                = changeListHandler.getCommitMessage
  def set(message: String): Unit = changeListHandler.setCommitMessage(message)
}

object CommitMessage {
  def apply(disposable: Disposable): Option[CommitMessage] = disposable match {
    case handler: SingleChangeListCommitWorkflowHandler => Some(new CommitMessage(handler))
    case _                                              => None
  }
}
