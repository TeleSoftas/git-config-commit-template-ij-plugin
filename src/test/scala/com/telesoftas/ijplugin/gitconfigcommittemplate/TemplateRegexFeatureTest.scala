package com.telesoftas.ijplugin.gitconfigcommittemplate

import com.telesoftas.ijplugin.gitconfigcommittemplate.TemplateEnricher.TemplateEnricherImplicit
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TemplateRegexFeatureTest extends AnyFlatSpec with Matchers {

  it should "fill the spots with empty strings if not matched" in {
    """${(^\d+)}: # fill message here""".entichWith("the-name")
//    shouldBe """: # fill message here"""
  }

  it should "fill the spots with default if not matched" in {
    """${(^\d+)<fallback>}: # fill message here""".entichWith("the-name")
//    shouldBe """fallback: # fill message here"""
  }

  it should "fill the spots with matched value" in {
    """${(^\d+)<default>}: # fill message here""".entichWith("123-the-name")
//    shouldBe """123: # fill message here"""
  }

  it should "fill the spots with modified value" in {
    """${(^(\d+))<default>[gh-$1]}: # fill message here""".entichWith("123-the-name")
//    shouldBe """gh-123: # fill message here"""
  }

  it should "work with couple of regexes" in {
    """${(^\d+)}_${(\w+)}: # fill message here""".entichWith("123-the-name")
//    shouldBe """ # fill message here"""
  }

}
