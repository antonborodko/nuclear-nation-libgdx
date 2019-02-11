package com.anton.nuclearnation

import com.anton.nuclearnation.MapScreen.MapLocation

case class Recipe(name:String, description:String,basicPrecondition:()=>Boolean, doCrafting:()=>Unit,getCount:()=>Int){
  def enabled():Boolean = {
    basicPrecondition()
  }
}