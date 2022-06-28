package com.telesoftas.ijplugin.gitconfigcommittemplate

import com.telesoftas.ijplugin.gitconfigcommittemplate.taskid.TaskIdInjector.TaskIdSetter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TemplateTaskIdFeatureTest extends AnyFlatSpec with Matchers {

  "TaskId" should "be replaced with emptiness when id is not found" in {
    val givenTemplate =
      """${TaskId}: # message here
        |
        |# Description""".stripMargin
    val givenBranchName = "not logged activity"
    givenTemplate.setTaskId(givenBranchName) shouldBe
      """: # message here
        |
        |# Description""".stripMargin
  }

  it should "be replaced with GitHub task Id" in {
    val givenTemplate =
      """${TaskId}: # message here
        |
        |# Description""".stripMargin
    val givenBranchName = "17-make-some-feature"
    givenTemplate.setTaskId(givenBranchName) shouldBe
      """gh-17: # message here
        |
        |# Description""".stripMargin
  }

  it should "be replaced with Jira task Id" in {
    val givenTemplate =
      """${TaskId}: # message here
        |
        |# Description""".stripMargin
    val givenBranchName = "JIRA-0023_make-some-feature"
    givenTemplate.setTaskId(givenBranchName) shouldBe
      """JIRA-0023: # message here
        |
        |# Description""".stripMargin
  }

  "GhTaskId" should "be replaced with emptiness when id is not found" in {
    val givenTemplate =
      """${GhTaskId}: # message here
        |
        |# Description""".stripMargin
    val givenBranchName = "not logged activity"
    givenTemplate.setTaskId(givenBranchName) shouldBe
      """: # message here
        |
        |# Description""".stripMargin
  }

  it should "be replaced with GitHub task Id" in {
    val givenTemplate =
      """${GhTaskId}: # message here
        |
        |# Description""".stripMargin
    val givenBranchName = "17-make-some-feature"
    givenTemplate.setTaskId(givenBranchName) shouldBe
      """gh-17: # message here
        |
        |# Description""".stripMargin
  }

  "JiraTaskId" should "be replaced with emptiness when id is not found" in {
    val givenTemplate =
      """${JiraTaskId}: # message here
        |
        |# Description""".stripMargin
    val givenBranchName = "not logged activity"
    givenTemplate.setTaskId(givenBranchName) shouldBe
      """: # message here
        |
        |# Description""".stripMargin
  }

  it should "be replaced with Jira task Id" in {
    val givenTemplate =
      """${JiraTaskId}: # message here
        |
        |# Description""".stripMargin
    val givenBranchName = "JIRA-0023_make-some-feature"
    givenTemplate.setTaskId(givenBranchName) shouldBe
      """JIRA-0023: # message here
        |
        |# Description""".stripMargin
  }

}
