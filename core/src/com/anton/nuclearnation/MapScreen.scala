package com.anton.nuclearnation

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.{Gdx, Screen}
import com.badlogic.gdx.graphics.{Camera, Texture}
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.MapLayers
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile
import com.badlogic.gdx.math.Vector3

class MapScreen(game: NuclearNation) extends Screen{


  import com.badlogic.gdx.graphics.OrthographicCamera



  val map = new TiledMap
  val layers = map.getLayers

  val mapWidthTiles = 10
  val mapHeightTiles = 10

  val texture = new Texture(Gdx.files.internal("desert_tile.png"))
  val layer0 = new TiledMapTileLayer(mapHeightTiles, mapWidthTiles, texture.getWidth, texture.getHeight)
  val cell:Cell = new Cell

  val region = new TextureRegion(texture)

  val dropImage = new Texture(Gdx.files.internal("droplet.png"))

  cell.setTile(new StaticTiledMapTile(region))

  import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer

  private val camera = new OrthographicCamera()
  camera.setToOrtho(false, 800, 480)
  camera.update()



  for (
    x <- 0 until mapWidthTiles;
    y <- 0 until mapHeightTiles
  ) yield  {
    layer0.setCell(x, y, cell)
  }
  map.getLayers.add(layer0)

  println("Map generated")

  val renderer = new OrthogonalTiledMapRenderer(map, 1f)

  val mainLayer = map.getLayers.get(0).asInstanceOf[TiledMapTileLayer]
  val mapWidthPixels = (mainLayer.getWidth * mainLayer.getTileWidth).asInstanceOf[Int]
  val mapHeightPixels = (mainLayer.getHeight * mainLayer.getTileHeight).asInstanceOf[Int]

  var dropImagePosX = mapWidthPixels/2 - dropImage.getWidth / 2
  var dropImagePosY = mapHeightPixels /2 - dropImage.getHeight /2


  override def show(): Unit = {}

  override def render(delta: Float): Unit = {


    if (Gdx.input.isKeyPressed(Keys.UP)){
      dropImagePosY+=10
      if (dropImagePosY + dropImage.getHeight > mapHeightPixels){
        dropImagePosY = mapHeightPixels - dropImage.getHeight
      }
    }

    if (Gdx.input.isKeyPressed(Keys.DOWN)){
      dropImagePosY-=10
      if (dropImagePosY <0) {
        dropImagePosY = 0
      }
    }

    if (Gdx.input.isKeyPressed(Keys.LEFT)){
      dropImagePosX-=10
      if (dropImagePosX <0){
        dropImagePosX = 0
      }
    }

    if (Gdx.input.isKeyPressed(Keys.RIGHT)){
      dropImagePosX+=10
      if (dropImagePosX + dropImage.getWidth > mapWidthPixels){
        dropImagePosX = mapWidthPixels - dropImage.getWidth
      }
    }

    setCameraPosition(camera,dropImagePosX,dropImagePosY)


  }

  private def setCameraPosition(camera: OrthographicCamera,playerPosX: Float, playerPosY: Float): Unit ={

    var cameraX = 0f
    var cameraY = 0f

    if (dropImagePosX - camera.viewportWidth /2 < camera.viewportWidth /2){
      if (dropImagePosX < camera.viewportWidth /2){
        cameraX =  camera.viewportWidth /2
      } else {
        cameraX = dropImagePosX
      }

    }  else if (dropImagePosX + camera.viewportWidth /2 > mapWidthPixels){
       cameraX = mapWidthPixels - camera.viewportWidth /2
    } else {
        cameraX = dropImagePosX
    }

    if (dropImagePosY - camera.viewportHeight /2 < camera.viewportHeight /2){
      if (dropImagePosY <  camera.viewportHeight /2) {
        cameraY = camera.viewportHeight / 2
      } else {
        cameraY = dropImagePosY
      }
    }  else if (dropImagePosY + camera.viewportHeight /2 > mapHeightPixels){
        cameraY = mapHeightPixels - camera.viewportHeight /2
    } else {
      cameraY = dropImagePosY
    }



    camera.position.set(cameraX,cameraY,0)
    camera.update()
    renderer.setView(camera)
    renderer.render()

    game.batch.begin()
    game.batch.setProjectionMatrix(camera.combined)
    game.font.draw(game.batch,s"Camera position: ($cameraX,$cameraY), camera viewport width: ${camera.viewportWidth} ,player position: ($dropImagePosX,$dropImagePosY)",camera.unproject(new Vector3(0,0,0)).x,camera.position.y)
    game.batch.draw(dropImage, dropImagePosX, dropImagePosY , dropImage.getWidth, dropImage.getHeight)
    game.batch.end()
  }

  override def resize(width: Int, height: Int): Unit = {}

  override def pause(): Unit = {}

  override def resume(): Unit = {}

  override def hide(): Unit = {}

  override def dispose(): Unit = {
    map.dispose()
    texture.dispose()
    renderer.dispose()
  }
}
