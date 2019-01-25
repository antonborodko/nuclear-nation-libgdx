package com.anton.nuclearnation

import com.anton.nuclearnation.MapScreen.CityInfo

class AssetChain(mapScreen:MapScreen) {
  def isCommandoEnabled = {
    getCitiesOwnedByPlayer.size > 1
  }

  def isSpyEnabled = {
    getCitiesOwnedByPlayer.size > 2
  }

  private def getCitiesOwnedByPlayer ={
    mapScreen.locations.filter(l=>l.isInstanceOf[CityInfo] && l.asInstanceOf[CityInfo].isOwnedByPlayer)
  }
}
