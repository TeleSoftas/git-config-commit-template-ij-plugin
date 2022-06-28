package com.telesoftas.ijplugin.gitconfigcommittemplate

import com.intellij.openapi._
import com.intellij.openapi.vcs.checkin.CheckinHandler._
import com.intellij.vcs.commit.{CommitMessageUi, CommitWorkflowUi}
import com.telesoftas.ijplugin.gitconfigcommittemplate.wrapper.CommitMessageField
import com.telesoftas.ijplugin.gitconfigcommittemplate.wrapper.CommitMessageField.CommitMessageHandler
import org.mockito.ArgumentMatchersSugar
import org.mockito.MockitoSugar
import org.scalatest.flatspec._
import org.scalatest.matchers.should._
import org.scalatest.prop.Tables._

class CustomCheckinHandlerTest extends AnyFlatSpec with Matchers with MockitoSugar with ArgumentMatchersSugar {

  "CommitMessage" should "wrap correct WorkflowHandler" in {
    val correct = mock[CommitMessageField.CommitMessageHandler]
    val wrong   = mock[Disposable]

    CommitMessageField(correct) shouldBe defined
    CommitMessageField(wrong) shouldBe empty
  }

  it should "pass get/set commit message to handler" in {
    val context = TestContext()

    when(context.messageUi.getText).thenReturn("Message")
    CommitMessageField(context.commitMessageHandler).map(_.getText) shouldBe Some("Message")

    CommitMessageField(context.commitMessageHandler).foreach(_.setText("Message"))
    verify(context.messageUi).setText("Message")
  }

  it should "do nothing on wrong disposable" in {
    val sut = new CustomCheckinHandler
    sut.getAfterCheckinConfigurationPanel(mock[Disposable])

    sut.beforeCheckin() shouldBe ReturnResult.COMMIT
  }

  it should "take message from ui" in {
    val context = TestContext()

    val sut = new CustomCheckinHandler
    sut.getAfterCheckinConfigurationPanel(context.commitMessageHandler)

    sut.beforeCheckin()
    verify(context.messageUi).getText
  }

  it should "filter out comments and set it back to ui" in {
    val context = TestContext()
    when(context.messageUi.getText).thenReturn(
      """|Line one
         |# ignored Line
         |Line two""".stripMargin
    )

    val sut = new CustomCheckinHandler
    sut.getAfterCheckinConfigurationPanel(context.commitMessageHandler)

    sut.beforeCheckin()
    verify(context.messageUi).setText(
      """|Line one
         |Line two""".stripMargin
    )
  }

  it should "validate commit messages" in {
    Table(
      ("commit message", "expected result"),
      ("# comment\n\n# comment\n", ReturnResult.CANCEL),
      ("# comment\n\n# comment\nFirst Line", ReturnResult.COMMIT),
      ("# comment\n\n# comment\n@First Line", ReturnResult.COMMIT)
    ).forEvery { (message, expectedResult) =>
      val context = TestContext()

      val sut = new CustomCheckinHandler
      sut.getAfterCheckinConfigurationPanel(context.commitMessageHandler)

      when(context.messageUi.getText).thenReturn(message)

      sut.beforeCheckin() shouldBe expectedResult
    }
  }

  case class TestContext(
      commitMessageHandler: CommitMessageHandler = mock[CommitMessageHandler],
      workflowUi: CommitWorkflowUi = mock[CommitWorkflowUi],
      messageUi: CommitMessageUi = mock[CommitMessageUi]
  ) {
    when(workflowUi.getCommitMessageUi).thenReturn(messageUi)
    when(commitMessageHandler.getUi).thenReturn(workflowUi)
  }

}
