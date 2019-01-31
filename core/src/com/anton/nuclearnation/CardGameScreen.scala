package com.anton.nuclearnation

import com.anton.nuclearnation.MapScreen.{CityInfo, MapLocation, RaiderCampInfo}
import com.anton.nuclearnation.UnitType.UnitType
import com.badlogic.gdx.Input.{Buttons, Keys}
import com.badlogic.gdx.graphics.{GL20, OrthographicCamera, Texture}
import com.badlogic.gdx.{Gdx, InputMultiplexer, InputProcessor, Screen}
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.scenes.scene2d.{Actor, Group, InputEvent, Stage}
import com.badlogic.gdx.scenes.scene2d.ui.{Container, Dialog, HorizontalGroup, Image, Label, Skin, Table, TextButton}
import com.badlogic.gdx.scenes.scene2d.utils.{ClickListener, TextureRegionDrawable}
import com.badlogic.gdx.utils.viewport.StretchViewport

import scala.collection.mutable.ListBuffer
import scala.util.Random

class CardGameScreen(currentLocation:MapLocation, game:NuclearNation, mapScreen:MapScreen, playerUnits:List[UnitType]) extends Screen{

  val assetManager = game.assetManager
  val map = new TiledMap
  val layers = map.getLayers

  val stage = new Stage(new StretchViewport(800,600,new OrthographicCamera()))
  val camera = stage.getCamera



  val skin = assetManager.get("data/commodore64/skin/uiskin.json",classOf[Skin])

  val startButton = new TextButton("Start",skin)
  val applySolutionButton = new TextButton("Apply solution",skin)
  val cancelButton = new TextButton("Cancel",skin)
  val descriptionLabel = new Label("You have arrived to the ancient ruins. You see the entrance blocked with a pile of rubble.",skin)


  val rubbleImage = new Image(assetManager.get("cardGameScreen/rubble.png",classOf[Texture]))
  val brokenMachineryImage = new Image(assetManager.get("cardGameScreen/brokenMachinery.png",classOf[Texture]))

  val solutionUnitMix=ListBuffer[UnitType]()
  val availableUnitMix = playerUnits.to[ListBuffer]

  val rootTable = new Table()
  rootTable.setFillParent(true)


  val playerUnitsTable = new Table().top()
  val opposingTable = new Table().top()
//
//  rootTable.setDebug(true)
//  opposingTable.setDebug(true)
//  playerUnitsTable.setDebug(true)

  val solutionMixGroup = new HorizontalGroup
  solutionMixGroup.wrap

  val opposingImageCell = opposingTable.add(rubbleImage)
  opposingTable.row()
  descriptionLabel.setWrap(true)
  opposingTable.add(descriptionLabel).fillX.pad(50,0,0,0)

  playerUnitsTable.add(new Label("Available assets:",skin))
  playerUnitsTable.row()
  val availableMixGroup = new HorizontalGroup()
  availableMixGroup.wrap()

  val collapsedEntrance = Subject(rubbleImage,reactsWith = ReactsWith(UnitType.ENGINEER,10),90,"Clear the entrance",killFactor = "falling debris")
  val brokenMachinery = Subject(brokenMachineryImage,reactsWith = ReactsWith(UnitType.SCIENTIST,10),90,"Study the machinery",killFactor = "poisonous gas")

  val subjects:List[Subject] = List(collapsedEntrance,brokenMachinery)

  var currentSubjectIndex =0

  val resultTitleLabel = new Label("Result:",skin)
  val resultLabel = new Label("???",skin)
  resultLabel.setWrap(true)

  playerUnits.foreach(u=>{

    val actor = u match {
      case UnitType.SOLDIER=>
        val soldierUnit = new Image(assetManager.get("unitConstruction/soldierUnit.png",classOf[Texture]))
        soldierUnit.setUserObject(UnitType.SOLDIER)
        addUnitLeftClickListener(soldierUnit,solutionMixGroup, ()=>{},resultLabel,subjects(currentSubjectIndex))
        soldierUnit
      case UnitType.SCIENTIST =>
        val scientistUnit = new Image(assetManager.get("unitConstruction/scientistUnit.png",classOf[Texture]))
        scientistUnit.setUserObject(UnitType.SCIENTIST)
        addUnitLeftClickListener(scientistUnit,solutionMixGroup, ()=>{},resultLabel,subjects(currentSubjectIndex))
        scientistUnit
      case UnitType.ENGINEER =>
        val engineerUnit = new Image(assetManager.get("unitConstruction/engineerUnit.png",classOf[Texture]))
        engineerUnit.setUserObject(UnitType.ENGINEER)
        addUnitLeftClickListener(engineerUnit,solutionMixGroup, ()=>{},resultLabel,subjects(currentSubjectIndex))
        engineerUnit
    }
    availableMixGroup.addActor(actor)
  })

  val mixPrefHeight = new Image(assetManager.get("unitConstruction/soldierUnit.png",classOf[Texture])).getPrefHeight


  playerUnitsTable.add(availableMixGroup).fillX().prefHeight(mixPrefHeight)
  playerUnitsTable.row()
  playerUnitsTable.add(new Label("Choose your mix:",skin))
  playerUnitsTable.row()

