package com.telesoftas.ijplugin.gitconfigcommittemplate.wrapper

import com.intellij.openapi._
import com.intellij.vcs.commit._
import com.telesoftas.ijplugin.gitconfigcommittemplate.testutil._
import org.mockito.ArgumentMatchersSugar
import org.mockito.MockitoSugar
import org.scalatest.flatspec._
import org.scalatest.matchers.should._

class CommitMessageTest extends AnyFlatSpec with Matchers with MockitoSugar with ArgumentMatchersSugar {

  it should "wrap correct WorkflowHandler" in {
    val correct = mock[CommitMessageField.CommitMessageHandler]
    val wrong   = mock[Disposable]

    CommitMessageField(correct) shouldBe defined
    CommitMessageField(wrong) shouldBe empty
  }

  it should "pass get/set commit message to handler" in {
    val (handler, messageUi) = MockCommitMessageHandler.context

    when(messageUi.getText).thenReturn("Message")
    CommitMessageField(handler).map(_.getText) shouldBe Some("Message")

    CommitMessageField(handler).foreach(_.setText("Message"))
    verify(messageUi).setText("Message")
  }

}
