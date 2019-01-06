package com.anton.nuclearnation

import scala.collection.mutable.ListBuffer

class MapData(mapWidth: Int,mapHeight:Int) {
  private val cellsBuffer = ListBuffer[MapCellData]()

  for (
    x <- 0 until mapWidth;
    y <- 0 until mapHeight
  ) yield  {
    cellsBuffer += new MapCellData(x,y)
  }

  val cells = cellsBuffer.toList

  def getCell(x:Int,y:Int): Option[MapCellData] ={
    cells.find(cell=>cell.x == x && cell.y == y)
  }
}
