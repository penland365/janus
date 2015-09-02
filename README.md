janus - A Toy Scheduler for Scala
====
### Fast start
```
./sbt clean update compile test/run
```
This will run the toy test program, which creates a basic job of printing off it's arguments every few seconds, then deleting it

### Getting Started
The scheduler allows a user to schedule jobs to be run at any point in the future.
The most important type is
```scala
Program = (Array[String] => Unit)
```
That is, a function that takes an `Array[String]` and return `Unit`.  If this looks familiar, it should
```scala
def main(args: Array[String]): Unit
```
is the same type.  A `Job` allows you to create a program to run at some point in the future, with the option to run it on a set frequency.
```scala
case class Job(p: Program, args: Array[String], startTime: DateTime, frequency: Duration)
```
Once you create a job, you submit it to the scheduler.  If submission is successful, a `UUID` to identify the job is returned to you
```scala
import janus.core.scheduler
import java.util.concurrent.TimeUnit
import org.joda.time.DateTime
import scala.concurrent.duration.Duration

val p = (args: Array[String]) => args.foreach(x => println(x))
val job = new Job(args, Array("foo", "bar"), DateTime.now, Duration(2. TimeUnit.SECONDS))
val id = scheduler.submit(job)
```
Attempting to cancel the job returns a disjunction that will return the job, or provide information if the job was not found
```scala
val result = scheduler.cancel(id)
println(result) //Right(Job(<function1>,[Ljava.lang.String;@41b9748a,2015-09-02T15:11:10.369-05:00,3 seconds))
```
while cancelling an random UUID gives
```scala
val result = scheduler.cancel(UUID.randomUUID)
println(result) // Left(No job found for id 76ffebeb-c073-43f1-97aa-c6db448083e1)
```
It's possible to stop the entire Scheduler via
```scala
scheduler.stop
```
this will attempt a graceful shutdown by allowing the current queue of possible jobs to complete

If you need things `diediedie` there's always
```scala
scheduler.killDashNine
```
This is *guranteed* to throw a `java.lang.ThreadDeath` exception, but when need something to die, you need it to die.

#### Requirements
Java 1.7 or higher.  Running the `sbt` script will pull down *all* *the* *things*™

#### Scaladoc is available
```
./sbt doc
```
will generate scaladoc documentation available under `core/target/scala-2.11/api/index.html`
