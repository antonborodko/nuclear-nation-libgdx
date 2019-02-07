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

  val stage = new Stage(new StretchViewport(800 ,600,new OrthographicCamera()))
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


  val subjectPicture = targetLocation match {
    case _:RaiderCampInfo => new Image(assetManager.get("raider_camp.png",classOf[Texture]))
    case _:CityInfo => new Image(assetManager.get("town.png",classOf[Texture]))
    case _:RuinsInfo => new Image(assetManager.get("ruined-building.png",classOf[Texture]))
    case _:CoveredAreaInfo => new Image(assetManager.get("unitConstruction/questionMark.png",classOf[Texture]))
    case _=> throw new RuntimeException(s"Unknown target location type for: ${targetLocation.name}")
  }

  val soldierTexture = assetManager.get("unitConstruction/soldierUnit.png",classOf[Texture])
  val scientistTexture = assetManager.get("unitConstruction/scientistUnit.png",classOf[Texture])
  val engineerTexture = assetManager.get("unitConstruction/engineerUnit.png",classOf[Texture])

  val soldierUnitClass = new Image(soldierTexture)
  val scientistUnitClass = new Image(scientistTexture)
  val engineerUnitClass = new Image(engineerTexture)

  val soldierCard = new AssetCard(new Image(soldierTexture),"SOLDIER",0,game,None)
  val engineerCard = new AssetCard(new Image(scientistTexture),"ENGINEER",0,game,None)
  val scientistCard = new AssetCard(new Image(engineerTexture),"SCIENTIST",0,game,None)

  soldierCard.setUserObject(ActorUserObject(UnitType.SOLDIER, ()=>{
    game.soldierCounter +=1
    soldierCard.updateCount(-1)
    if (soldierCard.count <=0){
      assetsGroup.removeActor(soldierCard)
    }
    updateUnitCountLabel("Soldier",soldierLabel,game.soldierCounter)
  }))

  scientistCard.setUserObject(ActorUserObject(UnitType.SCIENTIST, ()=>{
    game.scientistCounter +=1
    scientistCard.updateCount(-1)
    if (scientistCard.count <=0){
      assetsGroup.removeActor(scientistCard)
    }
    updateUnitCountLabel("Scientist",scientistLabel,game.scientistCounter)
  }))


  engineerCard.setUserObject(ActorUserObject(UnitType.ENGINEER, ()=>{
    game.engineerCounter +=1
    engineerCard.updateCount(-1)
    if (engineerCard.count <=0){
      assetsGroup.removeActor(engineerCard)
    }
    updateUnitCountLabel("Engineer",engineerLabel,game.engineerCounter)
  }))


  case class ActorUserObject(unitType: UnitType.UnitType, onCountDecreased:()=>Unit)
  val assetsGroup = new Group()

  soldierUnitClass.setUserObject(ActorUserObject(UnitType.SOLDIER, ()=>{
    game.soldierCounter +=1
    soldierCard.updateCount(-1)
    if (soldierCard.count <=0){
      assetsGroup.removeActor(soldierCard)
    }
    updateUnitCountLabel("Soldier",soldierLabel,game.soldierCounter)
  }))

  scientistUnitClass.setUserObject(ActorUserObject(UnitType.SCIENTIST, ()=>{
    game.scientistCounter +=1
    scientistCard.updateCount(-1)
    if (scientistCard.count <=0){
      assetsGroup.removeActor(scientistCard)
    }
    updateUnitCountLabel("Scientist",scientistLabel,game.scientistCounter)
  }))

  engineerUnitClass.setUserObject(ActorUserObject(UnitType.ENGINEER, ()=>{
    game.engineerCounter +=1
    engineerCard.updateCount(-1)
    if (engineerCard.count <=0){
      assetsGroup.removeActor(engineerCard)
    }
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
    controlGroup.addActor(soldierUnitClass)
    controlGroup.addActor(soldierLabel)

    controlGroup.addActor(scientistUnitClass)
    controlGroup.addActor(scientistLabel)

    controlGroup.addActor(engineerUnitClass)
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

    soldierUnitClass.setPosition(assetsLabel.getX,assetsLabel.getY - soldierUnitClass.getPrefHeight-10)
    soldierLabel.setPosition(soldierUnitClass.getX,soldierUnitClass.getY - soldierLabel.getPrefHeight - 10)

    scientistUnitClass.setPosition(assetsLabel.getX,soldierLabel.getY - scientistUnitClass.getPrefHeight-10)
    scientistLabel.setPosition(scientistUnitClass.getX,scientistUnitClass.getY - scientistLabel.getPrefHeight - 10)

    engineerUnitClass.setPosition(assetsLabel.getX,scientistLabel.getY - engineerUnitClass.getPrefHeight-10)
    engineerLabel.setPosition(engineerUnitClass.getX,engineerUnitClass.getY - engineerLabel.getPrefHeight - 10)



    addUnitLeftClickListener(soldierUnitClass, soldierCard,()=>game.soldierCounter>0 && soldierCard.count <10,assetsGroup, ()=>{
      game.soldierCounter -=1
      if (soldierCard.count == 0){
        assetsGroup.addActor(soldierCard)
      }
      updateUnitCountLabel("Soldier",soldierLabel,game.soldierCounter)
    })

    updateUnitCountLabel("Soldier",soldierLabel,game.soldierCounter)

    addUnitLeftClickListener(scientistUnitClass, scientistCard, ()=>game.scientistCounter>0 && scientistCard.count <10,assetsGroup, ()=>{
      game.scientistCounter -=1
      if (scientistCard.count == 0){
        assetsGroup.addActor(scientistCard)
      }
      updateUnitCountLabel("Scientist",scientistLabel,game.scientistCounter)
    })
    updateUnitCountLabel("Scientist",scientistLabel,game.scientistCounter)

    addUnitLeftClickListener(engineerUnitClass,engineerCard, ()=>game.engineerCounter >0 && engineerCard.count <10,assetsGroup, ()=>{
      game.engineerCounter -=1
      if (engineerCard.count == 0){
        assetsGroup.addActor(engineerCard)
      }
      updateUnitCountLabel("Engineer",engineerLabel,game.engineerCounter)
    })

    updateUnitCountLabel("Engineer",engineerLabel,game.engineerCounter)

    stage.getBatch.setProjectionMatrix(camera.combined)

    applyButton.addCaptureListener(new ClickListener() {
      override def clicked(event: InputEvent, x: Float, y: Float): Unit = {

        val result = analyzeActionMix(assetsGroup)
        if (engineerCard.count + soldierCard.count + scientistCard.count >0){
          mapScreen.sendExpedition(destCell = targetLocation.mapCell,units =result._2, objective = result._1)
          game.setScreen(mapScreen)
        }
      }
    })


    stage.addActor(controlGroup)

    controlGroup.setPosition(0, camera.unproject(new Vector3(0,0,0)).y)

  }

  private def addUnitLeftClickListener(sourceActor:Image,card:AssetCard,condition:()=>Boolean, assetsGroup:Group,callback:() => Unit): Unit ={
    sourceActor.addCaptureListener(new ClickListener(Buttons.LEFT){

      override def clicked(event: InputEvent, x: Float, y: Float): Unit = {
        val repeats = if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)) 10 else 1

        var counter = 0
        while (counter < repeats && condition()) {
          addActorToAssetGroup(card, assetsGroup)
          callback()
          counter +=1
        }
      }
    })
  }


  private def addActorToAssetGroup(assetCard:AssetCard,assetGroup:Group): Unit ={
    val means = assetGroup.findActor[Image]("means")
    assetGroup.getChildren.toArray().find(
        c=>c.isInstanceOf[AssetCard]
        && c.getUserObject.asInstanceOf[ActorUserObject].unitType == assetCard.getUserObject.asInstanceOf[ActorUserObject].unitType
    ) match {
      case None=>
        val size = assetGroup.getChildren.size
        val furthestRightActor = if (means == null) assetGroup.getChildren.get(size-1) else means
        assetCard.setPosition(furthestRightActor.getX + assetCard.getPrefWidth + 5,furthestRightActor.getY)
        assetGroup.addActor(assetCard)
        assetCard.clearListeners()
        assetCard.addListener(new ClickListener(){
          override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) :Boolean= {
            if (button == Buttons.RIGHT || button == Buttons.LEFT){
              val userObject = assetCard.getUserObject.asInstanceOf[ActorUserObject]
              userObject.onCountDecreased()
              if (assetCard.count == 0){
                assetGroup.removeActor(assetCard)
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
            }
            false
          }
        })
      case Some(_)=>
    }
    assetCard.updateCount(1)
    updateResultPicture(assetGroup)



    if (means != null){
      assetCard.setX(means.getX)
      assetGroup.removeActor(means)
    }
  }

  private def analyzeActionMix(assetGroup:Group): (Objective.Objective,scala.List[UnitType]) ={
    val units = ListBuffer[UnitType]()

    assetGroup.getChildren.toArray().filter(u=>u.isInstanceOf[AssetCard]).foreach(u=>{
      val c = u.asInstanceOf[AssetCard]
      for (_<- 0 until c.count){
        units += c.getUserObject.asInstanceOf[ActorUserObject].unitType
      }
    })

    (Objective.EXPEDITION,units.toList)


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
