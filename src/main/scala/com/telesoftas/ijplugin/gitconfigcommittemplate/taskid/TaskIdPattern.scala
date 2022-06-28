package com.telesoftas.ijplugin.gitconfigcommittemplate.taskid

import scala.collection.mutable
import scala.util.matching.Regex

case class TaskIdPattern(key: String, pattern: Regex, value: PartialFunction[String, String])
object TaskIdPattern {

  private val _values: mutable.Set[TaskIdPattern] = mutable.Set()

  val GhTaskId: TaskIdPattern   = TaskIdPattern("${GhTaskId}", """(\d+).*""".r, id => s"gh-$id")
  val JiraTaskId: TaskIdPattern = TaskIdPattern("${JiraTaskId}", """([A-Z]+-\d+).*""".r)
  val TaskId: TaskIdPattern     = TaskIdPattern("${TaskId}", "".r)(GhTaskId.value orElse JiraTaskId.value)

  val Empty: TaskIdPattern = TaskIdPattern("Empty", "".r, { case _ => "" })

  def values: Set[TaskIdPattern] = _values.toSet

  def apply(
      key: String,
      Pattern: Regex,
      mutate: String => String = s => s
  )(
      implicit value: PartialFunction[String, String] = { case Pattern(taskId) => mutate(taskId) }
  ): TaskIdPattern = {
    val taskIdPattern = TaskIdPattern(key, Pattern, value)
    _values += taskIdPattern
    taskIdPattern
  }
}
