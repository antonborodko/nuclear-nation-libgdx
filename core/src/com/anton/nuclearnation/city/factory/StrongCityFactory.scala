package com.anton.nuclearnation.city.factory

import com.anton.nuclearnation.{ControlledBy, MapCellData}
import com.anton.nuclearnation.MapScreen.City


object StrongCityFactory extends CityFactory {
  override def getCity(): City = {
    val name = getName
    val population =getPop(150,200) //population from 150 to 200
    City(name,getCell,population,ControlledBy.COMPUTER)
  }
}