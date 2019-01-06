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
  val desertLayer = new TiledMapTileLayer(mapHeightTiles, mapWidthTiles, desertTileTexture.getWidth, desertTileTexture.getHeight)
  val townLayer = new TiledMapTileLayer(mapHeightTiles, mapWidthTiles, desertTileTexture.getWidth, desertTileTexture.getHeight)
  val fogOfWarLayer = new TiledMapTileLayer(mapHeightTiles, mapWidthTiles, desertTileTexture.getWidth, desertTileTexture.getHeight)
  val desertTileCell:Cell = new Cell
  val fogOfWarCell = new Cell

  val region = new TextureRegion(desertTileTexture)

  val raiderCampImage = assetManager.get("raider_camp.png",classOf[Texture])
  val fogOfWarTexture = assetManager.get("fog_of_war_tile.png",classOf[Texture])
  val townImage = assetManager.get("town.png",classOf[Texture])

  desertTileCell.setTile(new StaticTiledMapTile(region))
  fogOfWarCell.setTile(new StaticTiledMapTile(new TextureRegion(fogOfWarTexture)))

  private val camera = new OrthographicCamera()
  camera.setToOrtho(false, 1600, 960)
  camera.update()

  val expeditions = ListBuffer[ExpeditionInfo]()

  val coordsGenerator = new MapCoordsGenerator(mapWidthTiles,mapHeightTiles,3)

  val mapData = new MapData(mapWidthTiles,mapHeightTiles)

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

  val citiesData = ListBuffer[CityInfo]()
  val raiderCampsData = ListBuffer[RaiderCampInfo]()

  val cityNames = List[String]("Hope","New Reno","Modoc","Arroyo")
  val raiderCampsNames = List[String]("Mad Dogs","Knives","Jokers")

  cityNames.foreach(cityName=> {
    val coords = coordsGenerator.getCoords
    citiesData += CityInfo(cityName, coords._1, coords._2)
    if (cityName.toLowerCase() == "hope"){
      mapData.cells.find(cell=>cell.x == coords._1 && cell.y == coords._2).get.state = MapCellState.DISCOVERED
    }
    Gdx.app.log("INFO",s"Generated city $cityName at coords $coords")
  })

  raiderCampsNames.foreach(raiderCampName=> {
    val coords = coordsGenerator.getCoords
    raiderCampsData += RaiderCampInfo(raiderCampName, coords._1, coords._2)
    Gdx.app.log("INFO",s"Generated raider camp $raiderCampName at coords $coords")
  })


  for (
    x <- 0 until mapWidthTiles;
    y <- 0 until mapHeightTiles
  ) yield  {
    desertLayer.setCell(x, y, desertTileCell)


    citiesData.foreach(cityInfo=>{
      if (x == cityInfo.x && y == cityInfo.y){
        val townRegion = new TextureRegion(townImage)
        val townTile = new StaticTiledMapTile(townRegion)
        val townCell = new Cell
        townCell.setTile(townTile)
        townLayer.setCell(x,y,townCell)
      }
    })

    if (mapData.cells.find(cell=>cell.x == x && cell.y == y).get.state == MapCellState.UNDISCOVERED){
      fogOfWarLayer.setCell(x,y,fogOfWarCell)
    }

    raiderCampsData.foreach(raiderCampInfo=>{
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
  map.getLayers.add(desertLayer)


  println("Map generated")

  val renderer = new OrthogonalTiledMapRenderer(map, 1f)

  val mainLayer = map.getLayers.get(0).asInstanceOf[TiledMapTileLayer]
  val mapWidthPixels = (mainLayer.getWidth * mainLayer.getTileWidth).asInstanceOf[Int]
  val mapHeightPixels = (mainLayer.getHeight * mainLayer.getTileHeight).asInstanceOf[Int]

  var cameraCenterX = citiesData.head.x * mainLayer.getTileWidth - mainLayer.getTileWidth/2
  var cameraCenterY = citiesData.head.y * mainLayer.getTileHeight - mainLayer.getTileHeight /2


  val gameFont = assetManager.get("fonts/lunchtime-doubly-so/lunchds.ttf",classOf[BitmapFont])


  override def show(): Unit = {
    Gdx.input.setInputProcessor(mapInputProcessor)
  }

  override def render(delta: Float): Unit = {

    if (Gdx.input.isKeyPressed(Keys.UP)){
      cameraCenterY += 25
    }

    if (Gdx.input.isKeyPressed(Keys.DOWN)){
      cameraCenterY -= 25
    }

    if (Gdx.input.isKeyPressed(Keys.LEFT)){
      cameraCenterX-=25
    }

    if (Gdx.input.isKeyPressed(Keys.RIGHT)){
      cameraCenterX += 25
    }


    setCameraPosition(camera,delta)


  }

  private def setCameraPosition(camera: OrthographicCamera,delta: Float): Unit ={


    if (cameraCenterY + camera.viewportHeight /2 > mapHeightPixels) {
      cameraCenterY = mapHeightPixels - camera.viewportHeight /2
    }

    if (cameraCenterY - camera.viewportHeight /2 < 0) {
      cameraCenterY = camera.viewportHeight /2
    }

    if (cameraCenterX - camera.viewportWidth/2<0){
      cameraCenterX = camera.viewportWidth/2
    }

    if (cameraCenterX + camera.viewportWidth /2 > mapWidthPixels) {
      cameraCenterX = mapWidthPixels - camera.viewportWidth /2
    }

    camera.position.set(cameraCenterX,cameraCenterY,0)
    camera.update()
    renderer.setView(camera)

    renderer.getBatch.begin()
    renderer.renderTileLayer(desertLayer)
    renderer.renderTileLayer(townLayer)
    renderer.renderTileLayer(fogOfWarLayer)

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
    citiesData.foreach(city=>{
      if (mapData.cells.find(cell=>cell.x == city.x && cell.y == city.y).get.state == MapCellState.DISCOVERED){
        val cityPixelX : Int = (city.x * mainLayer.getTileWidth).asInstanceOf[Int]
        val cityPixelY : Int = (city.y * mainLayer.getTileHeight).asInstanceOf[Int]
        gameFont.draw(game.batch,city.name,cityPixelX,cityPixelY)
      }
    })

    //drawing raider camps names
    raiderCampsData.foreach(raiderCampInfo=>{
      if (mapData.cells.find(cell=>cell.x == raiderCampInfo.tileX && cell.y == raiderCampInfo.tileY).get.state == MapCellState.DISCOVERED){
        val campPixelX : Int = (raiderCampInfo.tileX * mainLayer.getTileWidth).asInstanceOf[Int]
        val campPixelY : Int = (raiderCampInfo.tileY * mainLayer.getTileHeight).asInstanceOf[Int]
        gameFont.draw(game.batch,s"${raiderCampInfo.name} (${raiderCampInfo.tileX},${raiderCampInfo.tileY})",campPixelX,campPixelY)
      }
    })

    gameFont.draw(game.batch,s"Camera position: ($cameraCenterX,$cameraCenterY), camera viewport size: ${camera.viewportWidth}/${camera.viewportHeight} ,player position: ($cameraCenterX,$cameraCenterY)",camera.unproject(new Vector3(0,0,0)).x,camera.unproject(new Vector3(0,0,0)).y)
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
      val texture = assetManager.get("expedition.png",classOf[Texture])
      expeditions += ExpeditionInfo(cameraCenterX,cameraCenterY,cameraCenterX,cameraCenterY,clickInfo.pixelX, clickInfo.pixelY,texture,None)
    } else {
      Gdx.app.log("INFO","Expedition already sent")
    }
  }

  private def checkExpeditionTile(tileX:Int,tileY:Int,expedition:ExpeditionInfo): Unit ={
    raiderCampsData.foreach(camp=>{
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
    if (raiderCampsData.contains(camp)){
      raiderCampsData -= camp
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
