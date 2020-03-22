package com.anton.nuclearnation.nation

import com.anton.nuclearnation.ControlledBy
import com.anton.nuclearnation.MapScreen.City
import com.anton.nuclearnation.global.Global
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.msg.{Telegram, Telegraph}
import Global.Events._

object NationLevelTracker extends Telegraph {

  var playerCitiesCounter = 0
  var totalPopulation = 0

  var nationLevel:NationLevel = Level1

  Global.messageDispatcher.addListener(this,CITY_CREATED_EVENT)

  override def handleMessage(msg: Telegram): Boolean = {
    if (msg.message == CITY_CREATED_EVENT){
      val city = msg.extraInfo.asInstanceOf[City]
      if (city.controlledBy == ControlledBy.PLAYER) {
        playerCitiesCounter +=1
        totalPopulation += city.population
        Gdx.app.log("INFO",s"""The city of "${city.name}" has joined the nation of $playerCitiesCounter cities and population of $totalPopulation""")
      }
      nationLevel = nationLevel.updateState()
    }
    true
  }

  sealed trait NationLevel{
    def name:String

    def updateState(cities: Int = playerCitiesCounter, population: Int = totalPopulation): NationLevel = {

      //checking higher level criteria if available
      if (getNextLevel.isDefined && getNextLevel.get.isMatchingCriteria(cities,population)){
          return getNextLevel.get
      }

      //checking current criteria
      if (isMatchingCriteria(cities,population)){
        return this
      }

      //checking lower level criteria if available. If not - the game is over, as this is the lowest level and no
      //other level matches
      if (getPreviousLevel.isDefined && getPreviousLevel.get.isMatchingCriteria(cities,population)){
        return getPreviousLevel.get
      }
      Global.messageDispatcher.dispatchMessage(Global.Events.GAME_OVER_EVENT)
      this
    }

    def isMatchingCriteria(cities:Int, population: Int) : Boolean = {
      cities >= getMinimalCities && population >= getMinimalPopulation
    }

    def getMaxLevel: NationLevel = {
      if (getNextLevel.isDefined){
        getNextLevel.get.getMaxLevel
      } else {
        this
      }
    }

    def getNextLevel: Option[NationLevel]
    def getPreviousLevel: Option[NationLevel]


    def getMinimalCities : Int
    def getMinimalPopulation: Int

  }

  object Level1 extends NationLevel {
    override def name: String = "Isolated town"

    def getNextLevel: Option[NationLevel] = Some(Level2)
    def getPreviousLevel: Option[NationLevel] = None

    override def getMinimalCities: Int = 1
    override def getMinimalPopulation: Int = 1
  }

  object Level2 extends NationLevel {
    override def name: String = "Sparkle of hope"

    def getNextLevel: Option[NationLevel] = Some(Level3)
    def getPreviousLevel: Option[NationLevel] = Some(Level1)

    override def getMinimalCities: Int = 2
    override def getMinimalPopulation: Int = 100
  }

  object Level3 extends NationLevel {
    override def name: String = "Tiny alliance"

    def getNextLevel: Option[NationLevel] = None
    def getPreviousLevel: Option[NationLevel] = Some(Level2)

    override def getMinimalCities: Int = 3
    override def getMinimalPopulation: Int = 200
  }


}
