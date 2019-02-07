package com.anton.nuclearnation

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Image

class SoldierAssetCard(count:Int,game:NuclearNation,userObject: Option[Object]) extends AssetCard(
  image = new Image(game.assetManager.get("unitConstruction/soldierUnit.png",classOf[Texture])),
  "Soldier",
  count,
  game,
  userObject: Option[Object]){
}
