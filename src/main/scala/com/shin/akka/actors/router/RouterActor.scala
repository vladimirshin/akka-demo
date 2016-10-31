package com.shin.akka.actors.router


import akka.actor.{Actor, ActorLogging, Props, ActorSystem}
import akka.routing.{FromConfig, Broadcast}
import com.typesafe.config.ConfigFactory

import RouterActor.Hello

class LocalActor extends Actor with ActorLogging {
  def receive: Actor.Receive = {
    case unknown@_ =>
      log.info(s"[$unknown] message had been received")
  }
}

class RouterActor extends Actor with ActorLogging {

  context.actorOf(Props[LocalActor])

  def receive: Actor.Receive = {
    case Hello(name) =>
      log.info(s"Hello $name!")

    case unknown@_ =>
      log.info(s"[$unknown] message had been received")
  }
}

object RouterActor {
  @SerialVersionUID(1L) final case class Hello(name: String)
}

object StartPoint {
  def main(args: Array[String]): Unit = {
    // round-robin-pool
    // random-pool
    // broadcast-pool
    // scatter-gather-pool
    // smallest-mailbox-pool
    // consistent-hashing-pool
    val config =
      """
        | akka.actor.deployment {
        |   /router_test/ {
        |     router = round-robin-pool
        |     nr-of-instances = 3
        |     # within = 3 seconds
        |     #resizer.lower-bound = 1
        |     #resizer.upper-bound = 2
        |   }
        | }
      """.stripMargin
    val system = ActorSystem.create("RouterTest", ConfigFactory.parseString(config))
    val router = system.actorOf(FromConfig.props(Props[RouterActor]), "router_test")

    for (i <- 0 to 4) router ! Hello(s"[$i] Mauritius")
    //for (i <- 0 until 1) router ! Broadcast(s"[$i] Mauritius")
  }
}
