package com.telesoftas.ijplugin.gitconfigcommittemplate

import com.intellij.openapi._
import com.intellij.openapi.vcs.checkin.CheckinHandler._
import com.telesoftas.ijplugin.gitconfigcommittemplate.testutil._
import org.mockito.ArgumentMatchersSugar
import org.mockito.MockitoSugar
import org.scalatest.flatspec._
import org.scalatest.matchers.should._
import org.scalatest.prop.Tables._

class CustomCheckinHandlerTest extends AnyFlatSpec with Matchers with MockitoSugar with ArgumentMatchersSugar {

  it should "do nothing on wrong disposable" in {
    val sut = new CustomCheckinHandler
    sut.getAfterCheckinConfigurationPanel(mock[Disposable])

    sut.beforeCheckin() shouldBe ReturnResult.COMMIT
  }

  it should "take message from ui" in {
    val (handler, messageUi) = MockCommitMessageHandler.context

    val sut = new CustomCheckinHandler
    sut.getAfterCheckinConfigurationPanel(handler)

    sut.beforeCheckin()
    verify(messageUi).getText
  }

  it should "filter out comments and set it back to ui" in {
    val (handler, messageUi) = MockCommitMessageHandler.context
    when(messageUi.getText).thenReturn(
      """|Line one
         |# ignored Line
         |Line two""".stripMargin,
    )

    val sut = new CustomCheckinHandler
    sut.getAfterCheckinConfigurationPanel(handler)

    sut.beforeCheckin()
    verify(messageUi).setText(
      """|Line one
         |Line two""".stripMargin,
    )
  }

  it should "validate commit messages" in {
    Table(
      ("commit message", "expected result"),
      ("# comment\n\n# comment\n", ReturnResult.CANCEL),
      ("# comment\n\n# comment\nFirst Line", ReturnResult.COMMIT),
      ("# comment\n\n# comment\n@First Line", ReturnResult.COMMIT),
    ).forEvery { (message, expectedResult) =>
      val (handler, messageUi) = MockCommitMessageHandler.context

      val sut = new CustomCheckinHandler
      sut.getAfterCheckinConfigurationPanel(handler)

      when(messageUi.getText).thenReturn(message)

      sut.beforeCheckin() shouldBe expectedResult
    }
  }

}
