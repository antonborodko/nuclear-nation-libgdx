package com.anton.nuclearnation

import com.anton.nuclearnation.MapScreen.MapLocation
import com.anton.nuclearnation.UnitType.UnitType
import com.badlogic.gdx.graphics.{GL20, OrthographicCamera, Texture}
import com.badlogic.gdx.{Gdx, InputMultiplexer, InputProcessor, Screen}
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.scenes.scene2d.{Group, Stage}
import com.badlogic.gdx.scenes.scene2d.ui.{Image, Label, Skin, TextButton}
import com.badlogic.gdx.utils.viewport.StretchViewport

class CardGameScreen(currentLocation:MapLocation, game:NuclearNation, mapScreen:MapScreen, playerUnits:List[UnitType]) extends Screen{

  val assetManager = game.assetManager
  val map = new TiledMap
  val layers = map.getLayers

  val stage = new Stage(new StretchViewport(800,600,new OrthographicCamera()))
  val camera = stage.getCamera

  val soldierUnit = new Image(assetManager.get("unitConstruction/soldierUnit.png",classOf[Texture]))
  val scientistUnit = new Image(assetManager.get("unitConstruction/scientistUnit.png",classOf[Texture]))
  val engineerUnit = new Image(assetManager.get("unitConstruction/engineerUnit.png",classOf[Texture]))

  val skin = assetManager.get("data/commodore64/skin/uiskin.json",classOf[Skin])

  val startButton = new TextButton("Start",skin)
  val descriptionLabel = new Label("You have arrived to the ancient ruins. You see the entrance blocked with a pile of rubble.",skin)


  val group = new Group()

  group.addActor(startButton)
  group.addActor(descriptionLabel)
  stage.addActor(group)

  group.setPosition(200,50)
  group.setSize(400,400)

  descriptionLabel.setWidth(descriptionLabel.getParent.getWidth)
  descriptionLabel.setWrap(true)

  startButton.setPosition(0,0)
  descriptionLabel.setPosition(0,group.getHeight - startButton.getPrefHeight)

  override def show(): Unit = {
    val tacticalScreenInputProcessor = new InputProcessor {
      override def keyDown(keycode: Int): Boolean = {true}

      override def keyUp(keycode: Int): Boolean = {true}

      override def keyTyped(character: Char): Boolean = {true}


      override def touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = {true}

      override def touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = {true}

      override def mouseMoved(screenX: Int, screenY: Int): Boolean = {true}

      override def scrolled(amount: Int): Boolean = {true}

      override def touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = {return false}
    }


    val multiplexer = new InputMultiplexer()
    multiplexer.addProcessor(stage)
    multiplexer.addProcessor(tacticalScreenInputProcessor)

    Gdx.input.setInputProcessor(multiplexer)
  }

  override def render(delta: Float): Unit = {
    Gdx.gl.glClearColor(0, 0, 0, 0)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT)

    stage.act(delta)
    stage.draw()
  }

  override def resize(width: Int, height: Int): Unit = {}

  override def pause(): Unit = {}

  override def resume(): Unit = {}

  override def hide(): Unit = {}

  override def dispose(): Unit = {}
}
