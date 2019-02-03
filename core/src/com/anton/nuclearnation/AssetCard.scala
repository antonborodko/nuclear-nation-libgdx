package com.anton.nuclearnation

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.{Batch, TextureRegion}
import com.badlogic.gdx.scenes.scene2d.ui.{Image, Label, Skin, Table}
import com.badlogic.gdx.scenes.scene2d.{Actor, Group}

class AssetCard(texture:Texture,count:Int,game:NuclearNation) extends Group{


  val table = new Table

  val assetManager = game.assetManager
  val skin = assetManager.get("data/commodore64/skin/uiskin.json",classOf[Skin])

  val countLabel = new Label(count.toString,skin)

  table.add(new Image(texture)).center()
  table.row()
  table.add(countLabel).right().bottom()

  addActor(table)

}
