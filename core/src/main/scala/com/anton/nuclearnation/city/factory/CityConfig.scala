package com.anton.nuclearnation.city.factory

import com.anton.nuclearnation.ControlledBy
import com.anton.nuclearnation.ControlledBy.ControlledBy

case class CityConfig(name:Option[String] = None,val popFrom:Int = 0, val popTo:Int = 0, val ownedBy:ControlledBy = ControlledBy.COMPUTER)

