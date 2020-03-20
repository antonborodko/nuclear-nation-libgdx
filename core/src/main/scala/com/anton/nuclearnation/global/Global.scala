package com.anton.nuclearnation.global

import com.anton.nuclearnation.MapData
import com.badlogic.gdx.ai.msg.MessageDispatcher

object Global {
  val mapWidthTiles = 30
  val mapHeightTiles = 30
  val mapData = new MapData(mapWidthTiles,mapHeightTiles)
  val messageDispatcher = new MessageDispatcher


  object Events {
    private val GAME_EVENTS_GROUP = 0
    private val CITY_EVENTS_GROUP = 99


    val CITY_CREATED_EVENT = CITY_EVENTS_GROUP + 1
    val GAME_OVER_EVENT= GAME_EVENTS_GROUP + 1
  }

}
