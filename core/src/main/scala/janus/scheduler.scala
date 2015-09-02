package janus
package core

import cats.data.Xor
import java.util.UUID
import org.joda.time.DateTime
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.Duration

object scheduler {
  @volatile private var shouldThreadRun = false;
  @volatile private var schedules = ListBuffer.empty[Schedule]

  def submit(job: Job): UUID = {
    val schedule = new Schedule(job)
    schedules += schedule
    schedule.id
  }

  def cancel(id: UUID): JobRemovalResult = schedules.find { _.id == id } match {
    case Some(schedule) => {
      schedules = schedules.filterNot { _.id == id }
      Xor.Right(schedule.job)
    }
    case None           => Xor.Left(s"No job found for id $id")
  }

  def run: Unit = if(!shouldThreadRun) {
    shouldThreadRun = true
    thread.start
  }

  def stop: Unit = shouldThreadRun = false

  @annotation.tailrec
  def go(xs: List[Schedule]): Unit = xs match {
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
      val next = x.plusSeconds(job.frequency.toSeconds.toInt)
      val shouldRun = next.isBefore(DateTime.now)
      if(shouldRun) lastRun = Some(DateTime.now)
      shouldRun
    }
    case None => {
      val now = DateTime.now
      val shouldRun = job.startTime.isBefore(now)
      if(shouldRun) lastRun = Some(now)
      shouldRun
    }
  }
}
