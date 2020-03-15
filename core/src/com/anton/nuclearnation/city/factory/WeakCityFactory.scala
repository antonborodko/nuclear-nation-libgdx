package com.anton.nuclearnation.city.factory

import com.anton.nuclearnation.{ControlledBy, MapCellData}
import com.anton.nuclearnation.MapScreen.City
import com.badlogic.gdx.Gdx

class WeakCityFactory extends CityFactory {
  override def getCity: City = {
    val name = getName
    val population = getPop(10,50) //population from 10 to 50
    val city = City(name,getCell,population,ControlledBy.COMPUTER)
    Gdx.app.log("INFO",s"Generated city ${city.name} with population ${city.population} at coords ${city.mapCell.x}/${city.mapCell.y}")
    city
  }
}

object WeakCityFactory {
  def apply(): WeakCityFactory = new WeakCityFactory()
}
