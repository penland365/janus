package janus
package core

import org.joda.time.DateTime
import scala.concurrent.duration.Duration

/**
  * A Job to submit to the scheduler.  The current implementation takes a Program of type 
  *     Array[String] => Unit, the arguments for this function, the start time for the job,
  *     and the frequency the job should be run.
 */
case class Job(p: Program, args: Array[String], startTime: DateTime, frequency: Duration)
