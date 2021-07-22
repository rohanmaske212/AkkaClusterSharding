package com.rohmask.train

import akka.actor.{ActorSystem, Props}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import akka.cluster.sharding.external.ExternalShardAllocationStrategy.ShardRegion
import com.rohmask.train.clusterShardingSettings.TurnstileSettings
import com.rohmask.train.model._
import com.typesafe.config.ConfigFactory

import java.util.UUID
import scala.util.Random

class TubeStation(port: Int, numberOfTurnstiles:Int) extends App{
  val config = ConfigFactory.parseString(
    s"""
       |akka.remote.canonical.port = ${port}
       |""".stripMargin).withFallback(ConfigFactory.load("londoncluster.conf"))

  val system = ActorSystem("LondonCluster", config)

  val validatorShardRegionRef: ShardRegion = ClusterSharding(system).start(
    typeName = "OysterCardValidator",
    entityProps = Props[OysterCardValidator],
    settings = ClusterShardingSettings(system),
    extractEntityId = TurnstileSettings.extractEntityId,
    extractShardId = TurnstileSettings.extractShardId
  )

  val turnstiles: Seq[ShardRegion] = (1 to numberOfTurnstiles).map(
    _ => system.actorOf(Turnstile.props(validatorShardRegionRef))
  )

  Thread.sleep(10000)

  for(_ <- 1 to 100){
    val turnstileIndex = Random.nextInt(numberOfTurnstiles)
    val randomTurnstiles = turnstiles(turnstileIndex)

    randomTurnstiles ! Oyster(UUID.randomUUID().toString, Random.nextDouble() * 20)
    Thread.sleep(200)
  }

}


//Run these objects to start the App
object test_one extends TubeStation(2551, 10)
object test_two extends TubeStation(2552, 10)
object test_three extends TubeStation(2553, 10)

