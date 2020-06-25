package com.telesoftas.ijplugin.gitconfigcommittemplate

import java.io._

import com.intellij.openapi.project._
import org.mockito.ArgumentMatchersSugar
import org.mockito.MockitoSugar
import org.scalatest.flatspec._
import org.scalatest.matchers.should._

class ImplicitApisTest extends AnyFlatSpec with Matchers with MockitoSugar with ArgumentMatchersSugar {

  "ImplicitsFileApi" should "return parent" in {
    new File(".").getAbsoluteFile.parent shouldBe defined
    new File(".").parent shouldBe empty
  }

  it should "read file" in {
    new File("src/test/resources/nonEmptyFile").read shouldBe Some("contents")
    new File("src/test/resources/nonExisting").read shouldBe empty
  }

  it should "list contents" in {
    new File("src/test/resources").contents shouldNot be(List.empty)
    new File("src/test/voidness").contents shouldBe empty
  }

  it should "look parent up until it matches condition" in {
    new File("src/test/resources/nonEmptyFile").lookUp(_.getName == "test") shouldBe defined
    new File("src/test/resources/nonEmptyFile").lookUp(_.getName.contains("!@#$!@#$!%#$%")) shouldBe empty
  }

  "ImplicitProjectApi" should "find repoRoot" in {
    val mockProject = mock[Project]

    when(mockProject.getProjectFilePath).thenReturn("src/test/resources/")
    mockProject.repoRoot shouldBe defined

    when(mockProject.getProjectFilePath).thenReturn("/")
    mockProject.repoRoot shouldBe empty
  }

}
