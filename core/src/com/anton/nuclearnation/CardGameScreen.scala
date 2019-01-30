package com.anton.nuclearnation

import com.anton.nuclearnation.MapScreen.MapLocation
import com.anton.nuclearnation.UnitType.UnitType
import com.badlogic.gdx.Input.{Buttons, Keys}
import com.badlogic.gdx.graphics.{GL20, OrthographicCamera, Texture}
import com.badlogic.gdx.{Gdx, InputMultiplexer, InputProcessor, Screen}
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.scenes.scene2d.{Actor, Group, InputEvent, Stage}
import com.badlogic.gdx.scenes.scene2d.ui.{Container, HorizontalGroup, Image, Label, Skin, Table, TextButton}
import com.badlogic.gdx.scenes.scene2d.utils.{ClickListener, TextureRegionDrawable}
import com.badlogic.gdx.utils.viewport.StretchViewport

import scala.collection.mutable.ListBuffer

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


  val rubbleImage = new Image(assetManager.get("cardGameScreen/rubble.png",classOf[Texture]))

  val unitMix=ListBuffer[UnitType]()

  val rootTable = new Table()
  rootTable.setFillParent(true)

  rootTable.setDebug(true)

  val playerUnitsTable = new Table().top()
  val opposingTable = new Table().top()

  opposingTable.setDebug(true)
  playerUnitsTable.setDebug(true)

  val mixGroup = new HorizontalGroup

  opposingTable.add(rubbleImage)
  opposingTable.row()
  descriptionLabel.setWrap(true)
  opposingTable.add(descriptionLabel).fillX.pad(50,0,0,0)

  playerUnitsTable.add(new Label("Available assets:",skin))
  playerUnitsTable.row()
  val horizontalGroup = new HorizontalGroup()
  horizontalGroup.addActor(engineerUnit)
  horizontalGroup.addActor(soldierUnit)
  horizontalGroup.addActor(scientistUnit)

  val resultLabel = new Label("Result: ???",skin)


  addUnitLeftClickListener(scientistUnit,()=>true,mixGroup,()=>{})
  addUnitLeftClickListener(soldierUnit,()=>true,mixGroup,()=>{})
  addUnitLeftClickListener(engineerUnit,()=>true,mixGroup,()=>{})

  soldierUnit.setUserObject(UnitType.SOLDIER)
  scientistUnit.setUserObject(UnitType.SCIENTIST)
  engineerUnit.setUserObject(UnitType.ENGINEER)

  playerUnitsTable.add(horizontalGroup)
  playerUnitsTable.row()
  playerUnitsTable.add(new Label("Choose your mix:",skin))
  playerUnitsTable.row()

  playerUnitsTable.add(mixGroup).prefHeight(soldierUnit.getPrefHeight)
  playerUnitsTable.row()
  playerUnitsTable.add(resultLabel)


  rootTable.add(playerUnitsTable)
  rootTable.add(opposingTable)

  stage.addActor(rootTable)

  private def addUnitLeftClickListener(sourceActor:Actor,condition:()=>Boolean, mixGroup:Group,callback:() => Unit): Unit = {

    sourceActor.addCaptureListener(new ClickListener(Buttons.LEFT) {
      override def clicked(event: InputEvent, x: Float, y: Float): Unit = {
        val actor = new Image(sourceActor.asInstanceOf[Image].getDrawable.asInstanceOf[TextureRegionDrawable].getRegion.getTexture)
        mixGroup.addActor(actor)
        val userObj = Option(actor.getUserObject)
        userObj match {
          case Some(t) if t.isInstanceOf[UnitType] => unitMix += t.asInstanceOf[UnitType]
          case _ =>
        }
      }
    })
  }


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
