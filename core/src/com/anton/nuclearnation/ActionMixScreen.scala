package com.anton.nuclearnation

import com.anton.nuclearnation.Objective.Value
import com.anton.nuclearnation.MapScreen._
import com.anton.nuclearnation.UnitType.UnitType
import com.badlogic.gdx.Input.{Buttons, Keys}
import com.badlogic.gdx._
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.{Color, GL20, OrthographicCamera, Texture}
import com.badlogic.gdx.math.{Vector2, Vector3}
import com.badlogic.gdx.scenes.scene2d.{Actor, Group, InputEvent, Stage}
import com.badlogic.gdx.scenes.scene2d.ui._
import com.badlogic.gdx.scenes.scene2d.utils.{ClickListener, DragAndDrop, SpriteDrawable, TextureRegionDrawable}
import com.badlogic.gdx.utils.viewport.StretchViewport

import scala.collection.mutable.ListBuffer

class ActionMixScreen(targetLocation: MapLocation, game:NuclearNation, mapScreen: MapScreen) extends Screen{

  val stage = new Stage(new StretchViewport(1600,960,new OrthographicCamera()))
  val camera = stage.getCamera.asInstanceOf[OrthographicCamera]

  val assetManager = game.assetManager

  val skin = assetManager.get("data/commodore64/skin/uiskin.json",classOf[Skin])

  val crossedSwordsTexture = assetManager.get("unitConstruction/crossedSwords.png",classOf[Texture])
  val keyHoleTexture = assetManager.get("unitConstruction/spyKeyhole.png",classOf[Texture])
  val expeditionTexture = assetManager.get("actionMix/expeditionOutcome.png",classOf[Texture])
  val questionMarkTexture = assetManager.get("unitConstruction/questionMark.png",classOf[Texture])

  val meansPicture = new Image(questionMarkTexture)
  meansPicture.setName("means")

  val resultPicture = new Image(questionMarkTexture)

  val soldierLabel = new Label("Soldier",skin)
  val scientistLabel = new Label("Scientist",skin)
  val engineerLabel = new Label("Engineer",skin)

  var mixSoldierCounter = 0
  var mixScientistCounter = 0
  var mixEngineerCounter = 0


  val subjectPicture = targetLocation match {
    case _:RaiderCampInfo => new Image(assetManager.get("raider_camp.png",classOf[Texture]))
    case _:CityInfo => new Image(assetManager.get("town.png",classOf[Texture]))
    case _:RuinsInfo => new Image(assetManager.get("ruined-building.png",classOf[Texture]))
    case _:CoveredAreaInfo => new Image(assetManager.get("unitConstruction/questionMark.png",classOf[Texture]))
    case _=> throw new RuntimeException(s"Unknown target location type for: ${targetLocation.name}")
  }


  val soldierUnit = new Image(assetManager.get("unitConstruction/soldierUnit.png",classOf[Texture]))
  val scientistUnit = new Image(assetManager.get("unitConstruction/scientistUnit.png",classOf[Texture]))
  val engineerUnit = new Image(assetManager.get("unitConstruction/engineerUnit.png",classOf[Texture]))


  case class ActorUserObject(unitType: UnitType.UnitType,onRemovedFromStack:()=>Unit)

  soldierUnit.setUserObject(ActorUserObject(UnitType.SOLDIER,()=>{
    game.soldierCounter +=1
    mixSoldierCounter -=1
    updateUnitCountLabel("Soldier",soldierLabel,game.soldierCounter)
  }))

  scientistUnit.setUserObject(ActorUserObject(UnitType.SCIENTIST,()=>{
    game.scientistCounter +=1
    mixScientistCounter -=1
    updateUnitCountLabel("Scientist",scientistLabel,game.scientistCounter)
  }))

  engineerUnit.setUserObject(ActorUserObject(UnitType.ENGINEER,()=>{
    game.engineerCounter +=1
    mixEngineerCounter -=1
    updateUnitCountLabel("Engineer",engineerLabel,game.engineerCounter)
  }))

