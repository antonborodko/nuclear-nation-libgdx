package com.anton.nuclearnation

import com.badlogic.gdx.{Gdx, Input, InputProcessor, Screen}
import com.badlogic.gdx.graphics.{Color, GL20, OrthographicCamera}
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.{Button, Label, Skin, TextButton}
import com.badlogic.gdx.utils.viewport.StretchViewport

class UnitConstructionScreen(game:NuclearNation,mapScreen: MapScreen) extends Screen{


  val stage = new Stage(new StretchViewport(1600,960,new OrthographicCamera()))
  val camera = stage.getCamera.asInstanceOf[OrthographicCamera]

  val assetManager = game.assetManager

  val skin = assetManager.get("data/commodore64/skin/uiskin.json",classOf[Skin])


  val constructionScreenInputProcessor = new InputProcessor() {

    override def keyUp(keycode: Int): Boolean = {
      if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.T) {
        game.setScreen(mapScreen)
        return true
      }
      false
    }

    override def keyDown(keycode: Int): Boolean = {return true}

    override def keyTyped(character: Char): Boolean = {return true}

    override def touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = {return true}

    override def touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = {return true}

    override def touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = {return true}

    override def mouseMoved(screenX: Int, screenY: Int): Boolean = {return true}

    override def scrolled(amount: Int): Boolean = {return true}
  }

  Gdx.input.setInputProcessor(constructionScreenInputProcessor)

  override def show(): Unit = {
    val titleLabel = new Label("Unit construction",skin)
    val commandoUnitButton = new TextButton("Commando",skin)
    commandoUnitButton.setDisabled(mapScreen.technologies.count(t => !t.enabled)>0)
    titleLabel.setPosition(stage.getViewport.getScreenWidth/2,stage.getViewport.getScreenHeight-20)
    commandoUnitButton.setPosition(stage.getViewport.getScreenWidth/2,stage.getViewport.getScreenHeight-100)
    stage.addActor(titleLabel)
    stage.addActor(commandoUnitButton)

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
