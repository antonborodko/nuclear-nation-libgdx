package com.anton.nuclearnation

import java.lang.Math

import com.anton.nuclearnation.MapScreen.{CityInfo, ExpeditionInfo, MapClickInfo, RaiderCampInfo}
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.{Gdx, Input, InputProcessor, Screen}
import com.badlogic.gdx.graphics.{Camera, Color, OrthographicCamera, Texture}
import com.badlogic.gdx.graphics.g2d.{BitmapFont, TextureRegion}
import com.badlogic.gdx.graphics.g2d.freetype.{FreeTypeFontGenerator, FreeTypeFontGeneratorLoader, FreetypeFontLoader}
import com.badlogic.gdx.maps.MapLayers
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile
import com.badlogic.gdx.math.{Vector2, Vector3}

import scala.util.Random
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter
import com.badlogic.gdx.maps.tiled.renderers.{IsometricStaggeredTiledMapRenderer, IsometricTiledMapRenderer, OrthogonalTiledMapRenderer}
import com.badlogic.gdx.scenes.scene2d.ui.Dialog

import scala.collection.mutable.ListBuffer

class MapScreen(game: NuclearNation) extends Screen{



  val assetManager = game.assetManager

  val map = new TiledMap
  val layers = map.getLayers

  val mapWidthTiles = 20
  val mapHeightTiles = 20

  val desertTileTexture = assetManager.get("desert_tile.png",classOf[Texture])
  val layer0 = new TiledMapTileLayer(mapHeightTiles, mapWidthTiles, desertTileTexture.getWidth, desertTileTexture.getHeight)
  val townLayer = new TiledMapTileLayer(mapHeightTiles, mapWidthTiles, desertTileTexture.getWidth, desertTileTexture.getHeight)
  val cell:Cell = new Cell

  val region = new TextureRegion(desertTileTexture)

  val dropImage = assetManager.get("droplet.png",classOf[Texture])
  val raiderCampImage = assetManager.get("raider_camp.png",classOf[Texture])

  val townImage = assetManager.get("town.png",classOf[Texture])

  cell.setTile(new StaticTiledMapTile(region))

  private val camera = new OrthographicCamera()
  camera.setToOrtho(false, 1600, 960)
  camera.update()



  val expeditions = ListBuffer[ExpeditionInfo]()

  val mapInputProcessor = new InputProcessor() {

    override def touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = {
      if (button == Input.Buttons.RIGHT) {
        mapRightClicked(screenX,screenY)
        return true
      }
      false
    }

    override def keyDown(keycode: Int): Boolean = {true}

    override def keyUp(keycode: Int): Boolean = {true}

    override def keyTyped(character: Char): Boolean = {true}

    override def touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = {true}

    override def touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = {true}

    override def mouseMoved(screenX: Int, screenY: Int): Boolean = {true}

    override def scrolled(amount: Int): Boolean = {true}
  }




  val cities = List[CityInfo](
    CityInfo("Hope",Random.nextInt(mapWidthTiles),Random.nextInt(mapHeightTiles)),
    CityInfo("New Reno",Random.nextInt(mapWidthTiles),Random.nextInt(mapHeightTiles)),
    CityInfo("Modoc",Random.nextInt(mapWidthTiles),Random.nextInt(mapHeightTiles))
  )

  val raiderCamps = ListBuffer[RaiderCampInfo](
    RaiderCampInfo("Mad dogs",Random.nextInt(mapWidthTiles),Random.nextInt(mapHeightTiles))
  )



  for (
    x <- 0 until mapWidthTiles;
    y <- 0 until mapHeightTiles
  ) yield  {
    layer0.setCell(x, y, cell)

    cities.foreach(cityInfo=>{
      if (x == cityInfo.x && y == cityInfo.y){
        val townRegion = new TextureRegion(townImage)
        val townTile = new StaticTiledMapTile(townRegion)
        val townCell = new Cell
        townCell.setTile(townTile)
        townLayer.setCell(x,y,townCell)
      }
    })

    raiderCamps.foreach(raiderCampInfo=>{
      if (x == raiderCampInfo.tileX && y == raiderCampInfo.tileY){
        val campRegion = new TextureRegion(raiderCampImage)
        val raiderTile = new StaticTiledMapTile(campRegion)
        val raiderCell = new Cell
        raiderCell.setTile(raiderTile)
        townLayer.setCell(x,y,raiderCell)
      }
    })



  }
  map.getLayers.add(townLayer)
  map.getLayers.add(layer0)


