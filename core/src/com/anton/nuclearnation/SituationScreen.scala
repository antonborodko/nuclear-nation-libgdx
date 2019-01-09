package com.anton.nuclearnation

import com.anton.nuclearnation.DefendersType.DefendersType
import com.anton.nuclearnation.MapScreen.{CityInfo, MapLocation, RaiderCampInfo}
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.{Color, GL20, OrthographicCamera, Texture}
import com.badlogic.gdx.graphics.g2d.{Batch, BitmapFont, Sprite, TextureRegion}
import com.badlogic.gdx._
import com.badlogic.gdx.maps.tiled.{TiledMap, TiledMapTileLayer}
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile
import com.badlogic.gdx.math.{Vector2, Vector3}
import com.badlogic.gdx.scenes.scene2d._
import com.badlogic.gdx.scenes.scene2d.ui.{Dialog, Skin, Window}
import com.badlogic.gdx.scenes.scene2d.utils.{ClickListener, Drawable}
import com.badlogic.gdx.utils.viewport.{ScreenViewport, StretchViewport}

import scala.collection.mutable.ListBuffer


object DefendersType extends Enumeration {
  type DefendersType = Value
  val RAIDERS, SOLDIERS = Value
}


class SituationScreen(currentLocation:MapLocation, defendersType:DefendersType, game:NuclearNation, mapScreen:MapScreen) extends Screen{

  val assetManager = game.assetManager
  val map = new TiledMap
  val layers = map.getLayers

  val mapWidthTiles = 11
  val mapHeightTiles = 11

  val middleXTile = (mapWidthTiles -1) / 2 + 1
  val middleYTile = (mapHeightTiles -1) / 2 + 1

  val desertTileTexture = assetManager.get("desert_tile.png",classOf[Texture])
  val defendingRaiderTexture = assetManager.get("raider-facing-left.png",classOf[Texture])
  val attackingSoldierTexture = assetManager.get("soldier-facing-right.png",classOf[Texture])
  val defendingSoldierTexture = assetManager.get("soldier-facing-left.png",classOf[Texture])

  val mainLayer = new TiledMapTileLayer(mapHeightTiles, mapWidthTiles, desertTileTexture.getWidth, desertTileTexture.getHeight)
  val cell:Cell = new Cell

  val region = new TextureRegion(desertTileTexture)
  cell.setTile(new StaticTiledMapTile(region))

  for (
    x <- 0 until mapWidthTiles;
    y <- 0 until mapHeightTiles
  ) yield  {
    mainLayer.setCell(x, y, cell)
  }

  map.getLayers.add(mainLayer)

  val renderer = new OrthogonalTiledMapRenderer(map, 1f)

  val mapWidthPixels = (mainLayer.getWidth * mainLayer.getTileWidth).asInstanceOf[Int]
  val mapHeightPixels = (mainLayer.getHeight * mainLayer.getTileHeight).asInstanceOf[Int]
  val gameFont = assetManager.get("fonts/lunchtime-doubly-so/lunchds.ttf",classOf[BitmapFont])


  val stage = new Stage(new StretchViewport(800,600,new OrthographicCamera()))
  val camera = stage.getCamera

  case class EnemyInfo(tileX:Int, tileY:Int)
  val skin = assetManager.get("data/commodore64/skin/uiskin.json",classOf[Skin])

  val enemies = ListBuffer[EnemyInfo]()

  enemies += EnemyInfo(middleXTile+1,middleYTile)
  enemies += EnemyInfo(middleXTile+2,middleYTile-1)
  enemies += EnemyInfo(middleXTile+2,middleYTile+1)

  val situationScreenInputProcessor = new InputProcessor {
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
  multiplexer.addProcessor(situationScreenInputProcessor)

  Gdx.input.setInputProcessor(multiplexer)

  val texture = defendersType match {
    case DefendersType.SOLDIERS => defendingSoldierTexture
    case DefendersType.RAIDERS => defendingRaiderTexture
    case _ => throw new RuntimeException(s"Unknown defender type in situation screen: $defendersType")
  }


  override def show(): Unit = {

    enemies.foreach(raider=>{
      stage.addActor(new EnemyActor(raider.tileX,raider.tileY,texture))
    })

  }

  override def render(delta: Float): Unit = {
    Gdx.gl.glClearColor(1, 0, 0, 1)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT)
    stage.getBatch.setColor(Color.WHITE)

    camera.position.set(middleXTile * mainLayer.getTileWidth,middleYTile * mainLayer.getTileHeight,0)
    camera.update()
    stage.getBatch.setProjectionMatrix(camera.combined)
    renderer.setView(camera.asInstanceOf[OrthographicCamera])

    renderer.getBatch.begin()
    renderer.renderTileLayer(mainLayer)
    renderer.getBatch.end()


    //rendering raiders and soldiers
    stage.getBatch.begin()

    stage.getBatch.draw(attackingSoldierTexture,(middleXTile-2) * mainLayer.getTileWidth,(middleYTile+1) * mainLayer.getTileHeight,attackingSoldierTexture.getWidth,attackingSoldierTexture.getHeight)
    stage.getBatch.draw(attackingSoldierTexture,(middleXTile-1) * mainLayer.getTileWidth,middleYTile * mainLayer.getTileHeight,attackingSoldierTexture.getWidth,attackingSoldierTexture.getHeight)
    stage.getBatch.draw(attackingSoldierTexture,(middleXTile-2) * mainLayer.getTileWidth,(middleYTile-1) * mainLayer.getTileHeight,attackingSoldierTexture.getWidth,attackingSoldierTexture.getHeight)
    stage.getBatch.end()

    stage.act(delta)
    stage.draw()
  }


  override def resize(width: Int, height: Int): Unit = {
    if (stage != null) {
      stage.getViewport.update(width, height, false);
    }
  }

  override def pause(): Unit = {}

  override def resume(): Unit = {}

  override def hide(): Unit = {}

  override def dispose(): Unit = {
    renderer.dispose()
    map.dispose()
    stage.dispose()
  }


  class EnemyActor(tileX:Int, tileY:Int,texture:Texture) extends Actor {
    setBounds( tileX * mainLayer.getTileWidth,tileY * mainLayer.getTileHeight,texture.getWidth(),texture.getHeight())

    addListener(new InputListener(){
        override def touchDown (event:InputEvent, x:Float, y:Float, pointer:Int, button:Int):Boolean= {
          val dialog = new Dialog("You've defeated the enemy", skin) {
            override def result(result:Object) {
              Gdx.app.log("INFO","Button clicked " + result)
              currentLocation match{
                case city:CityInfo=> city.isOwnedByPlayer = true
                case camp:RaiderCampInfo=>
                  mapScreen.deleteCamp(camp)
              }
              game.setScreen(mapScreen)
            }
          }

          val notification = currentLocation match {
            case city:CityInfo => s"You've conquered city ${city.name}"
            case camp:RaiderCampInfo => s"You've destroyed raider camp ${camp.name}"
          }

          dialog.text(notification)
          dialog.button("OK", true)
          dialog.key(Keys.ESCAPE, false).key(Keys.ENTER, true)
          dialog.pack()
          stage.addActor(dialog)

          dialog.setPosition(100,100)

          true
        }
    })

    override def draw(batch: Batch, alpha: Float): Unit = {
      batch.draw(texture, tileX * mainLayer.getTileWidth,tileY * mainLayer.getTileHeight)
    }


  }
}
