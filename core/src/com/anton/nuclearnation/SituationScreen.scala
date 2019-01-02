package com.anton.nuclearnation

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

class SituationScreen(currentCamp: MapScreen.RaiderCampInfo, game:NuclearNation, mapScreen:MapScreen) extends Screen{

  val assetManager = game.assetManager
  val map = new TiledMap
  val layers = map.getLayers

  val mapWidthTiles = 11
  val mapHeightTiles = 11

  val middleXTile = (mapWidthTiles -1) / 2 + 1
  val middleYTile = (mapHeightTiles -1) / 2 + 1

  val desertTileTexture = assetManager.get("desert_tile.png",classOf[Texture])
  val raiderTexture = assetManager.get("raider-facing-left.png",classOf[Texture])
  val soldierTexture = assetManager.get("soldier-facing-right.png",classOf[Texture])

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

  case class RaiderInfo(texture:Texture,tileX:Int,tileY:Int)
  val skin = new Skin(Gdx.files.internal("data/commodore64/skin/uiskin.json"))

  val raiders = ListBuffer[RaiderInfo]()

  raiders += RaiderInfo(assetManager.get("raider-facing-left.png",classOf[Texture]),middleXTile+1,middleYTile)
  raiders += RaiderInfo(assetManager.get("raider-facing-left.png",classOf[Texture]),middleXTile+2,middleYTile-1)
  raiders += RaiderInfo(assetManager.get("raider-facing-left.png",classOf[Texture]),middleXTile+2,middleYTile+1)

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


  override def show(): Unit = {

    raiders.foreach(raider=>{
      stage.addActor(new RaiderActor(raider.tileX,raider.tileY))
    })

  }

  override def render(delta: Float): Unit = {
    Gdx.gl.glClearColor(1, 0, 0, 1)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT)
    stage.getBatch.setColor(Color.WHITE);

    camera.position.set(middleXTile * mainLayer.getTileWidth,middleYTile * mainLayer.getTileHeight,0)
    camera.update()
    stage.getBatch.setProjectionMatrix(camera.combined)
    renderer.setView(camera.asInstanceOf[OrthographicCamera])

    renderer.getBatch.begin()
    renderer.renderTileLayer(mainLayer)
    renderer.getBatch.end()


    //rendering raiders and soldiers
    stage.getBatch.begin()
//    raiders.foreach(raider=>{
//      stage.getBatch.draw(raider.texture,raider.tileX * mainLayer.getTileWidth,raider.tileY * mainLayer.getTileHeight,raiderTexture.getWidth,raiderTexture.getHeight)
//      stage.getBatch.draw(raider.texture,raider.tileX * mainLayer.getTileWidth,raider.tileY * mainLayer.getTileHeight,raiderTexture.getWidth,raiderTexture.getHeight)
//      stage.getBatch.draw(raider.texture,raider.tileX * mainLayer.getTileWidth,raider.tileY * mainLayer.getTileHeight,raiderTexture.getWidth,raiderTexture.getHeight)
//    })

    stage.getBatch.draw(soldierTexture,(middleXTile-2) * mainLayer.getTileWidth,(middleYTile+1) * mainLayer.getTileHeight,soldierTexture.getWidth,soldierTexture.getHeight)
    stage.getBatch.draw(soldierTexture,(middleXTile-1) * mainLayer.getTileWidth,middleYTile * mainLayer.getTileHeight,soldierTexture.getWidth,soldierTexture.getHeight)
    stage.getBatch.draw(soldierTexture,(middleXTile-2) * mainLayer.getTileWidth,(middleYTile-1) * mainLayer.getTileHeight,soldierTexture.getWidth,soldierTexture.getHeight)
    stage.getBatch.end()



//    game.batch.begin()
//    game.font.draw(game.batch,s"Test",camera.unproject(new Vector3(0,0,0)).x,camera.position.y)
//    game.batch.end()

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
    skin.dispose()
  }

  import com.badlogic.gdx.Gdx
  import com.badlogic.gdx.graphics.Texture
  import com.badlogic.gdx.graphics.g2d.Batch
  import com.badlogic.gdx.scenes.scene2d.Actor

  class RaiderActor(tileX:Int,tileY:Int) extends Actor {
    val texture = new Texture(Gdx.files.internal("raider-facing-left.png"))

    setBounds( tileX * mainLayer.getTileWidth,tileY * mainLayer.getTileHeight,texture.getWidth(),texture.getHeight())

    addListener(new InputListener(){
        override def touchDown (event:InputEvent, x:Float, y:Float, pointer:Int, button:Int):Boolean= {
          val dialog = new Dialog("You've defeated the raiders", skin) {
            override def result(result:Object) {
              Gdx.app.log("INFO","Button clicked " + result)
              dispose()
              mapScreen.deleteCamp(currentCamp)
              game.setScreen(mapScreen)
            }
          }

          dialog.text("You've defeated the raiders")
          dialog.button("OK", true)
          dialog.key(Keys.ESCAPE, false).key(Keys.ENTER, true)
          dialog.setSize(500,200)
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
