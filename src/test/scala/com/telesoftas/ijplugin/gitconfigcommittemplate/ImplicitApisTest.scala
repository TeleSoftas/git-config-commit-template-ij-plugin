package com.telesoftas.ijplugin.gitconfigcommittemplate

import org.mockito.{ArgumentMatchersSugar, MockitoSugar}
import org.scalatest.flatspec._
import org.scalatest.matchers.should._

import java.io._

class ImplicitApisTest extends AnyFlatSpec with Matchers with MockitoSugar with ArgumentMatchersSugar {

  "ImplicitsFileApi" should "return parent" in {
    new File(".").getAbsoluteFile.parent shouldBe defined
    new File(".").parent shouldBe empty
  }

  it should "read file" in {
    new File("src/test/resources/nonEmptyFile").read.toOption shouldBe Some("contents")
    new File("src/test/resources/nonExisting").read.toOption shouldBe empty
  }

  it should "list contents" in {
    new File("src/test/resources").contents shouldNot be(List.empty)
    new File("src/test/voidness").contents shouldBe empty
  }

}
