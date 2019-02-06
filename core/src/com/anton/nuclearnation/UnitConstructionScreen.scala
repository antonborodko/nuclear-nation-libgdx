package com.anton.nuclearnation

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx._
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.{Color, GL20, OrthographicCamera, Texture}
import com.badlogic.gdx.math.{Vector2, Vector3}
import com.badlogic.gdx.scenes.scene2d.{InputEvent, Stage}
import com.badlogic.gdx.scenes.scene2d.ui._
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.StretchViewport

class UnitConstructionScreen(game:NuclearNation,mapScreen: MapScreen) extends Screen{


  val stage = new Stage(new StretchViewport(1600,960,new OrthographicCamera()))
  val camera = stage.getCamera.asInstanceOf[OrthographicCamera]

  val assetManager = game.assetManager

  val skin = assetManager.get("data/commodore64/skin/uiskin.json",classOf[Skin])

  val soldierPicture = new Image(assetManager.get("unitConstruction/soldierUnit.png",classOf[Texture]))
  val scientistPicture = new Image(assetManager.get("unitConstruction/scientistUnit.png",classOf[Texture]))
  val engineerPicture = new Image(assetManager.get("unitConstruction/engineerUnit.png",classOf[Texture]))

  val gameFont = assetManager.get("fonts/lunchtime-doubly-so/lunchds.ttf",classOf[BitmapFont])

  val soldierDescriptionLabel = new Label("A basic soldier. Good for performing day to day tasks that don't require much intelligence",skin)
  val soldierCountLabel = new Label(game.soldierCounter.toString,skin)
  soldierDescriptionLabel.setWrap(true)
  soldierDescriptionLabel.setWidth(400)

  val scientistDescriptionLabel = new Label("Scientists try to learn about the old world",skin)
  val scientistCountLabel = new Label(game.scientistCounter.toString,skin)
  scientistDescriptionLabel.setWrap(true)
  scientistDescriptionLabel.setWidth(400)

  val engineerDescriptionLabel = new Label("Engineers are good in making and breaking things",skin)
  val engineerCountLabel = new Label(game.engineerCounter.toString,skin)
  engineerDescriptionLabel.setWrap(true)
  engineerDescriptionLabel.setWidth(400)

  updateCountLabel(game.soldierCounter,soldierCountLabel)
  updateCountLabel(game.scientistCounter,scientistCountLabel)
  updateCountLabel(game.engineerCounter,engineerCountLabel)

  val assetChain = new AssetChain(mapScreen)

  val OKButton = new TextButton("OK",skin)

  OKButton.addCaptureListener(new ClickListener() {
    override def clicked(event: InputEvent, x: Float, y: Float): Unit = {
      game.setScreen(mapScreen)
    }
  })

