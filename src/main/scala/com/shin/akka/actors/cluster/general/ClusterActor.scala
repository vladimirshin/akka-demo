package com.shin.akka.actors.cluster.general


import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._


class ClusterActor extends Actor with ActorLogging {

  private[this] val cluster = Cluster(context.system)

  override def preStart(): Unit =
    cluster.subscribe(
      self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember]
    )

  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive: Actor.Receive = {
    case msg@MemberUp(member) =>
      log.info(s"$msg has been received")

    case msg@UnreachableMember(member) =>
      log.info(s"$msg has been received")

    case msg@MemberRemoved(member, previousStatus) =>
      log.info(s"$msg has been received",
        member.address, previousStatus)

    case msg@_ =>
      log.info(s"unknown [$msg] has been received")
  }
}

object ClusterActor {

}

object StartPoint {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem.create("ClusterSystem")
    val actor = system.actorOf(Props[ClusterActor])
  }
}
