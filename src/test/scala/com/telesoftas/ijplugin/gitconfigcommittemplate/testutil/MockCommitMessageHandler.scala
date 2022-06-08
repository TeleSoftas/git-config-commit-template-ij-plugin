package com.telesoftas.ijplugin.gitconfigcommittemplate.testutil

import com.intellij.vcs.commit.CommitMessageUi
import com.intellij.vcs.commit.CommitWorkflowUi
import com.telesoftas.ijplugin.gitconfigcommittemplate.wrapper.CommitMessageField.CommitMessageHandler
import org.mockito.ArgumentMatchersSugar
import org.mockito.MockitoSugar

abstract class MockCommitMessageHandler extends CommitMessageHandler

object MockCommitMessageHandler extends MockitoSugar with ArgumentMatchersSugar {
  def context: (MockCommitMessageHandler, CommitMessageUi) = {
    val workflowUi = mock[CommitWorkflowUi]
    val messageUi  = mock[CommitMessageUi]
    when(workflowUi.getCommitMessageUi).thenReturn(messageUi)
    val handler    = mock[MockCommitMessageHandler]
    when(handler.getUi).thenReturn(workflowUi)

    (handler, messageUi)
  }
}
