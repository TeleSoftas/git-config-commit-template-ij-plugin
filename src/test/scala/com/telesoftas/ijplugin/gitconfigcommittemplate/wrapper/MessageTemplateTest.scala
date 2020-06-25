package com.telesoftas.ijplugin.gitconfigcommittemplate.wrapper

import com.intellij.openapi.project.Project
import git4idea.commands.Git
import org.mockito.ArgumentMatchersSugar
import org.mockito.MockitoSugar
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MessageTemplateTest extends AnyFlatSpec with Matchers with MockitoSugar with ArgumentMatchersSugar {

  it should "Initiates according git commit.template" in {
    MessageTemplate(
      mock[Project],
      mock[Git],
      (_, _, _, _) => Some("template"),
    ) shouldBe defined
  }

  it should "format pure template when no arg template" in {
    MessageTemplate(
      mock[Project],
      mock[Git],
      (_, _, _, _) => Some("template"),
    ).map(_("any")) shouldBe Some("template")
  }

  it should "format template when arg template added" in {
    MessageTemplate(
      mock[Project],
      mock[Git],
      (_, _, _, _) => Some(s"error: #{msg} // jeeee"),
    ).map(_("any")) shouldBe Some("error: any // jeeee")
  }

  it should "fail if cant get commit.template" in {
    MessageTemplate(mock[Project], mock[Git], (_, _, _, _) => None) shouldBe None
  }

}
