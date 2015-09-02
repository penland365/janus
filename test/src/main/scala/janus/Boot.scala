package janus
package test

import janus.core.Job
import janus.core.scheduler
import java.util.concurrent.TimeUnit
import org.joda.time.DateTime
import scala.concurrent.duration.Duration

object Boot {

  def main(args: Array[String]): Unit = {
    val func = (args: Array[String]) => {
      println("Starting job of printing every argument!")
      args.foreach(x => println(x))
      println("All arguments printed!")
    }
    val job = new Job(func, Array("foo", "bar"), DateTime.now, Duration(2, TimeUnit.SECONDS))
    val id = scheduler.submit(job)
    println(s"Id for job is $id")
    scheduler.run
    Thread.sleep(5000L)
    scheduler.stop
  }
}
