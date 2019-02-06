package com.anton.nuclearnation

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable

class ExpeditionActor(game:NuclearNation,texture:Option[Texture] = None) extends Image(texture.getOrElse(game.assetManager.get("tradeCaravan.png",classOf[Texture]))){
}
