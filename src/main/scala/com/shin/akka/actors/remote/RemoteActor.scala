package com.shin.akka.actors.remote


import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.typesafe.config.ConfigFactory


class RemoteActor extends Actor with ActorLogging {

  def receive: Actor.Receive = {
    case unknown@_ =>
      log.info(s"[$unknown] message has been received")
  }
}

object RemoteActor {
  @SerialVersionUID(1L) case class A()
}

object StartPoint {
  def main(args: Array[String]): Unit = {
    val config =
      """
        | akka.actor.provider = remote
        | akka.remote.enabled-transports = ["akka.remote.netty.tcp"]
        | akka.remote.netty.tcp.hostname = "localhost"
        |
        | akka.remote.netty.tcp.port = 9001
        | #akka.remote.netty.tcp.port = 9002
      """.stripMargin

    val systemA = ActorSystem.create("RemoteActorSystemA", ConfigFactory.parseString(config))
    val actorA = systemA.actorOf(Props[RemoteActor], "RemoteActorA")
    actorA ! "Hi"

//    val systemB = ActorSystem.create("RemoteActorSystemB", ConfigFactory.parseString(config))
//    val actorA = systemB.actorSelection("akka.tcp://RemoteActorSystemA@localhost:9001/user/RemoteActorA")
//    actorA ! "How are you?"
  }
}
