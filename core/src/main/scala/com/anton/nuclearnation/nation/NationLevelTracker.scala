package com.anton.nuclearnation.nation

import com.anton.nuclearnation.ControlledBy
import com.anton.nuclearnation.MapScreen.City
import com.anton.nuclearnation.global.Global
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.msg.{Telegram, Telegraph}

trait NationLevelTracker extends Telegraph

object NationLevelTracker extends NationLevelTracker {

  val CITY_CREATED_EVENT = 1

  var playerCitiesCounter = 0
  var totalPopulation = 0

  Global.messageDispatcher.addListener(this,CITY_CREATED_EVENT)

  override def handleMessage(msg: Telegram): Boolean = {
    if (msg.message == CITY_CREATED_EVENT){
      val city = msg.extraInfo.asInstanceOf[City]
      if (city.controlledBy == ControlledBy.PLAYER) {
        playerCitiesCounter +=1
        totalPopulation += city.population
        Gdx.app.log("INFO",s"""The city of "${city.name}" has joined the nation of $playerCitiesCounter cities and population of $totalPopulation""")
      }
    }
    true
  }
}
