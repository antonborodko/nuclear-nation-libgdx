package com.anton.nuclearnation

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.{Gdx, Screen}
import com.badlogic.gdx.maps.tiled.{TiledMap, TiledMapTileLayer}
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell

class SituationScreen(game:NuclearNation) extends Screen{

  val assetManager = game.assetManager
  val map = new TiledMap
  val layers = map.getLayers

  val mapWidthTiles = 20
  val mapHeightTiles = 20

  val texture = new Texture(Gdx.files.internal("desert_tile.png"))
  val layer0 = new TiledMapTileLayer(mapHeightTiles, mapWidthTiles, texture.getWidth, texture.getHeight)
  val townLayer = new TiledMapTileLayer(mapHeightTiles, mapWidthTiles, texture.getWidth, texture.getHeight)
  val cell:Cell = new Cell

  val region = new TextureRegion(texture)


  override def show(): Unit = {}

  override def render(delta: Float): Unit = {}

  override def resize(width: Int, height: Int): Unit = {}

  override def pause(): Unit = {}

  override def resume(): Unit = {}

  override def hide(): Unit = {}

  override def dispose(): Unit = {}
}
