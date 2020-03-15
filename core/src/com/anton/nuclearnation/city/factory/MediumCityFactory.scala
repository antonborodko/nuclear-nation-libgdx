package com.anton.nuclearnation.city.factory

import com.anton.nuclearnation.{ControlledBy, MapCellData}
import com.anton.nuclearnation.MapScreen.City


object MediumCityFactory extends CityFactory {
  override def getCity(): City = {
    val name = getName
    val population = getPop(60,100)//population from 60 to 100
    City(name,getCell,population,ControlledBy.COMPUTER)
  }
}