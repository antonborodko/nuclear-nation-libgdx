package com.anton.nuclearnation

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx._
import com.badlogic.gdx.graphics.{Color, GL20, OrthographicCamera, Texture}
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.{Actor, Group, InputEvent, Stage}
import com.badlogic.gdx.scenes.scene2d.ui._
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.{Payload, Source, Target}
import com.badlogic.gdx.scenes.scene2d.utils.{ClickListener, DragAndDrop, SpriteDrawable, TextureRegionDrawable}
import com.badlogic.gdx.utils.viewport.StretchViewport

import scala.collection.JavaConverters

class ActionMixScreen(game:NuclearNation,mapScreen: MapScreen) extends Screen{

  val stage = new Stage(new StretchViewport(1600,960,new OrthographicCamera()))
  val camera = stage.getCamera.asInstanceOf[OrthographicCamera]

  val assetManager = game.assetManager

  val skin = assetManager.get("data/commodore64/skin/uiskin.json",classOf[Skin])

  val subjectPicture = new Image(assetManager.get("raider_camp.png",classOf[Texture]))

  val meansPicture = new Image(assetManager.get("question-mark.png",classOf[Texture]))
  meansPicture.setName("means")

  val resultPicture = new Image(assetManager.get("question-mark.png",classOf[Texture]))


  val crossedSwordsPicture = new Image(assetManager.get("crossed-swords.png",classOf[Texture]))

  val soldierPicture = new Image(assetManager.get("unitConstruction/soldierUnit.png",classOf[Texture]))
  val commandoPicture = new Image(assetManager.get("unitConstruction/commandoUnit.png",classOf[Texture]))
  val spyPicture = new Image(assetManager.get("unitConstruction/spyUnit.png",classOf[Texture]))

  val soldierLabel = new Label("Soldier",skin)
  val commandoLabel = new Label("Commando",skin)
  val spyLabel = new Label("Spy",skin)

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
    val titleLabel = new Label("CREATE ACTION MIX",skin)
    val subjectLabel = new Label("Subject",skin)
    val meansLabel = new Label("Means",skin)
    val resultLabel = new Label("Result", skin)

    val assetsLabel = new Label("AVAILABLE ASSETS:",skin)

    val applyButton = new TextButton("Apply mix",skin)

    applyButton.addCaptureListener(new ClickListener() {
      override def clicked(event: InputEvent, x: Float, y: Float): Unit = {
        game.setScreen(mapScreen)
        dispose()
      }
    })

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
    controlGroup.addActor(soldierPicture)
    controlGroup.addActor(soldierLabel)
    controlGroup.addActor(commandoPicture)
    controlGroup.addActor(commandoLabel)
    controlGroup.addActor(spyPicture)
    controlGroup.addActor(spyLabel)

    controlGroup.setWidth(camera.viewportWidth)


    titleLabel.setPosition(controlGroup.getWidth/2,controlGroup.getHeight- titleLabel.getHeight)
    subjectLabel.setPosition(controlGroup.getWidth/2,controlGroup.getHeight- titleLabel.getHeight - subjectLabel.getHeight-30)
    subjectPicture.setPosition(subjectLabel.getX,subjectLabel.getY-subjectPicture.getHeight-10)
    meansLabel.setPosition(subjectPicture.getX(),subjectPicture.getY() - meansLabel.getHeight - 10)
    meansPicture.setPosition(meansLabel.getX,meansLabel.getY - meansPicture.getHeight - 10)
    resultLabel.setPosition(meansPicture.getX(),meansPicture.getY - resultLabel.getHeight - 10)
    resultPicture.setPosition(resultLabel.getX,resultLabel.getY - resultPicture.getHeight - 10)
    applyButton.setPosition(resultPicture.getX,resultPicture.getY - applyButton.getHeight - 10)

    assetsLabel.setPosition(100,subjectLabel.getY)

    soldierPicture.setPosition(assetsLabel.getX,assetsLabel.getY - soldierPicture.getPrefHeight-10)
    soldierLabel.setPosition(soldierPicture.getX,soldierPicture.getY - soldierLabel.getPrefHeight - 10)

    commandoPicture.setPosition(soldierLabel.getX,soldierLabel.getY - commandoPicture.getPrefHeight-10)
    commandoLabel.setPosition(commandoPicture.getX,commandoPicture.getY - commandoLabel.getPrefHeight - 10)

    spyPicture.setPosition(commandoLabel.getX,commandoLabel.getY - spyPicture.getPrefHeight-10)
    spyLabel.setPosition(spyPicture.getX,spyPicture.getY - spyLabel.getPrefHeight - 10)


    stage.addActor(controlGroup)

    controlGroup.setPosition(0, camera.unproject(new Vector3(0,0,0)).y)


    //adding drag and drop functionality
    val dragAndDrop = new DragAndDrop()

    setAssetDragDrop(soldierPicture,assetManager.get("unitConstruction/soldierUnit.png",classOf[Texture]),dragAndDrop,meansPicture)
    setAssetDragDrop(commandoPicture,assetManager.get("unitConstruction/commandoUnit.png",classOf[Texture]),dragAndDrop,meansPicture)
    setAssetDragDrop(spyPicture,assetManager.get("unitConstruction/spyUnit.png",classOf[Texture]),dragAndDrop,meansPicture)

    setTargetDragDrop(meansPicture,assetsGroup,dragAndDrop)

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
        val means = assetGroup.findActor[Image]("means")
        val size = assetGroup.getChildren.size
        val furthestRightActor = if (means == null) assetGroup.getChildren.get(size-1) else means
        val actor = new Image(payload.getDragActor.asInstanceOf[Image].getDrawable.asInstanceOf[TextureRegionDrawable].getRegion.getTexture)
        actor.setPosition(furthestRightActor.getX + actor.getPrefWidth + 5,furthestRightActor.getY)
        assetGroup.addActor(actor)

        setTargetDragDrop(actor,assetGroup,dragAndDrop)

        if (means != null){
          actor.setX(means.getX)
          assetGroup.removeActor(means)
        }
      }
    })
  }

  private def setAssetDragDrop(source:Actor,assetTexture:Texture,dragAndDrop: DragAndDrop,targetActor:Actor): Unit ={
    val dragAndDropImage = new Image(assetTexture)
    dragAndDrop.addSource(new Source(source) {
      def dragStart (event:InputEvent, x:Float, y:Float, pointer:Int) = {
        val payload = new Payload()
        payload.setObject("Some payload!")

        payload.setDragActor(dragAndDropImage)

        payload.setValidDragActor(dragAndDropImage)

        payload.setInvalidDragActor(dragAndDropImage)
        dragAndDrop.setDragActorPosition(x, y - dragAndDropImage.getPrefHeight)

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

  override def hide(): Unit = {}

  override def dispose(): Unit = {}
}
