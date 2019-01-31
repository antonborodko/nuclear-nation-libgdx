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
  val applySolutionButton = new TextButton("Apply solution",skin)
  val descriptionLabel = new Label("You have arrived to the ancient ruins. You see the entrance blocked with a pile of rubble.",skin)


  val rubbleImage = new Image(assetManager.get("cardGameScreen/rubble.png",classOf[Texture]))

  val unitMix=ListBuffer[UnitType]()

  val rootTable = new Table()
  rootTable.setFillParent(true)


  val playerUnitsTable = new Table().top()
  val opposingTable = new Table().top()
//
//  rootTable.setDebug(true)
//  opposingTable.setDebug(true)
//  playerUnitsTable.setDebug(true)

  val solutionMix = new HorizontalGroup
  solutionMix.wrap

  opposingTable.add(rubbleImage)
  opposingTable.row()
  descriptionLabel.setWrap(true)
  opposingTable.add(descriptionLabel).fillX.pad(50,0,0,0)

  playerUnitsTable.add(new Label("Available assets:",skin))
  playerUnitsTable.row()
  val availableMix = new HorizontalGroup()
  availableMix.addActor(engineerUnit)
  availableMix.addActor(soldierUnit)
  availableMix.addActor(scientistUnit)


  val resultTitleLabel = new Label("Result:",skin)
  val resultLabel = new Label("???",skin)
  resultLabel.setWrap(true)

  val collapsedEntrance = Subject(rubbleImage,reactsWith = ReactsWith(UnitType.ENGINEER,10),90,"Clear the entrance")


  addUnitLeftClickListener(scientistUnit,()=>true,solutionMix, ()=>{},resultLabel,collapsedEntrance)
  addUnitLeftClickListener(soldierUnit,()=>true,solutionMix, ()=>{},resultLabel,collapsedEntrance)
  addUnitLeftClickListener(engineerUnit,()=>true,solutionMix, ()=>{},resultLabel,collapsedEntrance)

  soldierUnit.setUserObject(UnitType.SOLDIER)
  scientistUnit.setUserObject(UnitType.SCIENTIST)
  engineerUnit.setUserObject(UnitType.ENGINEER)

  playerUnitsTable.add(availableMix).fillX().prefHeight(soldierUnit.getPrefHeight)
  playerUnitsTable.row()
  playerUnitsTable.add(new Label("Choose your mix:",skin))
  playerUnitsTable.row()

  playerUnitsTable.add(solutionMix).fillX().prefHeight(soldierUnit.getPrefHeight)
  playerUnitsTable.row()
  playerUnitsTable.add(resultTitleLabel)
  playerUnitsTable.row()
  playerUnitsTable.add(resultLabel).fillX()
  playerUnitsTable.row()
  playerUnitsTable.add(applySolutionButton).pad(20,0,0,0)


  rootTable.add(playerUnitsTable)
  rootTable.add(opposingTable)

  stage.addActor(rootTable)



  resultLabel.setText(analyzeOutcome(collapsedEntrance,unitMix.toList))


  private def analyzeOutcome(subject:Subject, unitMix:List[UnitType] = unitMix.toList): String ={
    val reactsWithCount = unitMix.count(u=>u == subject.reactsWith.unit)
    val deltaChance = math.min(reactsWithCount * subject.reactsWith.deltaChanceToResolve,subject.maxChanceToResolve)

    reactsWithCount match {
      case x if x>0 => s"$deltaChance% to ${subject.outcomeDescription.toLowerCase}"
      case _ => "???"
    }
  }




  private def addUnitLeftClickListener(sourceActor:Actor,condition:()=>Boolean, mixGroup:Group,callback:() => Unit,resultLabel:Label,subject: Subject): Unit = {

    sourceActor.addCaptureListener(new ClickListener(Buttons.LEFT) {
      override def clicked(event: InputEvent, x: Float, y: Float): Unit = {
        val actor = new Image(sourceActor.asInstanceOf[Image].getDrawable.asInstanceOf[TextureRegionDrawable].getRegion.getTexture)
        actor.setUserObject(sourceActor.getUserObject)
        mixGroup.addActor(actor)
        val userObj = Option(actor.getUserObject)
        userObj match {
          case Some(t) if t.isInstanceOf[UnitType] =>
            unitMix += t.asInstanceOf[UnitType]
            resultLabel.setText(analyzeOutcome(subject))
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

  case class Subject(image:Image,reactsWith:ReactsWith,maxChanceToResolve:Int,outcomeDescription:String)
  case class ReactsWith(unit:UnitType,deltaChanceToResolve:Int)
}
