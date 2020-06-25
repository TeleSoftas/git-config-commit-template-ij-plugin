package com.telesoftas.ijplugin.gitconfigcommittemplate

import org.mockito.ArgumentMatchersSugar
import org.mockito.MockitoSugar
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CustomCheckinHandlerFactoryTest extends AnyFlatSpec with Matchers with MockitoSugar with ArgumentMatchersSugar {

  it should "create CommitTemplateCheckin" in {
    val handlerFactory = new CustomCheckinHandlerFactory()
    handlerFactory.createHandler(null, null) shouldBe a[CustomCheckinHandler]
  }

}
