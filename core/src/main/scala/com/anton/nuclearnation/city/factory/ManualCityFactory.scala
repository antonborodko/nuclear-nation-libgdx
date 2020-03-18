package com.anton.nuclearnation.city.factory
import com.anton.nuclearnation.{ControlledBy, MapScreen}
import com.anton.nuclearnation.MapScreen.City
import com.anton.nuclearnation.global.Global
import com.anton.nuclearnation.nation.NationLevelTracker
import com.badlogic.gdx.Gdx

class ManualCityFactory(cityConfig: CityConfig) extends CityFactory {
  var config:Option[CityConfig] = None
  override def getCity: MapScreen.City = {
    val name = cityConfig.name.getOrElse(getName)
    val population = getPop(cityConfig.popFrom, cityConfig.popTo)
    val city = City(name,getCell,population,cityConfig.ownedBy)
    city
  }
}

object ManualCityFactory {
  var factory: Option[ManualCityFactory] = None
  def apply(cityConfig: CityConfig): ManualCityFactory = {
    if (factory.isEmpty) {
      factory = Some(new ManualCityFactory(cityConfig))
    } else {
      factory.get.config = Some(cityConfig)
    }
    factory.get
  }
}