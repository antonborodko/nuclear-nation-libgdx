package com.anton.nuclearnation

import com.anton.nuclearnation.MapScreen._
import com.anton.nuclearnation.UnitType.UnitType
import com.badlogic.gdx.Input.{Buttons, Keys}
import com.badlogic.gdx._
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.{Color, GL20, OrthographicCamera, Texture}
import com.badlogic.gdx.math.{Vector2, Vector3}
import com.badlogic.gdx.scenes.scene2d.{Actor, Group, InputEvent, Stage}
import com.badlogic.gdx.scenes.scene2d.ui._
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.{Payload, Source, Target}
import com.badlogic.gdx.scenes.scene2d.utils.{ClickListener, DragAndDrop, SpriteDrawable, TextureRegionDrawable}
import com.badlogic.gdx.utils.viewport.StretchViewport

import scala.collection.JavaConverters

class ActionMixScreen(targetLocation: MapLocation, game:NuclearNation, mapScreen: MapScreen) extends Screen{

  val stage = new Stage(new StretchViewport(1600,960,new OrthographicCamera()))
  val camera = stage.getCamera.asInstanceOf[OrthographicCamera]

  val assetManager = game.assetManager

  val skin = assetManager.get("data/commodore64/skin/uiskin.json",classOf[Skin])

  val crossedSwordsTexture = assetManager.get("unitConstruction/crossedSwords.png",classOf[Texture])
  val keyHoleTexture = assetManager.get("unitConstruction/spyKeyhole.png",classOf[Texture])
  val expeditionTexture = assetManager.get("expedition.png",classOf[Texture])
  val questionMarkTexture = assetManager.get("unitConstruction/questionMark.png",classOf[Texture])

  val meansPicture = new Image(questionMarkTexture)
  meansPicture.setName("means")

  val resultPicture = new Image(questionMarkTexture)

  val soldierLabel = new Label("Soldier",skin)
  val commandoLabel = new Label("Commando",skin)
  val spyLabel = new Label("Spy",skin)


  object ActionMixOutcome extends Enumeration {
    type MixOutcome = Value
    val SURVEILLANCE, COMBAT, EXPEDITION, UNKNOWN = Value
  }

  val subjectPicture = targetLocation match {
    case _:RaiderCampInfo => new Image(assetManager.get("raider_camp.png",classOf[Texture]))
    case _:CityInfo => new Image(assetManager.get("town.png",classOf[Texture]))
    case _:RuinsInfo => new Image(assetManager.get("ruined-building.png",classOf[Texture]))
    case _:CoveredAreaInfo => new Image(assetManager.get("unitConstruction/questionMark.png",classOf[Texture]))
    case _=> throw new RuntimeException(s"Unknown target location type for: ${targetLocation.name}")
  }


  val soldierUnit = new Image(assetManager.get("unitConstruction/soldierUnit.png",classOf[Texture]))
  val commandoUnit = new Image(assetManager.get("unitConstruction/commandoUnit.png",classOf[Texture]))
  val spyUnit = new Image(assetManager.get("unitConstruction/spyUnit.png",classOf[Texture]))


  case class ActorUserObject(unitType: UnitType.UnitType,onRemovedFromStack:()=>Unit)

