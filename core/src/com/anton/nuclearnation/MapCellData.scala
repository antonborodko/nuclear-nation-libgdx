package com.anton.nuclearnation

import com.anton.nuclearnation.MapCellState.MapCellState
import com.anton.nuclearnation.MapScreen.MapLocation

class MapCellData(val x:Int,val y:Int, var location:Option[MapLocation],var state:MapCellState = MapCellState.UNDISCOVERED) {}


object MapCellState extends Enumeration {
  type MapCellState = Value
  val DISCOVERED, UNDISCOVERED = Value
}
