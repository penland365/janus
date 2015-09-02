package janus

import cats.data.Xor

package object core {
  /**
    * The Base type for the scheduler.  It's based on the Main Method of a Scala Program, 
    *   consuming Arguments and returning a Unit. All programs, according to the scheduler,
    *   are orthoganal.
   */
  type Program = (Array[String]) => Unit

  /**
    * Disjunction for mainting results on possible failure actions
   */
  type JobRemovalResult = String Xor Job
}
