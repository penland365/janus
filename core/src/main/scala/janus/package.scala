package janus

import cats.data.Xor

package object core {
  type Program = (Array[String]) => Unit
  type JobRemovalResult = String Xor Job
}
