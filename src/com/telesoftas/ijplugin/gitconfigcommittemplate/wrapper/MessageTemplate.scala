package com.telesoftas.ijplugin.gitconfigcommittemplate.wrapper

import com.intellij.openapi.project.Project
import com.telesoftas.ijplugin.gitconfigcommittemplate._

case class MessageTemplate private (parts: Array[String]) {
  def apply(message: String): String =
    if (parts.length == 2 && message.startsWith(parts.head) && message.endsWith(parts.last)) message
    else if (parts.length == 2) parts.mkString(if (message.isEmpty) "# message" else message)
    else parts.head
}

object MessageTemplate {

  def apply(project: Project): Option[MessageTemplate] = {
    Git.commitTemplate(project).map(_.split("#\\{msg}", 2)).map(new MessageTemplate(_))
  }

}
