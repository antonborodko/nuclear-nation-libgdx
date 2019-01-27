package com.anton.nuclearnation

import com.anton.nuclearnation.MapScreen.ExpeditionInfo
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
  val commandoPicture = new Image(assetManager.get("unitConstruction/commandoUnit.png",classOf[Texture]))
  val spyPicture = new Image(assetManager.get("unitConstruction/spyUnit.png",classOf[Texture]))
  val gameFont = assetManager.get("fonts/lunchtime-doubly-so/lunchds.ttf",classOf[BitmapFont])

  val soldierDescriptionLabel = new Label("A basic soldier. Good for performing day to day tasks that don't require much intelligence",skin)
  val soldierCountLabel = new Label(game.soldierCounter.toString,skin)
  soldierDescriptionLabel.setWrap(true)
  soldierDescriptionLabel.setWidth(400)

  val commandoDescriptionLabel = new Label("Everything a soldier can do, commandos can do better",skin)
  val commandoCountLabel = new Label(game.commandoCounter.toString,skin)
  commandoDescriptionLabel.setWrap(true)
  commandoDescriptionLabel.setWidth(400)

  val spyDescriptionLabel = new Label("Spies can gather information and if lucky influence other cities",skin)
  val spyCountLabel = new Label(game.spyCounter.toString,skin)
  spyDescriptionLabel.setWrap(true)
  spyDescriptionLabel.setWidth(400)

  updateCountLabel(game.soldierCounter,soldierCountLabel)
  updateCountLabel(game.commandoCounter,commandoCountLabel)
  updateCountLabel(game.spyCounter,spyCountLabel)

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
    val commandoUnitButton = new TextButton("Commando",skin)
    val spyUnitButton= new TextButton("Spy",skin)



    val buildSoldierListener = new ClickListener(){
      override def clicked (event:InputEvent, x:Float, y:Float):Unit= {
        game.soldierCounter +=1
        updateCountLabel(game.soldierCounter,soldierCountLabel)
      }
    }

    val buildCommandoListener = new ClickListener(){
      override def clicked (event:InputEvent, x:Float, y:Float):Unit= {
        game.commandoCounter +=1
        updateCountLabel(game.commandoCounter,commandoCountLabel)
      }
    }

    val cantBuildCommandoListener = new ClickListener(){
      override def clicked (event:InputEvent, x:Float, y:Float):Unit= {
        showRequirementsDialog("2 or more conquered cities")
      }
    }

    val buildSpyListener = new ClickListener(){
      override def clicked (event:InputEvent, x:Float, y:Float):Unit= {
        game.spyCounter +=1
        updateCountLabel(game.spyCounter,spyCountLabel)
      }
    }

    val cantBuildSpyListener = new ClickListener(){
      override def clicked (event:InputEvent, x:Float, y:Float):Unit= {
        showRequirementsDialog("3 or more conquered cities")
      }
    }


    soldierUnitButton.addCaptureListener(buildSoldierListener)
    soldierPicture.addCaptureListener(buildSoldierListener)


    if (assetChain.isCommandoEnabled){
      commandoUnitButton.addCaptureListener(buildCommandoListener)
      commandoPicture.addCaptureListener(buildCommandoListener)
    } else {
      commandoUnitButton.addCaptureListener(cantBuildCommandoListener)
      commandoPicture.addCaptureListener(cantBuildCommandoListener)
    }


    if (assetChain.isSpyEnabled){
      spyUnitButton.addCaptureListener(buildSpyListener)
      spyPicture.addCaptureListener(buildSpyListener)
    } else {
      spyUnitButton.addCaptureListener(cantBuildSpyListener)
      spyPicture.addCaptureListener(cantBuildSpyListener)
    }





    stage.addActor(titleLabel)
    stage.addActor(soldierUnitButton)
    stage.addActor(commandoUnitButton)
    stage.addActor(spyUnitButton)

    stage.addActor(soldierPicture)
    stage.addActor(commandoPicture)
    stage.addActor(spyPicture)

    stage.addActor(soldierDescriptionLabel)
    stage.addActor(commandoDescriptionLabel)
    stage.addActor(spyDescriptionLabel)

    stage.addActor(soldierCountLabel)
    stage.addActor(commandoCountLabel)
    stage.addActor(spyCountLabel)

    stage.addActor(OKButton)

    titleLabel.setPosition(stage.getViewport.getScreenWidth/2,camera.unproject(new Vector3(0,0,0)).y-titleLabel.getPrefHeight)

    soldierPicture.setPosition(titleLabel.getX,titleLabel.getY - soldierPicture.getPrefHeight - 120)
    soldierDescriptionLabel.setPosition(soldierPicture.getX + soldierPicture.getPrefWidth +30,soldierPicture.getY + soldierDescriptionLabel.getPrefHeight /2)
    soldierUnitButton.setPosition(soldierPicture.getX,soldierPicture.getY - soldierUnitButton.getPrefHeight - 10)
    soldierCountLabel.setPosition(soldierUnitButton.getX() + soldierUnitButton.getPrefWidth + 10, soldierUnitButton.getY)

    commandoPicture.setPosition(soldierUnitButton.getX,soldierUnitButton.getY - commandoPicture.getPrefHeight - 50)
    commandoDescriptionLabel.setPosition(commandoPicture.getX + commandoPicture.getPrefWidth +30,commandoPicture.getY + commandoDescriptionLabel.getPrefHeight /2)
    commandoUnitButton.setPosition(commandoPicture.getX,commandoPicture.getY - commandoUnitButton.getPrefHeight - 10)
    commandoCountLabel.setPosition(commandoUnitButton.getX() + commandoUnitButton.getPrefWidth + 10, commandoUnitButton.getY)

    spyPicture.setPosition(commandoUnitButton.getX,commandoUnitButton.getY - spyPicture.getPrefHeight - 50)
    spyDescriptionLabel.setPosition(spyPicture.getX + spyPicture.getPrefWidth +30,spyPicture.getY + spyDescriptionLabel.getPrefHeight /2)
    spyUnitButton.setPosition(spyPicture.getX,spyPicture.getY - spyUnitButton.getPrefHeight - 10)
    spyCountLabel.setPosition(spyUnitButton.getX() + spyUnitButton.getPrefWidth + 10, spyUnitButton.getY)

    OKButton.setPosition(spyUnitButton.getX,0)

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