  println("Map generated")

  val renderer = new OrthogonalTiledMapRenderer(map, 1f)

  val mainLayer = map.getLayers.get(0).asInstanceOf[TiledMapTileLayer]
  val mapWidthPixels = (mainLayer.getWidth * mainLayer.getTileWidth).asInstanceOf[Int]
  val mapHeightPixels = (mainLayer.getHeight * mainLayer.getTileHeight).asInstanceOf[Int]

  var dropImagePosX = mapWidthPixels/2 - dropImage.getWidth / 2
  var dropImagePosY = mapHeightPixels /2 - dropImage.getHeight /2



  val gameFont = assetManager.get("fonts/lunchtime-doubly-so/lunchds.ttf",classOf[BitmapFont])


  override def show(): Unit = {
    Gdx.input.setInputProcessor(mapInputProcessor)
  }

  override def render(delta: Float): Unit = {

    if (Gdx.input.isKeyPressed(Keys.UP)){
      dropImagePosY+=25
      if (dropImagePosY + dropImage.getHeight > mapHeightPixels){
        dropImagePosY = mapHeightPixels - dropImage.getHeight
      }
    }

    if (Gdx.input.isKeyPressed(Keys.DOWN)){
      dropImagePosY-=25
      if (dropImagePosY <0) {
        dropImagePosY = 0
      }
    }

    if (Gdx.input.isKeyPressed(Keys.LEFT)){
      dropImagePosX-=25
      if (dropImagePosX <0){
        dropImagePosX = 0
      }
    }

    if (Gdx.input.isKeyPressed(Keys.RIGHT)){
      dropImagePosX+=25
      if (dropImagePosX + dropImage.getWidth > mapWidthPixels){
        dropImagePosX = mapWidthPixels - dropImage.getWidth
      }
    }


    setCameraPosition(camera,dropImagePosX,dropImagePosY,delta)


  }

  private def setCameraPosition(camera: OrthographicCamera,playerPosX: Float, playerPosY: Float,delta: Float): Unit ={

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

    renderer.getBatch.begin()
    renderer.renderTileLayer(layer0)
    renderer.renderTileLayer(townLayer)
    renderer.getBatch.end()

    game.batch.begin()
    game.batch.setProjectionMatrix(camera.combined)

    expeditions.foreach(expedition => {

      if(expedition.positionGlobalPixelX != expedition.destinationGlobalPixelX || expedition.positionGlobalPixelY !=expedition.destinationGlobalPixelY) {

        val currentPos = new Vector2(expedition.positionGlobalPixelX,expedition.positionGlobalPixelY)
        val destination = new Vector2(expedition.destinationGlobalPixelX,expedition.destinationGlobalPixelY)

        val direction = destination.sub(currentPos).nor()

        if (expedition.originalDirection.isDefined && !direction.hasSameDirection(expedition.originalDirection.get)){
          val tileX = (expedition.positionGlobalPixelX / mainLayer.getTileWidth).toInt
          val tileY = (expedition.positionGlobalPixelY / mainLayer.getTileHeight).toInt
          checkExpeditionTile(tileX,tileY,expeditions.head)
          expeditions.remove(0)
        } else {

          val newOriginX = expedition.positionGlobalPixelX + direction.x * 150 * delta
          val newOriginY = expedition.positionGlobalPixelY + direction.y * 150 * delta

          expeditions(0) = expedition.copy(positionGlobalPixelX = newOriginX, positionGlobalPixelY = newOriginY,originalDirection = Some(direction))
          game.batch.draw(expedition.marker, expeditions.head.positionGlobalPixelX - expedition.marker.getWidth/2, expeditions.head.positionGlobalPixelY - expedition.marker.getHeight/2)

        }


      }
    })

    //drawing cities names
    cities.foreach(city=>{
      val cityPixelX : Int = (city.x * mainLayer.getTileWidth).asInstanceOf[Int]
      val cityPixelY : Int = (city.y * mainLayer.getTileHeight).asInstanceOf[Int]
      gameFont.draw(game.batch,city.name,cityPixelX,cityPixelY)
    })

    //drawing raider camps names
    raiderCamps.foreach(raiderCampInfo=>{
      val campPixelX : Int = (raiderCampInfo.tileX * mainLayer.getTileWidth).asInstanceOf[Int]
      val campPixelY : Int = (raiderCampInfo.tileY * mainLayer.getTileHeight).asInstanceOf[Int]
      gameFont.draw(game.batch,s"${raiderCampInfo.name} (${raiderCampInfo.tileX},${raiderCampInfo.tileY})",campPixelX,campPixelY)
    })

    gameFont.draw(game.batch,s"Camera position: ($cameraX,$cameraY), camera viewport width: ${camera.viewportWidth} ,player position: ($dropImagePosX,$dropImagePosY)",camera.unproject(new Vector3(0,0,0)).x,camera.position.y)
    game.batch.draw(dropImage, dropImagePosX, dropImagePosY , dropImage.getWidth, dropImage.getHeight)
    game.batch.end()
  }