  val actionMixScreen = new InputProcessor() {

    override def keyUp(keycode: Int): Boolean = {
      if (keycode == Input.Keys.ESCAPE) {
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
  multiplexer.addProcessor(actionMixScreen)
  Gdx.input.setInputProcessor(multiplexer)


  private def updateUnitCountLabel(baseText:String,label:Label,count:Int): Unit ={
    label.setText(s"$baseText ($count)")
  }

  override def show(): Unit = {
    val titleLabel = new Label("CREATE ACTION MIX",skin)
    val hintLabel = new Label("Click to add one unit, SHIFT+click to add up to 10. \nMax 10 units of each kind allowed.\n",skin)
    val subjectLabel = new Label("Subject",skin)
    val meansLabel = new Label("Means",skin)
    val resultLabel = new Label("Result", skin)

    val assetsLabel = new Label("AVAILABLE ASSETS:",skin)

    val applyButton = new TextButton("Apply mix",skin)

    val controlGroup = new Group()
    val assetsGroup = new Group()
    assetsGroup.addActor(meansPicture)

    controlGroup.addActor(titleLabel)
    controlGroup.addActor(hintLabel)
    controlGroup.addActor(subjectLabel)
    controlGroup.addActor(subjectPicture)
    controlGroup.addActor(meansLabel)
    controlGroup.addActor(assetsGroup)
    controlGroup.addActor(resultLabel)
    controlGroup.addActor(resultPicture)
    controlGroup.addActor(applyButton)

    controlGroup.addActor(assetsLabel)
    controlGroup.addActor(soldierUnit)
    controlGroup.addActor(soldierLabel)

    controlGroup.addActor(scientistUnit)
    controlGroup.addActor(scientistLabel)

    controlGroup.addActor(engineerUnit)
    controlGroup.addActor(engineerLabel)

    controlGroup.setWidth(camera.viewportWidth)


    titleLabel.setPosition(controlGroup.getWidth/2,controlGroup.getHeight- titleLabel.getHeight)
    hintLabel.setPosition(controlGroup.getWidth/2,controlGroup.getHeight- titleLabel.getHeight - hintLabel.getPrefHeight-30)
    subjectLabel.setPosition(controlGroup.getWidth/2,controlGroup.getHeight- hintLabel.getHeight - subjectLabel.getHeight-30)
    subjectPicture.setPosition(subjectLabel.getX,subjectLabel.getY-subjectPicture.getHeight-10)
    meansLabel.setPosition(subjectPicture.getX(),subjectPicture.getY() - meansLabel.getHeight - 10)
    assetsGroup.setPosition(meansLabel.getX,meansLabel.getY - meansPicture.getHeight - 10)
    resultLabel.setPosition(assetsGroup.getX(),assetsGroup.getY - resultLabel.getHeight - 10)
    resultPicture.setPosition(resultLabel.getX,resultLabel.getY - resultPicture.getHeight - 10)
    applyButton.setPosition(resultPicture.getX,resultPicture.getY - applyButton.getHeight - 10)

    assetsLabel.setPosition(hintLabel.getX-assetsLabel.getPrefWidth-20,hintLabel.getY)

    soldierUnit.setPosition(assetsLabel.getX,assetsLabel.getY - soldierUnit.getPrefHeight-10)
    soldierLabel.setPosition(soldierUnit.getX,soldierUnit.getY - soldierLabel.getPrefHeight - 10)

    scientistUnit.setPosition(assetsLabel.getX,soldierLabel.getY - scientistUnit.getPrefHeight-10)
    scientistLabel.setPosition(scientistUnit.getX,scientistUnit.getY - scientistLabel.getPrefHeight - 10)

    engineerUnit.setPosition(assetsLabel.getX,scientistLabel.getY - engineerUnit.getPrefHeight-10)
    engineerLabel.setPosition(engineerUnit.getX,engineerUnit.getY - engineerLabel.getPrefHeight - 10)



    addUnitLeftClickListener(soldierUnit,()=>game.soldierCounter>0 && mixSoldierCounter <10,assetsGroup,()=>{
      game.soldierCounter -=1
      mixSoldierCounter +=1
      updateUnitCountLabel("Soldier",soldierLabel,game.soldierCounter)
    })

    updateUnitCountLabel("Soldier",soldierLabel,game.soldierCounter)

    addUnitLeftClickListener(scientistUnit,()=>game.scientistCounter>0 && mixScientistCounter <10,assetsGroup,()=>{
      game.scientistCounter -=1
      mixScientistCounter +=1
      updateUnitCountLabel("Scientist",scientistLabel,game.scientistCounter)
    })

    updateUnitCountLabel("Scientist",scientistLabel,game.scientistCounter)

    addUnitLeftClickListener(engineerUnit,()=>game.engineerCounter >0 && mixEngineerCounter <10,assetsGroup,()=>{
      game.engineerCounter -=1
      mixEngineerCounter +=1
      updateUnitCountLabel("Engineer",engineerLabel,game.engineerCounter)
    })

    updateUnitCountLabel("Engineer",engineerLabel,game.engineerCounter)

    stage.getBatch.setProjectionMatrix(camera.combined)

    applyButton.addCaptureListener(new ClickListener() {
      override def clicked(event: InputEvent, x: Float, y: Float): Unit = {

        val result = analyzeActionMix(assetsGroup)
        if (mixEngineerCounter + mixScientistCounter + mixSoldierCounter >0){
          mapScreen.sendExpedition(destCell = targetLocation.mapCell,units =result._2, objective = result._1)
          game.setScreen(mapScreen)
        }
      }
    })


    stage.addActor(controlGroup)
    val actor = new AssetCard(assetManager.get("unitConstruction/soldierUnit.png",classOf[Texture]),10,game)

    stage.addActor(actor)
    actor.setPosition(100,100)

    controlGroup.setPosition(0, camera.unproject(new Vector3(0,0,0)).y)

  }

  private def addUnitLeftClickListener(actor:Actor,condition:()=>Boolean, assetsGroup:Group,callback:() => Unit): Unit ={
    actor.addCaptureListener(new ClickListener(Buttons.LEFT){

      override def clicked(event: InputEvent, x: Float, y: Float): Unit = {
        val repeats = if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)) 10 else 1

        var counter = 0
        while (counter < repeats && condition()) {
          addActorToAssetGroup(actor, assetsGroup)
          callback()
          counter +=1
        }
      }
    })
  }


  private def addActorToAssetGroup(sourceActor:Actor,assetGroup:Group): Unit ={
    val means = assetGroup.findActor[Image]("means")
    val size = assetGroup.getChildren.size
    val furthestRightActor = if (means == null) assetGroup.getChildren.get(size-1) else means
    val actor = new Image(sourceActor.asInstanceOf[Image].getDrawable.asInstanceOf[TextureRegionDrawable].getRegion.getTexture)
    actor.setUserObject(sourceActor.getUserObject)
    actor.setPosition(furthestRightActor.getX + actor.getPrefWidth + 5,furthestRightActor.getY)
    assetGroup.addActor(actor)
    updateResultPicture(assetGroup)

    actor.addListener(new ClickListener(){
      override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) :Boolean= {
        if (button == Buttons.RIGHT || button == Buttons.LEFT){
          assetGroup.removeActor(actor)
          val userObject = actor.getUserObject.asInstanceOf[ActorUserObject]
          userObject.onRemovedFromStack()
          val size = assetGroup.getChildren.size
          for (i<-0 until size){
            val groupActor = assetGroup.getChildren.get(i)
            if (i ==0 ) {
              groupActor.setPosition(0, 0)
            } else {
              val previousActor = assetGroup.getChildren.get(i - 1)
              groupActor.setPosition(previousActor.getX + previousActor.getWidth + 5, previousActor.getY())
            }
          }

          if (assetGroup.getChildren.size == 0){
            assetGroup.addActor(meansPicture)
            meansPicture.setPosition(0,0)
            meansPicture.setColor(Color.WHITE)
          }
          updateResultPicture(assetGroup)
          return true
        }
        false
      }
    })

    if (means != null){
      actor.setX(means.getX)
      assetGroup.removeActor(means)
    }
  }

