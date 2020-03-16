package com.anton.nuclearnation.city.factory

import com.anton.nuclearnation.{ControlledBy, MapCellData, MapCoordsGenerator}
import com.anton.nuclearnation.MapScreen.City
import com.anton.nuclearnation.global.Global

import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.util.Random

object CityFactory {

  protected val coordsGenerator = new MapCoordsGenerator(5)

  protected val cityNames: ListBuffer[String] = ListBuffer(
    "The Nether",
    "Everwinter",
    "Seclusion",
    "Perile",
    "Direfall",
    "Concrete Jungle",
    "Malaise",
    "Foolshope",
    "Crishire",
    "Vacancy",
    "Cruelfeld",
    "Dawnford",
    "Murkville",
    "Deadline",
    "Lost Angeles",
    "Wrathford",
    "Victorville",
    "Witherbury",
    "Emitton",
    "Blightown",
    "Wreckville",
    "Snowmelt",
    "Nefaria",
    "Mensfield",
    "The Verdicts",
    "The Boons",
    "Wickhills",
    "Blackridge",
    "Elysium",
    "Grieford"
  )
}

trait CityFactory{



  def getCity : City

  protected val random = new Random()

  protected def getName :String = {
    val name = CityFactory.cityNames(random.nextInt(CityFactory.cityNames.length))
    CityFactory.cityNames -= name
    println(CityFactory.cityNames.length)
    name
  }
  protected def getPop(from:Int,to:Int):Int  = {
    from + random.nextInt((to - from) + 1)
  }

  protected def getCell:MapCellData = {
    val coords = CityFactory.coordsGenerator.getCoords
    Global.mapData.getCell(coords._1,coords._2).get
  }
}