  override def resize(width: Int, height: Int): Unit = {}

  override def pause(): Unit = {}

  override def resume(): Unit = {}

  override def hide(): Unit = {
  }

  override def dispose(): Unit = {
    map.dispose()
    renderer.dispose()
  }

  private def getClickInfo(cameraXPixel:Float,cameraYPixel:Float):MapClickInfo = {
    val coordX = camera.unproject(new Vector3(cameraXPixel,0,0)).x
    val coordY = camera.unproject(new Vector3(0,cameraYPixel,0)).y
    val clickedTileX = (coordX / mainLayer.getTileWidth).toInt
    val clickedTileY = (coordY / mainLayer.getTileHeight).toInt
    MapClickInfo(coordX,coordY,clickedTileX,clickedTileY)
  }

  private def mapRightClicked(screenX: Int, screenY: Int) = {
    if (expeditions.size<1) {
      val clickInfo = getClickInfo(screenX,screenY)
      val texture = assetManager.get("droplet.png",classOf[Texture])
      expeditions += ExpeditionInfo(dropImagePosX,dropImagePosY,dropImagePosX,dropImagePosY,clickInfo.pixelX, clickInfo.pixelY,texture,None)
    } else {
      Gdx.app.log("INFO","Expedition already sent")
    }
  }

  private def checkExpeditionTile(tileX:Int,tileY:Int,expedition:ExpeditionInfo): Unit ={
    raiderCamps.foreach(camp=>{
      if (camp.tileX == tileX && camp.tileY == tileY){
        game.setScreen(new SituationScreen(camp,game,this))
        val newExpedition = expedition.copy(
          expedition.destinationGlobalPixelX,
          expedition.destinationGlobalPixelY,
          expedition.destinationGlobalPixelX,
          expedition.destinationGlobalPixelY,
          expedition.originGlobalPixelX,
          expedition.originGlobalPixelY,
          expedition.marker,
          None
        )

        expeditions += newExpedition
      }
    })
  }

  def deleteCamp(camp: RaiderCampInfo) = {
    if (raiderCamps.contains(camp)){
      raiderCamps -= camp
      val desertTile = new StaticTiledMapTile(new TextureRegion(desertTileTexture))
      val desertCell = new Cell
      desertCell.setTile(desertTile)
      townLayer.setCell(camp.tileX,camp.tileY,desertCell)
    }
  }



}

object MapScreen{
  case class CityInfo(name:String,x:Int,y:Int)
  case class RaiderCampInfo(name:String,tileX:Int,tileY:Int)
  case class MapClickInfo(pixelX:Float, pixelY: Float, tileX:Int,tileY:Int)
  case class ExpeditionInfo(originGlobalPixelX:Float,
                            originGlobalPixelY:Float,
                            positionGlobalPixelX:Float,
                            positionGlobalPixelY:Float,
                            destinationGlobalPixelX:Float,
                            destinationGlobalPixelY:Float,
                            marker:Texture,
                            originalDirection:Option[Vector2]
                           )
}
