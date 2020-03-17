package com.anton.nuclearnation.city.factory

import com.anton.nuclearnation.{ControlledBy, MapCellData}
import com.anton.nuclearnation.MapScreen.City
import com.badlogic.gdx.Gdx

class MediumCityFactory extends CityFactory{
  override def getCity: City = {
    val name:String = getName
    val population = getPop(60,100)//population from 60 to 100
    val city = City(name,getCell,population,ControlledBy.COMPUTER)
    Gdx.app.log("INFO",s"Generated city ${city.name} with population ${city.population} at coords ${city.mapCell.x}/${city.mapCell.y}")
    city
  }
}

object MediumCityFactory{
  def apply(): MediumCityFactory = new MediumCityFactory()
}