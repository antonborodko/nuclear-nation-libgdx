package com.anton.nuclearnation

import com.anton.nuclearnation.UnitType.UnitType
import com.badlogic.gdx.graphics.{Color, Colors, Texture}
import com.badlogic.gdx.graphics.g2d.{Batch, TextureRegion}
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.{Vector2, Vector3}
import com.badlogic.gdx.scenes.scene2d.ui._
import com.badlogic.gdx.scenes.scene2d.{Actor, Group}

class AssetCard(image:Image,val name:String,var count:Int,game:NuclearNation,userObject: Option[Object]) extends Table{

  val renderer = new ShapeRenderer
  val assetManager = game.assetManager
  val skin = assetManager.get("data/commodore64/skin/uiskin.json",classOf[Skin])

  val countLabel = new Label(count.toString,skin)

  val stack = new Stack()

  stack.add(image)

  //Second add wrapped overlay object
  val overlay = new Table()
  overlay.add(countLabel).expand().bottom().right()
  stack.add(overlay)

  setHeight(stack.getPrefHeight)
  setWidth(stack.getPrefWidth)
  add(stack).expandX()
  row()
  add(new Label(name,skin)).fillX()


  if (userObject.isDefined){
    setUserObject(userObject.get)
  }


  def updateCount(delta:Int): Unit ={
    count +=delta
    if (count <0) count = 0
    countLabel.setText(count.toString)
  }

}
