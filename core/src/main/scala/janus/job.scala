package janus
package core

import org.joda.time.DateTime
import scala.concurrent.duration.Duration

case class Job(p: Program, args: Array[String], startTime: DateTime, frequency: Duration)
