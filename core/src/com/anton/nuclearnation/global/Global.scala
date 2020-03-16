package com.anton.nuclearnation.global

import com.anton.nuclearnation.MapData
import com.anton.nuclearnation.MapScreen.MapLocation

import scala.collection.mutable.ListBuffer

object Global {
  val mapWidthTiles = 30
  val mapHeightTiles = 30
  val mapData = new MapData(mapWidthTiles,mapHeightTiles)
  val locations = ListBuffer[MapLocation]()
}
