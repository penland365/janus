package janus
package core

import cats.data.Xor
import java.util.UUID
import org.joda.time.DateTime
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.Duration

/**
  * Represents a JVM wide scheduler. The scheduler runs on a separate thread from the main
  *     application thread and blocks per program.
 */
object scheduler {
  @volatile private var shouldThreadRun = false;
  @volatile private var schedules = ListBuffer.empty[Schedule]

  /**
    * Submit a Job to the scheduler to run. If the jobs is scheduled to run immediately, it will
    *   run when the next time the job comes up
    * @return id a UUID unique to the job itself
   */
  def submit(job: Job): UUID = {
    val schedule = new Schedule(job)
    schedules += schedule
    schedule.id
  }

  /**
    * Cancel a specific job
    * @returns A disjunction that returns the removed job if found, or the id requested
    *   if no job is found by that id
   */
  def cancel(id: UUID): JobRemovalResult = schedules.find { _.id == id } match {
    case Some(schedule) => {
      schedules = schedules.filterNot { _.id == id }
      Xor.Right(schedule.job)
    }
    case None           => Xor.Left(s"No job found for id $id")
  }

  /**
    * Runs the scheduler. Can only be activated if the scheduler is currently off
  */
  def run: Unit = if(!shouldThreadRun) {
    shouldThreadRun = true
    thread.start
  }

  /**
    * Stops the scheduler gracefully, completing a scan of the job queue to see if any jobs 
    *   need to run. All jobs are run if needed
   */
  def stop: Unit = shouldThreadRun = false

  /**
    *  Destroys the thead. This method WILL THROW a java.lang.ThreadDeath Exception
    *  @throws java.lang.ThreadDeathException
   */
  def killDashNine: Unit = thread.stop

  @annotation.tailrec
  private def go(xs: List[Schedule]): Unit = xs match {
    case h :: t => h.runJobIfTimeToRun; go(t)
    case t      => 
  }

  private[this] val thread = new Thread {
    override def run: Unit = {
      while(shouldThreadRun) { go(schedules.toList) }
    }
  }
}

private[core] final class Schedule(j: Job) {
  val job = j
  var lastRun: Option[DateTime] = None
  val id = UUID.randomUUID

  def runJobIfTimeToRun: Unit = if(timeToRun) job.p(job.args)

  private def timeToRun: Boolean = lastRun match {
    case Some(x) => {
      val nextRun = x.plusSeconds(job.frequency.toSeconds.toInt)
      runWindow(nextRun, DateTime.now)
    }
    case None => runWindow(job.startTime, DateTime.now) 
  }

  private val runWindow = (deadline: DateTime, current: DateTime) => {
    val shouldRun = deadline.isBefore(current)
    if(shouldRun) lastRun = Some(current)
    shouldRun
  }
}