  playerUnitsTable.add(solutionMixGroup).fillX().prefHeight(mixPrefHeight)
  playerUnitsTable.row()
  playerUnitsTable.add(resultTitleLabel)
  playerUnitsTable.row()
  playerUnitsTable.add(resultLabel).fillX()
  playerUnitsTable.row()
  playerUnitsTable.add(applySolutionButton).pad(20,0,0,0)
  playerUnitsTable.row()
  playerUnitsTable.add(cancelButton).pad(20,0,0,0)


  rootTable.add(playerUnitsTable).pad(0,0,0,100)
  rootTable.add(opposingTable)

  stage.addActor(rootTable)



  resultLabel.setText(analyzeOutcome(subjects(currentSubjectIndex),solutionUnitMix.toList).description)

  cancelButton.addCaptureListener(new ClickListener(){
    override def clicked(event: InputEvent, x: Float, y: Float): Unit = {
      game.setScreen(mapScreen)
    }
  })

  applySolutionButton.addCaptureListener(new ClickListener(){
    override def clicked(event: InputEvent, x: Float, y: Float): Unit = {

      val chance = Random.nextInt(100)
      val successChance =  analyzeOutcome(subjects(currentSubjectIndex)).successChance
      val currentUnit = subjects(currentSubjectIndex).reactsWith.unit
      val unitPlural = currentUnit.toString.toLowerCase() + "s"

      val dialog = new Dialog("Rubble cleared", skin) {
        override def result(result:Object) {
          if (chance<successChance) {
            if (currentSubjectIndex == subjects.size) {
              game.setScreen(mapScreen)
            } else {
              currentSubjectIndex += 1
             opposingImageCell.clearActor()
              opposingImageCell.setActor(subjects(currentSubjectIndex).image)
//              currentActor.setDrawable(subjects(currentSubjectIndex).image.getDrawable.asInstanceOf[TextureRegionDrawable])
            }
          }
        }
      }

      val text = if (chance<successChance) {
        s"Your $unitPlural managed to clear the rubble."
      } else {
        //killing some units
        val killedUnitCount = Random.nextInt(solutionUnitMix.count(u => u == currentUnit))+1
        for (_ <-0 until killedUnitCount){
          solutionMixGroup.getChildren.toArray().find(a=>a.getUserObject!= null && a.getUserObject.asInstanceOf[UnitType] == currentUnit).get.remove()
          val index = solutionUnitMix.indexOf(subjects(currentSubjectIndex).reactsWith.unit)
          solutionUnitMix.remove(index)
        }
        resultLabel.setText(analyzeOutcome(subjects(currentSubjectIndex)).description)
        s"Your $unitPlural failed to clear the rubble. $killedUnitCount were killed with ${subjects(currentSubjectIndex).killFactor.toLowerCase()}."
      }

      val label = new Label(text,skin)
      label.setWrap(true)
      dialog.getContentTable.add(label).prefWidth(350)
      dialog.button("OK", true)
      dialog.key(Keys.ESCAPE, false).key(Keys.ENTER, true)
      dialog.pack()
      stage.addActor(dialog)
    }
  })


  private def analyzeOutcome(subject:Subject, unitMix:List[UnitType] = solutionUnitMix.toList): Outcome ={
    val reactsWithCount = unitMix.count(u=>u == subject.reactsWith.unit)
    val deltaChance = math.min(reactsWithCount * subject.reactsWith.deltaChanceToResolve,subject.maxChanceToResolve)

    reactsWithCount match {
      case x if x>0 => Outcome(deltaChance,s"$deltaChance% to ${subject.outcomeDescription.toLowerCase}")
      case _ => Outcome(0,"???")
    }
  }

  case class Outcome(successChance:Int,description:String)



  private def addUnitLeftClickListener(sourceActor:Actor, mixGroup:Group,callback:() => Unit,resultLabel:Label,subject: Subject): Unit = {

    sourceActor.clearListeners()
    sourceActor.addCaptureListener(new ClickListener(Buttons.LEFT) {
      override def clicked(event: InputEvent, x: Float, y: Float): Unit = {
        mixGroup.addActor(sourceActor)
        sourceActor.clearListeners()
        sourceActor.addListener(new ClickListener(Buttons.LEFT){
          override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) :Boolean= {
            solutionMixGroup.removeActor(sourceActor)
            availableMixGroup.addActor(sourceActor)
            addUnitLeftClickListener(sourceActor,mixGroup,()=>{},resultLabel,subject)
            availableUnitMix += sourceActor.getUserObject.asInstanceOf[UnitType]
            val index = solutionUnitMix.indexOf(sourceActor.getUserObject.asInstanceOf[UnitType])
            solutionUnitMix.remove(index)
            resultLabel.setText(analyzeOutcome(subject).description)
            true
          }
        })

        availableMixGroup.removeActor(sourceActor)
        val index = availableUnitMix.indexOf(sourceActor.getUserObject.asInstanceOf[UnitType])
        availableUnitMix.remove(index)
        val userObj = Option(sourceActor.getUserObject)
        userObj match {
          case Some(t) if t.isInstanceOf[UnitType] =>
            solutionUnitMix += t.asInstanceOf[UnitType]
            resultLabel.setText(analyzeOutcome(subject).description)
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

  override def hide(): Unit = {
    dispose()
  }

  override def dispose(): Unit = {}

  case class Subject(image:Image,
                     reactsWith:ReactsWith,
                     maxChanceToResolve:Int,
                     outcomeDescription:String,
                     killFactor:String
                    )
  case class ReactsWith(unit:UnitType,deltaChanceToResolve:Int)
}
