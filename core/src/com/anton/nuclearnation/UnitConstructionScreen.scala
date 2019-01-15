package com.anton.nuclearnation

import com.badlogic.gdx.{Gdx, Screen}
import com.badlogic.gdx.graphics.{Color, GL20, OrthographicCamera}
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.{Label, Skin}
import com.badlogic.gdx.utils.viewport.StretchViewport

class UnitConstructionScreen(game:NuclearNation,mapScreen: MapScreen) extends Screen{


  val stage = new Stage(new StretchViewport(1600,960,new OrthographicCamera()))
  val camera = stage.getCamera.asInstanceOf[OrthographicCamera]

  val assetManager = game.assetManager

  val skin = assetManager.get("data/commodore64/skin/uiskin.json",classOf[Skin])

  override def show(): Unit = {
    val titleLabel = new Label("Unit construction",skin)
    titleLabel.setPosition(stage.getViewport.getScreenWidth/2,stage.getViewport.getScreenHeight)
    stage.addActor(titleLabel)

  }

  override def render(delta: Float): Unit = {
    Gdx.gl.glClearColor(0, 0, 0, 0)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT)

    stage.act(delta)
    stage.draw()
  }

  override def resize(width: Int, height: Int): Unit = {

  }

  override def pause(): Unit = {

  }

  override def resume(): Unit = {

  }

  override def hide(): Unit = {

  }

  override def dispose(): Unit = {

  }
}
