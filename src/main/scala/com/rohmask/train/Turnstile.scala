package com.rohmask.train

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.rohmask.train.model._
import java.util.Date


/***
 * @problem - Distributed App using Akka toolkit
 * with reference to Rock The JVM Course
 ***/


object Turnstile {
   def props(validator: ActorRef): Props = Props(new Turnstile(validator))
}

class Turnstile(validator: ActorRef) extends Actor with ActorLogging{
  override def receive: Receive = {
    case oy @ Oyster(_, _) => log.info(s"Indivisual Attempting to enter Oyster")
      validator ! EntryAttempt(oy, new Date())
    case EntryAccepted => log.info("GREEN: Allowed")
    case EntryRejected => log.info("RED: Denied, Insufficient balance")
  }
}


class OysterCardValidator extends Actor with ActorLogging{

  override def preStart(): Unit = {
    super.preStart()
    log.info("validator starting.......")
  }

  override def receive: Receive = {
    case EntryAttempt(card @ Oyster(_, amount), _) =>
      log.info(s"validating ${card}")
      if(amount > 5) sender() ! EntryAccepted
      else sender() ! EntryRejected("Insufficient fund")
  }

}



