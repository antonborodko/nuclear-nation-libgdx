package com.anton.nuclearnation

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Image

class ScientistAssetCard(count:Int,game:NuclearNation,userObject: Option[Object]) extends AssetCard(
  image = new Image(game.assetManager.get("unitConstruction/scientistUnit.png",classOf[Texture])),
  "Scientist",
  count,
  game,
  userObject: Option[Object]) {
}