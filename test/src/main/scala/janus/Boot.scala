package janus
package test

import janus.core.Job
import janus.core.scheduler
import java.util.concurrent.TimeUnit
import java.util.UUID
import org.joda.time.DateTime
import scala.concurrent.duration.Duration

object Boot {

  def main(args: Array[String]): Unit = {
    val func = (args: Array[String]) => {
      println("Starting job of printing every argument!")
      args.foreach(x => println(x))
    }
    val job = new Job(func, Array("foo", "bar"), DateTime.now, Duration(3, TimeUnit.SECONDS))
    val id = scheduler.submit(job)
    println(s"Id for job is $id")
    scheduler.run
    val cancelResult = scheduler.cancel(UUID.randomUUID)
    println(s"Attempted to cancel random id gives $cancelResult")
    Thread.sleep(10000L)
    val jobCancelResult = scheduler.cancel(id)
    println(s"Attempted to cancel job id gives $jobCancelResult")
    Thread.sleep(3000L)
    scheduler.stop
  }
}
