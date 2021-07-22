package com.rohmask.train.clusterShardingSettings

import akka.cluster.sharding.ShardRegion
import com.rohmask.train.model.{EntryAttempt, Oyster}

object TurnstileSettings {

  val numberOfShards = 10 // Use 10x number of nodes in cluster
  val numberOfEntities = 100 //use 10x num of shards

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case attempt@EntryAttempt(Oyster(cardId, _), _) =>
      val entity = cardId.hashCode().abs % numberOfEntities
      (entity.toString, attempt)
  }

  val extractShardId: ShardRegion.ExtractShardId = {
    case EntryAttempt(Oyster(cardId, _), _) =>
      val shardID = cardId.hashCode().abs % numberOfShards
      shardID.toString
  }

}