  private def analyzeActionMix(assetGroup:Group): (Objective.Objective,scala.List[UnitType]) ={
    var soldiersCommandoCounter = 0
    var spyCounter = 0

    val filteredAssetGroup = assetGroup.getChildren.toArray.filter(a=>a != meansPicture)
    val size = filteredAssetGroup.length
    val units = ListBuffer[UnitType]()
    for (i<-0 until size){
      val actor = filteredAssetGroup(i)
      if (actor.getUserObject != null) {
        val unitType = filteredAssetGroup(i).getUserObject.asInstanceOf[ActorUserObject].unitType
        unitType match {
          case UnitType.SOLDIER | UnitType.COMMANDO => soldiersCommandoCounter += 1
          case UnitType.SPY => spyCounter += 1
          case _ =>
        }
        units += unitType
      }
    }

    val objective = if (targetLocation.isInstanceOf[CoveredAreaInfo] && size >0){
      Objective.EXPEDITION
    } else{ //target is discovered
        if (soldiersCommandoCounter > 0 && spyCounter == 0) {
          Objective.COMBAT
        } else if (spyCounter > 0 && soldiersCommandoCounter == 0) {
          Objective.SURVEILLANCE
        } else {
          Objective.UNKNOWN
        }
    }

    (objective,units.toList)


  }


  private def updateResultPicture(assetGroup: Group): Unit={

    val texture:Texture =
      analyzeActionMix(assetGroup)._1 match {
        case Objective.COMBAT=>crossedSwordsTexture
        case Objective.SURVEILLANCE=>keyHoleTexture
        case Objective.EXPEDITION => expeditionTexture
        case _=> questionMarkTexture
      }

    resultPicture.setDrawable(new SpriteDrawable(new Sprite(texture)))

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
}
