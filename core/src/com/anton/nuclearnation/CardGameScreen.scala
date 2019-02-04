package com.anton.nuclearnation

import com.anton.nuclearnation.MapScreen.{CityInfo, MapLocation, RaiderCampInfo, RuinsInfo}
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

  val soldierUnit = new Image(assetManager.get("unitConstruction/soldierUnit.png",classOf[Texture]))
  val scientistUnit = new Image(assetManager.get("unitConstruction/scientistUnit.png",classOf[Texture]))
  val engineerUnit = new Image(assetManager.get("unitConstruction/engineerUnit.png",classOf[Texture]))

  val skin = assetManager.get("data/commodore64/skin/uiskin.json",classOf[Skin])

  val startButton = new TextButton("Start",skin)
  val applySolutionButton = new TextButton("Apply solution",skin)
  val cancelButton = new TextButton("Cancel",skin)
  val descriptionLabel = new Label("[EMPTY]",skin)


  val rubbleImage = new Image(assetManager.get("cardGameScreen/rubble.png",classOf[Texture]))
  val brokenMachineryImage = new Image(assetManager.get("cardGameScreen/brokenMachinery.png",classOf[Texture]))
  val monsterNestImage = new Image(assetManager.get("cardGameScreen/monster.png",classOf[Texture]))


  val rootTable = new Table()
  rootTable.setFillParent(true)


  val playerUnitsTable = new Table().top()
  val opposingTable = new Table().top()

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

  val collapsedEntrance = Subject(
    rubbleImage,
    preface = "You have arrived to the ancient ruins. You see the entrance is blocked with a pile of rubble.",
    reactsWith = ReactsWith(UnitType.ENGINEER,10),
    90,
    "Clear the rubble",
    killFactor = "falling debris")
  val brokenMachinery = Subject(
    brokenMachineryImage,
    preface = "You see remnants of old machinery. It could be studied.",
    reactsWith = ReactsWith(UnitType.SCIENTIST,10),
    90,
    "Study the machinery",
    killFactor = "poisonous gas")

  val monsterNest = Subject(
    monsterNestImage,
    preface = "You uncovered a monster nest",
    reactsWith = ReactsWith(UnitType.SOLDIER,10),
    90,
    "Destroy the monster nest",
    killFactor = "monsters"
  )

  val subjects:List[Subject] = List(collapsedEntrance,brokenMachinery,monsterNest)

  var currentSubjectIndex =0

  descriptionLabel.setText(subjects(currentSubjectIndex).preface)

  val resultTitleLabel = new Label("Result:",skin)
  val resultLabel = new Label("???",skin)
  resultLabel.setWrap(true)

  val soldierCard = new AssetCard(soldierUnit,0,game,UnitType.SOLDIER)
  val engineerCard = new AssetCard(engineerUnit,0,game,UnitType.ENGINEER)
  val scientistCard = new AssetCard(scientistUnit,0,game,UnitType.SCIENTIST)

  playerUnits.foreach {
    case UnitType.SOLDIER =>
      soldierCard.updateCount(1)
    case UnitType.SCIENTIST =>
      scientistCard.updateCount(1)
    case UnitType.ENGINEER =>
      engineerCard.updateCount(1)
  }

  if (soldierCard.count >0){
    availableMixGroup.addActor(soldierCard)
  }

  if (engineerCard.count >0){
    availableMixGroup.addActor(engineerCard)
  }

  if (scientistCard.count >0){
    availableMixGroup.addActor(scientistCard)
  }

  addUnitLeftClickListener(soldierCard,solutionMixGroup,resultLabel,subjects(currentSubjectIndex))
  addUnitLeftClickListener(engineerCard,solutionMixGroup,resultLabel,subjects(currentSubjectIndex))
  addUnitLeftClickListener(scientistCard,solutionMixGroup,resultLabel,subjects(currentSubjectIndex))


  val mixPrefHeight = new Image(assetManager.get("unitConstruction/soldierUnit.png",classOf[Texture])).getPrefHeight


  playerUnitsTable.add(availableMixGroup).fillX().prefHeight(mixPrefHeight)
  playerUnitsTable.row()
  playerUnitsTable.add(new Label("Choose your stack:",skin))
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

  resultLabel.setText(analyzeOutcome(subjects(currentSubjectIndex)).description)

  def resetActionMix(): Unit ={
    solutionMixGroup.getChildren.toArray.foreach(a=>{
      availableMixGroup.addActor(a)
    })

    availableMixGroup.getChildren.toArray.foreach(a=>{
      a.clearListeners()
      addUnitLeftClickListener(a.asInstanceOf[AssetCard],solutionMixGroup,resultLabel,subjects(currentSubjectIndex))
    })

    resultLabel.setText(analyzeOutcome(subjects(currentSubjectIndex)).description)
  }

  cancelButton.addCaptureListener(new ClickListener(){
    override def clicked(event: InputEvent, x: Float, y: Float): Unit = {
      game.setScreen(mapScreen)
    }
  })

  applySolutionButton.addCaptureListener(new ClickListener(){
    override def clicked(event: InputEvent, x: Float, y: Float): Unit = {

      if (solutionMixGroup.getChildren.size == 0) return

      val chance = Random.nextInt(100)
      val successChance =  analyzeOutcome(subjects(currentSubjectIndex)).successChance
      val outcomeDescription = subjects(currentSubjectIndex).outcomeDescription.toLowerCase
      val currentUnit = subjects(currentSubjectIndex).reactsWith.unit
      val unitPlural = currentUnit.toString.toLowerCase() + "s"


      val dialog = new Dialog("", skin) {
        override def result(result:Object) {
          if (chance<successChance) {
            if (currentSubjectIndex == subjects.size-1) {
              mapScreen.discoverTech(currentLocation.asInstanceOf[RuinsInfo])
              game.setScreen(mapScreen)
            } else {
              currentSubjectIndex += 1
              opposingImageCell.clearActor()
              opposingImageCell.setActor(subjects(currentSubjectIndex).image)
              descriptionLabel.setText(subjects(currentSubjectIndex).preface)
              resetActionMix()

            }
          }
        }
      }

      val text = if (chance<successChance) {
        Some(s"Your $unitPlural managed to $outcomeDescription")
      } else {
        //killing some units
        val assetCard =
          solutionMixGroup
            .getChildren
            .toArray.find(u => u.asInstanceOf[AssetCard]
            .getUserObject.asInstanceOf[UnitType] == currentUnit)
        assetCard match {
          case None=>None
          case Some(c) => c.asInstanceOf[AssetCard]
            val assetCard = c.asInstanceOf[AssetCard]
            val killedUnitCount = Random.nextInt(assetCard.count)+1
            assetCard.updateCount(-killedUnitCount)
            if (assetCard.count <=0){
              solutionMixGroup.removeActor(assetCard)
            }
            resultLabel.setText(analyzeOutcome(subjects(currentSubjectIndex)).description)
            Some(s"Your $unitPlural failed to $outcomeDescription. $killedUnitCount were killed with ${subjects(currentSubjectIndex).killFactor.toLowerCase()}.")
        }

      }

      text match{
        case Some(t)=>
          val label = new Label(t,skin)
          label.setWrap(true)
          dialog.getContentTable.add(label).prefWidth(350)
          dialog.button("OK", true)
          dialog.key(Keys.ESCAPE, false).key(Keys.ENTER, true)
          dialog.pack()
          stage.addActor(dialog)
        case None=>
      }

    }
  })


  private def analyzeOutcome(subject:Subject): Outcome ={
    val reactsWithUnit = solutionMixGroup.getChildren.toArray().find(u=>u.getUserObject.asInstanceOf[UnitType] == subject.reactsWith.unit)

    val reactsWithCount = reactsWithUnit match{
      case Some(u) => u.asInstanceOf[AssetCard].count
      case None=> 0
    }
    val deltaChance = math.min(reactsWithCount * subject.reactsWith.deltaChanceToResolve,subject.maxChanceToResolve)

    reactsWithCount match {
      case x if x>0 => Outcome(deltaChance,s"$deltaChance% to ${subject.outcomeDescription.toLowerCase}")
      case _ => Outcome(0,"???")
    }
  }

  case class Outcome(successChance:Int,description:String)



  private def addUnitLeftClickListener(sourceActor:AssetCard, mixGroup:Group,resultLabel:Label,subject: Subject): Unit = {

    sourceActor.clearListeners()
    sourceActor.addCaptureListener(new ClickListener(Buttons.LEFT) {
      override def clicked(event: InputEvent, x: Float, y: Float): Unit = {
        mixGroup.addActor(sourceActor)
        sourceActor.clearListeners()
        sourceActor.addListener(new ClickListener(Buttons.LEFT){
          override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) :Boolean= {
            solutionMixGroup.removeActor(sourceActor)
            availableMixGroup.addActor(sourceActor)
            addUnitLeftClickListener(sourceActor,mixGroup,resultLabel,subject)
            resultLabel.setText(analyzeOutcome(subject).description)
            true
          }
        })

        availableMixGroup.removeActor(sourceActor)
        resultLabel.setText(analyzeOutcome(subject).description)
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
                     preface: String,
                     reactsWith:ReactsWith,
                     maxChanceToResolve:Int,
                     outcomeDescription:String,
                     killFactor:String
                    )
  case class ReactsWith(unit:UnitType,deltaChanceToResolve:Int)
}
