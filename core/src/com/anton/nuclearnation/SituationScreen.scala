package com.anton.nuclearnation

import com.badlogic.gdx.graphics.{GL20, OrthographicCamera, Texture}
import com.badlogic.gdx.graphics.g2d.{BitmapFont, Sprite, TextureRegion}
import com.badlogic.gdx.{Gdx, Input, InputProcessor, Screen}
import com.badlogic.gdx.maps.tiled.{TiledMap, TiledMapTileLayer}
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile
import com.badlogic.gdx.math.{Vector2, Vector3}
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport

import scala.collection.mutable.ListBuffer

class SituationScreen(game:NuclearNation) extends Screen{

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

  val camera = new OrthographicCamera()
  camera.setToOrtho(false, 800, 480)
  camera.update()

  val stage = new Stage(new ScreenViewport(camera))
  stage.getViewport.update(800,480)

  case class RaiderInfo(texture:Texture,tileX:Int,tileY:Int)

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

    override def touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = {
      if (button == Input.Buttons.LEFT) {
        val touchPos = camera.unproject(new Vector3(screenX,screenY,0))
        raiders.foreach(raider=>{
          val texture = raider.texture
          val x = raider.tileX * mainLayer.getTileWidth
          val y = raider.tileY * mainLayer.getTileHeight
          if (touchPos.x > x && touchPos.x < x + texture.getWidth) {
            if (touchPos.y > y && touchPos.y < y + texture.getHeight) {

            }
          }
        })
        return true
      }
      false
    }
  }

  Gdx.input.setInputProcessor(situationScreenInputProcessor)


  override def show(): Unit = {
  }

  override def render(delta: Float): Unit = {

    Gdx.gl.glClearColor(1, 0, 0, 1)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT)

    camera.position.set(middleXTile * mainLayer.getTileWidth,middleYTile * mainLayer.getTileHeight,0)
    camera.update()
    game.batch.setProjectionMatrix(camera.combined)
    renderer.setView(camera)

    renderer.getBatch.begin()
    renderer.renderTileLayer(mainLayer)
    renderer.getBatch.end()

    //rendering raiders and soldiers
    game.batch.begin()
    raiders.foreach(raider=>{
      game.batch.draw(raider.texture,raider.tileX * mainLayer.getTileWidth,raider.tileY * mainLayer.getTileHeight,raiderTexture.getWidth,raiderTexture.getHeight)
      game.batch.draw(raider.texture,raider.tileX * mainLayer.getTileWidth,raider.tileY * mainLayer.getTileHeight,raiderTexture.getWidth,raiderTexture.getHeight)
      game.batch.draw(raider.texture,raider.tileX * mainLayer.getTileWidth,raider.tileY * mainLayer.getTileHeight,raiderTexture.getWidth,raiderTexture.getHeight)
    })

    game.batch.draw(soldierTexture,(middleXTile-2) * mainLayer.getTileWidth,(middleYTile+1) * mainLayer.getTileHeight,soldierTexture.getWidth,soldierTexture.getHeight)
    game.batch.draw(soldierTexture,(middleXTile-1) * mainLayer.getTileWidth,middleYTile * mainLayer.getTileHeight,soldierTexture.getWidth,soldierTexture.getHeight)
    game.batch.draw(soldierTexture,(middleXTile-2) * mainLayer.getTileWidth,(middleYTile-1) * mainLayer.getTileHeight,soldierTexture.getWidth,soldierTexture.getHeight)
    game.batch.end()

//    game.batch.begin()
//    game.font.draw(game.batch,s"Test",camera.unproject(new Vector3(0,0,0)).x,camera.position.y)
//    game.batch.end()

    stage.act(delta)
    stage.draw()
  }

  override def resize(width: Int, height: Int): Unit = {}

  override def pause(): Unit = {}

  override def resume(): Unit = {}

  override def hide(): Unit = {}

  override def dispose(): Unit = {
    renderer.dispose()
    map.dispose()
    stage.dispose()
  }
}
