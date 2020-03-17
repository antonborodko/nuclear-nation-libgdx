package main.com.anton.nuclearnation.global

import com.anton.nuclearnation.MapData
import com.badlogic.gdx.ai.msg.MessageDispatcher

object Global {
  val mapWidthTiles = 30
  val mapHeightTiles = 30
  val mapData = new MapData(mapWidthTiles,mapHeightTiles)
  val messageDispatcher = new MessageDispatcher
}
