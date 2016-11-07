package com.shin.akka.actors.local


import scala.concurrent.duration._
import akka.actor.{Actor, ActorLogging, ActorSystem, Props, OneForOneStrategy}
import akka.actor.SupervisorStrategy.{Resume, Restart, Stop, Escalate}

import com.shin.akka.actors.local.LocalActor.HelloMessage


class ChildActor extends Actor with ActorLogging {

  override def postRestart(reason: Throwable): Unit = {
    super.postRestart(reason)
    log.info("Child actor restarted")
  }
    def receive: Actor.Receive = {
      case any@_ =>
        throw new Exception("Runtime exception")
    }
}

class LocalActor extends Actor with ActorLogging {

  private[this] val child = context.actorOf(Props[ChildActor])

  override def postRestart(reason: Throwable): Unit = {
    super.postRestart(reason)
    log.info("Local actor restarted")
  }

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1.minute) {
      case _: ArithmeticException      => Resume
      case _: NullPointerException     => Restart
      case _: IllegalArgumentException => Stop

      case _: Exception                =>
        log.info("Child actor throwed an exception")
        Escalate
    }

  def receive: Actor.Receive = {
    case HelloMessage(name) =>
      child ! "Kill"
      log.info(s"Hello, [$name]!")

    case unknown =>
      log.info(s"[$unknown] message has been received")
  }
}

object LocalActor {
  @SerialVersionUID(1L) case class HelloMessage(name: String)
}

object StartPoint {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem.create("LocalActorSystem")

    val localActor = system.actorOf(Props[LocalActor])

    localActor ! LocalActor.HelloMessage("Mauritius team")
  }
}