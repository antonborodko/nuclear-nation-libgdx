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

  Global.messageDispatcher.addListener(this,CITY_CREATED_EVENT)

  def updateLevel(): Unit ={

  }

  override def handleMessage(msg: Telegram): Boolean = {
    if (msg.message == CITY_CREATED_EVENT){
      val city = msg.extraInfo.asInstanceOf[City]
      if (city.controlledBy == ControlledBy.PLAYER) {
        playerCitiesCounter +=1
        totalPopulation += city.population
        Gdx.app.log("INFO",s"""The city of "${city.name}" has joined the nation of $playerCitiesCounter cities and population of $totalPopulation""")
      }
      updateLevel()
    }
    true
  }

  sealed trait NationLevel{
    def name:String

    def updateState(cities: Int, population: Int): NationLevel = {


      //checking higher level criteria if available
      if (getHigherLevel.isDefined && getHigherLevel.get.isMatchingCriteria(cities,population)){
          return getHigherLevel.get
      }

      //checking current criteria
      if (isMatchingCriteria(cities,population)){
        return this
      }

      //checking lower level criteria if available. If not - the game is over, as this is the lowest level and no
      //other level matches
      if (getLowerLevel.isDefined && getLowerLevel.get.isMatchingCriteria(cities,population)){
        return getLowerLevel.get
      }
      Global.messageDispatcher.dispatchMessage(Global.Events.GAME_OVER_EVENT)
      this
    }

    protected def getHigherLevel: Option[NationLevel]
    protected def getLowerLevel: Option[NationLevel]
    protected def isMatchingCriteria(cities:Int, population: Int) : Boolean

  }

  object Level1 extends NationLevel {
    override def name: String = "Isolated farmers"

    override protected def getHigherLevel: Option[NationLevel] = None
    override protected def getLowerLevel: Option[NationLevel] = Some(Level2)

    override protected def isMatchingCriteria(cities: Int, population: Int): Boolean = cities>=1
  }

  object Level2 extends NationLevel {
    override def name: String = "Sparkle of hope"

    override protected def getHigherLevel: Option[NationLevel] = Some(Level3)
    override protected def getLowerLevel: Option[NationLevel] = Some(Level1)

    override protected def isMatchingCriteria(cities: Int, population: Int): Boolean = cities>= 2 && population>=100
  }

  object Level3 extends NationLevel {
    override def name: String = "Tiny nation"

    override protected def getHigherLevel: Option[NationLevel] = None
    override protected def getLowerLevel: Option[NationLevel] = Some(Level2)

    override protected def isMatchingCriteria(cities: Int, population: Int): Boolean = cities >= 3 && population >=200
  }


}
