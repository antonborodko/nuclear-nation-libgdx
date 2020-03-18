package com.anton.nuclearnation.city.factory
import com.anton.nuclearnation.{ControlledBy, MapScreen}
import com.anton.nuclearnation.MapScreen.City
import com.anton.nuclearnation.global.Global
import com.anton.nuclearnation.nation.NationLevelTracker
import com.badlogic.gdx.Gdx

class ManualCityFactory(cityConfig: CityConfig) extends CityFactory {
  override def getCity: MapScreen.City = {
    val name = cityConfig.name.getOrElse(getName)
    val population = getPop(cityConfig.popFrom, cityConfig.popTo)
    val city = City(name,getCell,population,cityConfig.ownedBy)
    Gdx.app.log("INFO",s"Generated city ${city.name} with population ${city.population} at coords ${city.mapCell.x}/${city.mapCell.y}")
    city
  }
}

object ManualCityFactory {
  def apply(cityConfig: CityConfig): ManualCityFactory = new ManualCityFactory(cityConfig)
}