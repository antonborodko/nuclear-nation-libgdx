package com.anton.nuclearnation

import com.badlogic.gdx.graphics.{GL20, OrthographicCamera, Texture}
import com.badlogic.gdx.graphics.g2d.{BitmapFont, TextureRegion}
import com.badlogic.gdx.{Gdx, Screen}
import com.badlogic.gdx.maps.tiled.{TiledMap, TiledMapTileLayer}
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile
import com.badlogic.gdx.math.Vector3

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
  camera.setToOrtho(false, 1600, 960)
  camera.update()



  override def show(): Unit = {
  }

  override def render(delta: Float): Unit = {
    camera.position.set(camera.viewportWidth/2,camera.viewportHeight/2,0)
    camera.update()
    game.batch.setProjectionMatrix(camera.combined)
    renderer.setView(camera)

    renderer.getBatch.begin()
    renderer.renderTileLayer(mainLayer)
    renderer.getBatch.end()

    //rendering raiders and soldiers
    game.batch.begin()
    game.batch.draw(raiderTexture,(middleXTile+2) * mainLayer.getTileWidth,(middleYTile +1 ) * mainLayer.getTileHeight,raiderTexture.getWidth,raiderTexture.getHeight)
    game.batch.draw(raiderTexture,(middleXTile+1) * mainLayer.getTileWidth,middleYTile * mainLayer.getTileHeight,raiderTexture.getWidth,raiderTexture.getHeight)
    game.batch.draw(raiderTexture,(middleXTile+2) * mainLayer.getTileWidth,(middleYTile-1) * mainLayer.getTileHeight,raiderTexture.getWidth,raiderTexture.getHeight)
    game.batch.draw(soldierTexture,(middleXTile-2) * mainLayer.getTileWidth,(middleYTile+1) * mainLayer.getTileHeight,soldierTexture.getWidth,soldierTexture.getHeight)
    game.batch.draw(soldierTexture,(middleXTile-1) * mainLayer.getTileWidth,middleYTile * mainLayer.getTileHeight,soldierTexture.getWidth,soldierTexture.getHeight)
    game.batch.draw(soldierTexture,(middleXTile-2) * mainLayer.getTileWidth,(middleYTile-1) * mainLayer.getTileHeight,soldierTexture.getWidth,soldierTexture.getHeight)
    game.batch.end()

//    game.batch.begin()
//    game.font.draw(game.batch,s"Test",camera.unproject(new Vector3(0,0,0)).x,camera.position.y)
//    game.batch.end()
  }

  override def resize(width: Int, height: Int): Unit = {}

  override def pause(): Unit = {}

  override def resume(): Unit = {}

  override def hide(): Unit = {}

  override def dispose(): Unit = {
    renderer.dispose()
    map.dispose()
  }
}
