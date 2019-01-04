package com.anton.nuclearnation

import com.anton.nuclearnation.MapCellState.MapCellState

class MapCellData(val x:Int,val y:Int,var state:MapCellState = MapCellState.UNDISCOVERED) {
}


object MapCellState extends Enumeration {
  type MapCellState = Value
  val DISCOVERED, UNDISCOVERED = Value
}
