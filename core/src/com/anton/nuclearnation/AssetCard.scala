package com.anton.nuclearnation

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.{Batch, TextureRegion}
import com.badlogic.gdx.scenes.scene2d.ui._
import com.badlogic.gdx.scenes.scene2d.{Actor, Group}

class AssetCard(image:Image,count:Int,game:NuclearNation) extends Table{

  val assetManager = game.assetManager
  val skin = assetManager.get("data/commodore64/skin/uiskin.json",classOf[Skin])

  val countLabel = new Label(count.toString,skin)

  val stack = new Stack()

  stack.add(image)

  //Second add wrapped overlay object
  val overlay = new Table()
  overlay.add(countLabel).expand().bottom().right()
  stack.add(overlay)

  setDebug(true)
  setHeight(stack.getPrefHeight)
  setWidth(stack.getPrefWidth)
  add(stack)




}
