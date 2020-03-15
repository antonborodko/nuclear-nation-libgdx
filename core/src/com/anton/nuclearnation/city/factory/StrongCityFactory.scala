package com.anton.nuclearnation.city.factory

import com.anton.nuclearnation.{ControlledBy, MapCellData}
import com.anton.nuclearnation.MapScreen.City
import com.badlogic.gdx.Gdx


class StrongCityFactory extends CityFactory {
  override def getCity: City = {
    val name = getName
    val population =getPop(150,200) //population from 150 to 200
    val city = City(name,getCell,population,ControlledBy.COMPUTER)
    Gdx.app.log("INFO",s"Generated city ${city.name} with population ${city.population} at coords ${city.mapCell.x}/${city.mapCell.y}")
    city
  }
}

object StrongCityFactory {
  def apply(): StrongCityFactory = new StrongCityFactory()
}