  soldierUnit.setUserObject(ActorUserObject(UnitType.SOLDIER,()=>{
    game.soldierCounter +=1
    updateUnitCountLabel("Soldier",soldierLabel,game.soldierCounter)
  }))
  commandoUnit.setUserObject(ActorUserObject(UnitType.COMMANDO,()=>{
    game.commandoCounter +=1
    updateUnitCountLabel("Commando",commandoLabel,game.commandoCounter)
  }))
  spyUnit.setUserObject(ActorUserObject(UnitType.SPY,()=>{
    game.spyCounter +=1
    updateUnitCountLabel("Spy",spyLabel,game.spyCounter)
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
    val subjectLabel = new Label("Subject",skin)
    val meansLabel = new Label("Means",skin)
    val resultLabel = new Label("Result", skin)

    val assetsLabel = new Label("AVAILABLE ASSETS:",skin)

    val applyButton = new TextButton("Apply mix",skin)

    val controlGroup = new Group()
    val assetsGroup = new Group()
    assetsGroup.addActor(meansPicture)

    controlGroup.addActor(titleLabel)
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
    controlGroup.addActor(commandoUnit)
    controlGroup.addActor(commandoLabel)
    controlGroup.addActor(spyUnit)
    controlGroup.addActor(spyLabel)

    controlGroup.setWidth(camera.viewportWidth)


    titleLabel.setPosition(controlGroup.getWidth/2,controlGroup.getHeight- titleLabel.getHeight)
    subjectLabel.setPosition(controlGroup.getWidth/2,controlGroup.getHeight- titleLabel.getHeight - subjectLabel.getHeight-30)
    subjectPicture.setPosition(subjectLabel.getX,subjectLabel.getY-subjectPicture.getHeight-10)
    meansLabel.setPosition(subjectPicture.getX(),subjectPicture.getY() - meansLabel.getHeight - 10)
    assetsGroup.setPosition(meansLabel.getX,meansLabel.getY - meansPicture.getHeight - 10)
    resultLabel.setPosition(assetsGroup.getX(),assetsGroup.getY - resultLabel.getHeight - 10)
    resultPicture.setPosition(resultLabel.getX,resultLabel.getY - resultPicture.getHeight - 10)
    applyButton.setPosition(resultPicture.getX,resultPicture.getY - applyButton.getHeight - 10)

    assetsLabel.setPosition(subjectLabel.getX-assetsLabel.getPrefWidth-20,subjectLabel.getY)

    val dragAndDrop = new DragAndDrop()

    soldierUnit.setPosition(assetsLabel.getX,assetsLabel.getY - soldierUnit.getPrefHeight-10)
    soldierLabel.setPosition(soldierUnit.getX,soldierUnit.getY - soldierLabel.getPrefHeight - 10)

    commandoUnit.setPosition(soldierLabel.getX,soldierLabel.getY - commandoUnit.getPrefHeight-10)
    commandoLabel.setPosition(commandoUnit.getX,commandoUnit.getY - commandoLabel.getPrefHeight - 10)

    spyUnit.setPosition(commandoLabel.getX,commandoLabel.getY - spyUnit.getPrefHeight-10)
    spyLabel.setPosition(spyUnit.getX,spyUnit.getY - spyLabel.getPrefHeight - 10)

    addUnitLeftClickListener(soldierUnit,assetsGroup,dragAndDrop,()=>{
      game.soldierCounter -=1
      updateUnitCountLabel("Soldier",soldierLabel,game.soldierCounter)
    })
    addUnitLeftClickListener(commandoUnit,assetsGroup,dragAndDrop,()=>{
      game.commandoCounter -=1
      updateUnitCountLabel("Commando",commandoLabel,game.commandoCounter)
    })
    addUnitLeftClickListener(spyUnit,assetsGroup,dragAndDrop,()=>{
      game.spyCounter -=1
      updateUnitCountLabel("Spy",spyLabel,game.spyCounter)
    })

    updateUnitCountLabel("Soldier",soldierLabel,game.soldierCounter)
    updateUnitCountLabel("Commando",commandoLabel,game.commandoCounter)
    updateUnitCountLabel("Spy",spyLabel,game.spyCounter)

    stage.getBatch.setProjectionMatrix(camera.combined)

    applyButton.addCaptureListener(new ClickListener() {
      override def clicked(event: InputEvent, x: Float, y: Float): Unit = {

        val outcome = analyzeOutcome(assetsGroup)
        outcome match {
          case ActionMixOutcome.EXPEDITION =>
            mapScreen.sendExpedition(destTile = new Vector2(targetLocation.mapCell.x,targetLocation.mapCell.y),units = scala.List[UnitType]())
            game.setScreen(mapScreen)
          case ActionMixOutcome.COMBAT=>
            val defendersType = targetLocation match {
              case _:RaiderCampInfo => DefendersType.RAIDERS
              case _:CityInfo => DefendersType.SOLDIERS
              case _=> throw new RuntimeException("Unknown current location type " + targetLocation)
            }
            val playerUnits:scala.List[UnitType] = scala.List[UnitType]()
            game.setScreen(new SituationScreen(targetLocation,defendersType,game,mapScreen,playerUnits))

          case ActionMixOutcome.SURVEILLANCE=>
            val dialog = new Dialog("Intelligence gathered", skin) {
              override def result(result:Object) {
                game.setScreen(mapScreen)
              }
            }

            dialog.text(s"Intelligence gathered")
            dialog.button("OK", true)
            dialog.key(Keys.ESCAPE, false).key(Keys.ENTER, true)
            dialog.pack()
            stage.addActor(dialog)
            val dialogPos = camera.unproject(new Vector3(camera.position.x,camera.position.y,0))
            dialog.setPosition(dialogPos.x - dialog.getPrefWidth/2,dialogPos.y - dialog.getPrefHeight/2)

          case ActionMixOutcome.UNKNOWN => {
            game.setScreen(mapScreen)
          }
        }


      }
    })


    stage.addActor(controlGroup)

    controlGroup.setPosition(0, camera.unproject(new Vector3(0,0,0)).y)


    setAssetDragDrop(soldierUnit,assetManager.get("unitConstruction/soldierUnit.png",classOf[Texture]),dragAndDrop,meansPicture)
    setAssetDragDrop(commandoUnit,assetManager.get("unitConstruction/commandoUnit.png",classOf[Texture]),dragAndDrop,meansPicture)
    setAssetDragDrop(spyUnit,assetManager.get("unitConstruction/spyUnit.png",classOf[Texture]),dragAndDrop,meansPicture)

    setTargetDragDrop(meansPicture,assetsGroup,dragAndDrop)
  }

  private def addUnitLeftClickListener(actor:Actor,assetsGroup:Group,dragAndDrop: DragAndDrop,callback:() => Unit): Unit ={
    actor.addCaptureListener(new ClickListener(Buttons.LEFT){

      override def clicked(event: InputEvent, x: Float, y: Float): Unit = {
        addActorToAssetGroup(actor, assetsGroup, dragAndDrop)
        callback()
      }
    })
  }


  private def setTargetDragDrop(target:Actor,assetGroup:Group,dragAndDrop: DragAndDrop): Unit ={
    dragAndDrop.addTarget(new Target(target) {
      def drag (source:Source, payload:Payload, x:Float, y:Float, pointer:Int):Boolean = {
        val size = assetGroup.getChildren.size
        for (i<-0 until size){
          assetGroup.getChildren.get(i).setColor(Color.GREEN)
        }
        true
      }

      override def reset (source:Source, payload:Payload) {
        val size = assetGroup.getChildren.size
        for (i<-0 until size){
          assetGroup.getChildren.get(i).setColor(Color.WHITE)
        }
      }



      def drop (source:Source, payload:Payload, x:Float, y:Float, pointer:Int): Unit = {
        addActorToAssetGroup(payload.getDragActor,assetGroup,dragAndDrop)
      }
    })
  }

  private def addActorToAssetGroup(sourceActor:Actor,assetGroup:Group,dragAndDrop: DragAndDrop): Unit ={
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
        if (button == Buttons.RIGHT){
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

    setTargetDragDrop(actor,assetGroup,dragAndDrop)

    if (means != null){
      actor.setX(means.getX)
      assetGroup.removeActor(means)
    }
  }

  private def analyzeOutcome(assetGroup:Group): ActionMixOutcome.MixOutcome ={
    var soldiersCommandoCounter = 0
    var spyCounter = 0

    val filteredAssetGroup = assetGroup.getChildren.toArray.filter(a=>a != meansPicture)
    val size = filteredAssetGroup.length
    for (i<-0 until size){
      val actor = filteredAssetGroup(i)
      if (actor.getUserObject != null) {
        val unitType = filteredAssetGroup(i).getUserObject.asInstanceOf[ActorUserObject].unitType
        unitType match {
          case UnitType.SOLDIER | UnitType.COMMANDO => soldiersCommandoCounter += 1
          case UnitType.SPY => spyCounter += 1
          case _ =>
        }
      }
    }

    if (targetLocation.isInstanceOf[CoveredAreaInfo] && size >0){
      ActionMixOutcome.EXPEDITION
    } else{ //target is discovered
        if (soldiersCommandoCounter > 0 && spyCounter == 0) {
          ActionMixOutcome.COMBAT
        } else if (spyCounter > 0 && soldiersCommandoCounter == 0) {
          ActionMixOutcome.SURVEILLANCE
        } else {
          ActionMixOutcome.UNKNOWN
        }
    }


  }


  private def updateResultPicture(assetGroup: Group): Unit={

    val texture:Texture =
      analyzeOutcome(assetGroup) match {
        case ActionMixOutcome.COMBAT=>crossedSwordsTexture
        case ActionMixOutcome.SURVEILLANCE=>keyHoleTexture
        case ActionMixOutcome.EXPEDITION => expeditionTexture
        case _=> questionMarkTexture
      }

    resultPicture.setDrawable(new SpriteDrawable(new Sprite(texture)))

  }

  private def setAssetDragDrop(source:Actor,assetTexture:Texture,dragAndDrop: DragAndDrop,targetActor:Actor): Unit ={
    val dragAndDropActor = new Image(assetTexture)
    dragAndDropActor.setUserObject(source.getUserObject)

    dragAndDrop.addSource(new Source(source) {
      def dragStart (event:InputEvent, x:Float, y:Float, pointer:Int) = {
        val payload = new Payload()
        payload.setDragActor(dragAndDropActor)
        dragAndDrop.setDragActorPosition(x, y - dragAndDropActor.getPrefHeight)
        payload
      }
    })

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

  override def dispose(): Unit = {
  }
}
