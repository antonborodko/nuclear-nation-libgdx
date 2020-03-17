package com.anton.nuclearnation

import com.anton.nuclearnation.global.Global

import scala.collection.mutable.ListBuffer
import scala.util.Random

class MapCoordsGenerator(distanceBetweenSettlements:Int) {

  val availableTiles : ListBuffer[(Int,Int)] = ListBuffer()

  //filling available tiles (except the map boundaries)
  for (
    x <- 1 until Global.mapWidthTiles - 1;
    y <- 1 until Global.mapHeightTiles - 1
  ) yield {
    availableTiles += Tuple2(x,y)
  }

  def getCoords : (Int,Int) = {

    if (availableTiles.isEmpty ){
      throw new RuntimeException("No new tiles to generate from")
    }
    val randomTile = availableTiles(Random.nextInt(availableTiles.size))

    //deleting available tiles around it if any
    for (
      x <- randomTile._1-distanceBetweenSettlements to randomTile._1 + distanceBetweenSettlements;
      y <- randomTile._2-distanceBetweenSettlements to randomTile._2 + distanceBetweenSettlements
    ) yield{
      val tile = availableTiles.find(tile=>tile._1 == x && tile._2 == y)
      if (tile.isDefined){
        availableTiles -= tile.get
      }
    }
    randomTile
  }
}
