package com.anton.nuclearnation

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx._
import com.badlogic.gdx.graphics.{GL20, OrthographicCamera, Texture}
import com.badlogic.gdx.scenes.scene2d.{Group, InputEvent, Stage}
import com.badlogic.gdx.scenes.scene2d.ui._
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.StretchViewport

class ActionMixScreen(game:NuclearNation,mapScreen: MapScreen) extends Screen{

  val stage = new Stage(new StretchViewport(1600,960,new OrthographicCamera()))
  val camera = stage.getCamera.asInstanceOf[OrthographicCamera]

  val assetManager = game.assetManager

  val skin = assetManager.get("data/commodore64/skin/uiskin.json",classOf[Skin])

  val subjectPicture = new Image(assetManager.get("raider_camp.png",classOf[Texture]))
  val meansPicture = new Image(assetManager.get("raider-facing-left.png",classOf[Texture]))
  val crossedSwordsPicture = new Image(assetManager.get("crossed-swords.png",classOf[Texture]))

  val actionMixScreen = new InputProcessor() {

    override def keyUp(keycode: Int): Boolean = {
      if (keycode == Input.Keys.ESCAPE) {
        game.setScreen(mapScreen)
        dispose()
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

  val multiplexer = new InputMultiplexer()
  multiplexer.addProcessor(stage)
  multiplexer.addProcessor(actionMixScreen)
  Gdx.input.setInputProcessor(multiplexer)


  override def show(): Unit = {
    val titleLabel = new Label("Create action mix",skin)
    val subjectLabel = new Label("Subject",skin)
    val meansLabel = new Label("Means",skin)
    val resultLabel = new Label("Result", skin)

    val applyButton = new TextButton("Apply mix",skin)

    applyButton.addCaptureListener(new ClickListener() {
      override def clicked(event: InputEvent, x: Float, y: Float): Unit = {
        game.setScreen(mapScreen)
        dispose()
      }
    })

    val controlGroup = new Group()

    controlGroup.addActor(titleLabel)
    controlGroup.addActor(subjectLabel)
    controlGroup.addActor(subjectPicture)
    controlGroup.addActor(meansLabel)
    controlGroup.addActor(meansPicture)
    controlGroup.addActor(resultLabel)
    controlGroup.addActor(crossedSwordsPicture)
    controlGroup.addActor(applyButton)


    titleLabel.setPosition(controlGroup.getWidth/2,controlGroup.getHeight- titleLabel.getHeight)
    subjectLabel.setPosition(controlGroup.getWidth/2,controlGroup.getHeight- titleLabel.getHeight - subjectLabel.getHeight-10)
    subjectPicture.setPosition(subjectLabel.getX,subjectLabel.getY-subjectPicture.getHeight-10)
    meansLabel.setPosition(subjectPicture.getX(),subjectPicture.getY() - meansLabel.getHeight - 10)
    meansPicture.setPosition(meansLabel.getX,meansLabel.getY - meansPicture.getHeight - 10)
    resultLabel.setPosition(meansPicture.getX(),meansPicture.getY - resultLabel.getHeight - 10)
    crossedSwordsPicture.setPosition(resultLabel.getX,resultLabel.getY - crossedSwordsPicture.getHeight - 10)
    applyButton.setPosition(crossedSwordsPicture.getX,crossedSwordsPicture.getY - applyButton.getHeight - 10)

    stage.addActor(controlGroup)

    controlGroup.setPosition(stage.getWidth /2 - titleLabel.getWidth / 2, stage.getHeight/2 + 200)

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
