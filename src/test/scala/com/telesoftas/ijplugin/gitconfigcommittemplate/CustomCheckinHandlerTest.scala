package com.telesoftas.ijplugin.gitconfigcommittemplate

import com.intellij.openapi.Disposable
import com.intellij.openapi.vcs.checkin.CheckinHandler.ReturnResult
import com.telesoftas.ijplugin.gitconfigcommittemplate.testutil.MockCommitMessageHandler
import org.mockito.ArgumentMatchersSugar
import org.mockito.MockitoSugar
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

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

  it should "not allow first commented line" in {
    val (handler, messageUi) = MockCommitMessageHandler.context

    val sut = new CustomCheckinHandler
    sut.getAfterCheckinConfigurationPanel(handler)

    when(messageUi.getText).thenReturn(
      """|# forbidden line
         |Line one
         |Line two""".stripMargin,
    )

    sut.beforeCheckin() shouldBe ReturnResult.CANCEL
  }

}
