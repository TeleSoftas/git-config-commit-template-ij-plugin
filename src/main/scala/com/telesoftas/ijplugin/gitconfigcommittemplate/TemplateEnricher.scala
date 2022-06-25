package com.telesoftas.ijplugin.gitconfigcommittemplate

import scala.util.matching.Regex

object TemplateEnricher {
  val string = """${(regex)<default>[modification]}"""

  val ArgumentPart: Regex = """\$\{.*?}""".r
  val RegexPart: Regex = """\((.*?)\)""".r
  val DefaultPart: Regex = """<.*?>""".r
  val ModificationPart: Regex = """\[.*?]""".r

  case class ArgDeconstruction(arg: String, regex: Regex, default: Option[String], modification: Option[String])
  case class TemplateDeconstruction(raw: String, args: List[ArgDeconstruction])

  def deconstruct(template: String): TemplateDeconstruction = {
    val value = ArgumentPart.findAllMatchIn(template).toList.map(m => {
      ArgDeconstruction(
        m.source.toString,
        RegexPart.findFirstIn(m.source).map(_.r).head,
        DefaultPart.findFirstIn(m.source),
        ModificationPart.findFirstIn(m.source)
      )
    })
    TemplateDeconstruction(template, value)
  }

  implicit class TemplateEnricherImplicit(base: String) {
    def entichWith(additionalInfo: String): String = {
      println(deconstruct(base))
      base + additionalInfo
    }
  }

}