  val constructionScreenInputProcessor = new InputProcessor() {

    override def keyUp(keycode: Int): Boolean = {
      if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.U) {
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

  val multiplexer = new InputMultiplexer()
  multiplexer.addProcessor(stage)
  multiplexer.addProcessor(constructionScreenInputProcessor)
  Gdx.input.setInputProcessor(multiplexer)

  private def showRequirementsDialog(requirement:String) {
    val dialog = new Dialog("Requirements not met", skin) {
      override def result(result:Object) {

      }
    }

    dialog.text(s"Requirements not met: " + requirement)
    dialog.button("OK", true)
    dialog.key(Keys.ESCAPE, false).key(Keys.ENTER, true)
    dialog.getContentTable.pad(20)
    dialog.getTitleTable.pad(20)
    dialog.pack()
    stage.addActor(dialog)
    dialog.setPosition(100,100)
  }

  override def show(): Unit = {
    val titleLabel = new Label("Unit construction",skin)

    val soldierUnitButton = new TextButton("Soldier",skin)
    val scientistUnitButton = new TextButton("Scientist",skin)
    val engineerUnitButton = new TextButton("Engineer",skin)

    val buildSoldierListener = new ClickListener(){
      override def clicked (event:InputEvent, x:Float, y:Float):Unit= {
        game.soldierCounter +=1
        updateCountLabel(game.soldierCounter,soldierCountLabel)
      }
    }

    val buildScientistListener = new ClickListener(){
      override def clicked (event:InputEvent, x:Float, y:Float):Unit= {
        game.scientistCounter +=1
        updateCountLabel(game.scientistCounter,scientistCountLabel)
      }
    }

    val buildEngineerListener = new ClickListener(){
      override def clicked (event:InputEvent, x:Float, y:Float):Unit= {
        game.engineerCounter +=1
        updateCountLabel(game.engineerCounter,engineerCountLabel)
      }
    }



    soldierUnitButton.addCaptureListener(buildSoldierListener)
    soldierPicture.addCaptureListener(buildSoldierListener)

    scientistUnitButton.addCaptureListener(buildScientistListener)
    scientistPicture.addCaptureListener(buildScientistListener)

    engineerUnitButton.addCaptureListener(buildEngineerListener)
    engineerPicture.addCaptureListener(buildEngineerListener)

    stage.addActor(titleLabel)
    stage.addActor(soldierUnitButton)
    stage.addActor(soldierPicture)
    stage.addActor(soldierDescriptionLabel)

    stage.addActor(scientistPicture)
    stage.addActor(scientistDescriptionLabel)
    stage.addActor(scientistUnitButton)

    stage.addActor(engineerPicture)
    stage.addActor(engineerDescriptionLabel)
    stage.addActor(engineerUnitButton)

    stage.addActor(soldierCountLabel)
    stage.addActor(scientistCountLabel)
    stage.addActor(engineerCountLabel)

    stage.addActor(OKButton)

    titleLabel.setPosition(stage.getViewport.getScreenWidth/2,camera.unproject(new Vector3(0,0,0)).y-titleLabel.getPrefHeight)

    soldierPicture.setPosition(titleLabel.getX,titleLabel.getY - soldierPicture.getPrefHeight - 120)
    soldierDescriptionLabel.setPosition(soldierPicture.getX + soldierPicture.getPrefWidth +30,soldierPicture.getY + soldierDescriptionLabel.getPrefHeight /2)
    soldierUnitButton.setPosition(soldierPicture.getX,soldierPicture.getY - soldierUnitButton.getPrefHeight - 10)
    soldierCountLabel.setPosition(soldierUnitButton.getX() + soldierUnitButton.getPrefWidth + 10, soldierUnitButton.getY)

    scientistPicture.setPosition(soldierPicture.getX,soldierCountLabel.getY - scientistPicture.getPrefHeight - 50)
    scientistDescriptionLabel.setPosition(scientistPicture.getX + scientistPicture.getPrefWidth +30,scientistPicture.getY + scientistDescriptionLabel.getPrefHeight /2)
    scientistUnitButton.setPosition(scientistPicture.getX,scientistPicture.getY - scientistUnitButton.getPrefHeight - 10)
    scientistCountLabel.setPosition(scientistUnitButton.getX() + scientistUnitButton.getPrefWidth + 10, scientistUnitButton.getY)

    engineerPicture.setPosition(scientistPicture.getX,scientistCountLabel.getY - engineerPicture.getPrefHeight - 50)
    engineerDescriptionLabel.setPosition(engineerPicture.getX + engineerPicture.getPrefWidth +30,engineerPicture.getY + engineerDescriptionLabel.getPrefHeight /2)
    engineerUnitButton.setPosition(engineerPicture.getX,engineerPicture.getY - engineerUnitButton.getPrefHeight - 10)
    engineerCountLabel.setPosition(engineerUnitButton.getX() + engineerUnitButton.getPrefWidth + 10, engineerUnitButton.getY)


    OKButton.setPosition(engineerUnitButton.getX,engineerUnitButton.getY - OKButton.getPrefHeight - 30)

  }

  def updateCountLabel(count:Int,label:Label): Unit ={
    label.setText(s"(${count.toString})")
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
    dispose()
  }

  override def dispose(): Unit = {

  }
}
