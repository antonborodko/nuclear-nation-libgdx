package com.anton.nuclearnation

import com.anton.nuclearnation.MapScreen.{CityInfo, MapLocation, RuinsInfo}
import com.anton.nuclearnation.UnitType.UnitType
import com.badlogic.gdx.Input.{Buttons, Keys}
import com.badlogic.gdx.graphics.{GL20, OrthographicCamera, Texture}
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.scenes.scene2d.ui._
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.{Group, InputEvent, Stage}
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.badlogic.gdx._

import scala.util.Random

class CityScreen(city:CityInfo, game:NuclearNation, mapScreen:MapScreen) extends Screen{

  val assetManager = game.assetManager
  val map = new TiledMap
  val layers = map.getLayers

  val stage = new Stage(new StretchViewport(800,600,new OrthographicCamera()))
  val camera = stage.getCamera

  val cityPicture = new Image(assetManager.get("city/cityPicture.png",classOf[Texture]))

  val rootTable = new Table()
  rootTable.setFillParent(true)


  val stack = new Stack
  stack.add(cityPicture)
  val overlay = new Table()
  overlay.add(new Label(city.name,game.skin)).expand().bottom().right()
  stack.add(overlay)
  rootTable.add(stack)

  stage.addActor(rootTable)

  val inputProcessor = new InputProcessor {
    override def keyDown(keycode: Int): Boolean = {true}

    override def keyUp(keycode: Int): Boolean = {
      if (keycode == Input.Keys.ESCAPE) {
        game.setScreen(mapScreen)
        return true
      }
      false
    }

    override def keyTyped(character: Char): Boolean = {true}


    override def touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = {
     true
    }

    override def touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = {true}

    override def mouseMoved(screenX: Int, screenY: Int): Boolean = {true}

    override def scrolled(amount: Int): Boolean = {true}

    override def touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = {
      game.setScreen(mapScreen)
      true
    }
  }

  override def show(): Unit = {
    val multiplexer = new InputMultiplexer()
    multiplexer.addProcessor(stage)
    multiplexer.addProcessor(inputProcessor)

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
