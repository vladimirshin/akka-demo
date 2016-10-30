package com.shin.akka.actors.local


import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

import com.shin.akka.actors.local.LocalActor.HelloMessage


class LocalActor extends Actor with ActorLogging {

  def receive: Actor.Receive = {
    case HelloMessage(name) =>
      log.info(s"Hello, [$name]!")

    case unknown@_ =>
      log.info(s"[$unknown] message has been received")
  }
}

object LocalActor {
  // HelloMessage class definition here
  @SerialVersionUID(1L) case class HelloMessage(name: String)
}

object StartPoint {
  def main(args: Array[String]): Unit = {
    // create instance of ActorSystem here
    val system = ActorSystem.create("LocalActorSystem")

    // create instance of LocalActor here
    val localActor = system.actorOf(Props[LocalActor])

    localActor ! LocalActor.HelloMessage("Mauritius team")

    localActor ! "Unknown team"
  }
}