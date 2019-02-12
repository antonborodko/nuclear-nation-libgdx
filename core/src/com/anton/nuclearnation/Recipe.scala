package com.anton.nuclearnation

import java.util.Optional

import com.anton.nuclearnation.MapScreen.MapLocation

case class Recipe(name:String,
                  description:String,
                  basicPrecondition:()=>Boolean,
                  locationCondition:Option[(MapLocation)=>Boolean],
                  doCrafting:()=>Unit,
                  getCount:()=>Int){
  def enabled():Boolean = {
    basicPrecondition()
  }
}