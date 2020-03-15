package com.anton.nuclearnation.city.factory

import com.anton.nuclearnation.{ControlledBy, MapCellData}
import com.anton.nuclearnation.MapScreen.City


object WeakCityFactory extends CityFactory {
  override def getCity(): City = {
    val name = getName
    val population = getPop(10,50) //population from 10 to 50
    City(name,getCell,population,ControlledBy.COMPUTER)
  }
}
