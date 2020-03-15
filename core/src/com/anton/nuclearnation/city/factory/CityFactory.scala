package com.anton.nuclearnation.city.factory

import com.anton.nuclearnation.{ControlledBy, MapCellData, MapCoordsGenerator}
import com.anton.nuclearnation.MapScreen.City
import com.anton.nuclearnation.global.Global

import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.util.Random

trait CityFactory{
  val cityNames: ListBuffer[String] = ListBuffer(
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
    "Blackridge",
    "Wreckville",
    "Snowmelt",
    "Nefaria",
    "Mensfield",
    "The Verdicts",
    "The Boons",
    "Wickhills",
    "Blackridge",
    "Elysium"
  )
  val coordsGenerator = new MapCoordsGenerator(3)

  def getCity() : City

  val random = new Random()
  def getName :String = {
    val name = cityNames.toList(random.nextInt(cityNames.length))
    cityNames -= name
    name
  }
  def getPop(from:Int,to:Int):Int  = {
    from + random.nextInt((to - from) + 1)
  }

  def getCell:MapCellData = {
    val coords = coordsGenerator.getCoords
    Global.mapData.getCell(coords._1,coords._2).get
  }
}



