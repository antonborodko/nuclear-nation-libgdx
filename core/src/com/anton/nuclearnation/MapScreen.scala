package com.anton.nuclearnation

import java.lang.Math

import com.anton.nuclearnation.MapScreen._
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx._
import com.badlogic.gdx.graphics.Pixmap.Format
import com.badlogic.gdx.graphics._
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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.tiled.renderers.{IsometricStaggeredTiledMapRenderer, IsometricTiledMapRenderer, OrthogonalTiledMapRenderer}
import com.badlogic.gdx.scenes.scene2d.{InputEvent, InputListener, Stage}
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle
import com.badlogic.gdx.scenes.scene2d.ui.{Button, Dialog, Label, Skin, Table, TextButton}
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.StretchViewport

import scala.collection.mutable.ListBuffer

class MapScreen(game: NuclearNation) extends Screen{

  val technologies:List[Technology] = List(Technology("Advanced tactics"),Technology("Automatic weapons"))

  val assetManager = game.assetManager

  val map = new TiledMap
  val layers = map.getLayers

  val mapWidthTiles = 30
  val mapHeightTiles = 30

  val desertTileTexture = assetManager.get("desert_tile.png",classOf[Texture])
  val ruinedBuildingTexture = assetManager.get("ruined-building.png",classOf[Texture])
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

  val expeditions = ListBuffer[ExpeditionInfo]()

  val coordsGenerator = new MapCoordsGenerator(mapWidthTiles,mapHeightTiles,3)

  val mapData = new MapData(mapWidthTiles,mapHeightTiles)


  val gameFont = assetManager.get("fonts/lunchtime-doubly-so/lunchds.ttf",classOf[BitmapFont])

  val skin = assetManager.get("data/commodore64/skin/uiskin.json",classOf[Skin])

  val stage = new Stage(new StretchViewport(1600,960,new OrthographicCamera()))
  val camera = stage.getCamera.asInstanceOf[OrthographicCamera]


  val mapInputProcessor = new InputProcessor() {

    override def touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = {
      if (button == Input.Buttons.RIGHT) {
        mapRightClicked(screenX,screenY)
        return true
      }
      false
    }

    override def keyDown(keycode: Int): Boolean = {true}

    override def keyUp(keycode: Int): Boolean = {
      if (keycode == Input.Keys.U){
        game.setScreen(new UnitConstructionScreen(game,MapScreen.this))
        return true
      }
      false
    }

    override def keyTyped(character: Char): Boolean = {true}

    override def touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = {true}

    override def touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = {true}

    override def mouseMoved(screenX: Int, screenY: Int): Boolean = {true}

    override def scrolled(amount: Int): Boolean = {true}
  }

  val locationsList = ListBuffer[MapLocation]()

  val cityNames = List[String]("New Reno","Modoc","Arroyo")
  val raiderCampsNames = List[String]("Mad Dogs","Knives","Jokers")

  val capitalCoords = coordsGenerator.getCoords

  val capitalCell = mapData.cells.find(cell=>cell.x == capitalCoords._1 && cell.y == capitalCoords._2).get
  val capital = CityInfo("Hope",capitalCell,isOwnedByPlayer = true)
  capitalCell.state = MapCellState.DISCOVERED
  capitalCell.location = Some(capital)
  locationsList += capital


  for (_<-0 until 10) yield {
    val coords = coordsGenerator.getCoords
    val cell = mapData.getCell(coords._1,coords._2).get
    val ruin = RuinsInfo(cell)
    cell.location = Some(ruin)
    locationsList += ruin
  }


  cityNames.foreach(cityName=> {
    val coords = coordsGenerator.getCoords
    val cityCell = mapData.getCell(coords._1,coords._2).get
    val city = CityInfo(cityName, cityCell)
    cityCell.location = Some(city)
    locationsList += city
    Gdx.app.log("INFO",s"Generated city $cityName at coords $coords")
  })

  raiderCampsNames.foreach(raiderCampName=> {
    val coords = coordsGenerator.getCoords
    val raiderCell = mapData.getCell(coords._1,coords._2).get
    val raiderCamp = RaiderCampInfo(raiderCampName, raiderCell)
    raiderCell.location = Some(raiderCamp)
    locationsList += raiderCamp
    Gdx.app.log("INFO",s"Generated raider camp $raiderCampName at coords $coords")
  })


  for (
    x <- 0 until mapWidthTiles;
    y <- 0 until mapHeightTiles
  ) yield  {
    desertLayer.setCell(x, y, desertTileCell)

    val location = mapData.getCell(x,y).get.location

    location match {
      case Some(_:RuinsInfo) => {
        val ruinRegion = new TextureRegion(ruinedBuildingTexture)
        val ruinTile = new StaticTiledMapTile(ruinRegion)
        val ruinCell = new Cell
        ruinCell.setTile(ruinTile)
        townLayer.setCell(x,y,ruinCell)
      }

      case Some(_:CityInfo) => {
        val townRegion = new TextureRegion(townImage)
        val townTile = new StaticTiledMapTile(townRegion)
        val townCell = new Cell
        townCell.setTile(townTile)
        townLayer.setCell(x,y,townCell)
      }

      case Some(_:RaiderCampInfo) => {
        val campRegion = new TextureRegion(raiderCampImage)
        val raiderTile = new StaticTiledMapTile(campRegion)
        val raiderCell = new Cell
        raiderCell.setTile(raiderTile)
        townLayer.setCell(x,y,raiderCell)
      }

      case _=>

    }

    if (mapData.cells.find(cell=>cell.x == x && cell.y == y).get.state == MapCellState.UNDISCOVERED){
      fogOfWarLayer.setCell(x,y,fogOfWarCell)
    }

  }
  map.getLayers.add(townLayer)
  map.getLayers.add(desertLayer)


  println("Map generated")

  val renderer = new OrthogonalTiledMapRenderer(map, 1f)

  val mainLayer = map.getLayers.get(0).asInstanceOf[TiledMapTileLayer]
  val mapWidthPixels = (mainLayer.getWidth * mainLayer.getTileWidth).asInstanceOf[Int]
  val mapHeightPixels = (mainLayer.getHeight * mainLayer.getTileHeight).asInstanceOf[Int]

  var cameraCenterX = capitalCell.x * mainLayer.getTileWidth - mainLayer.getTileWidth/2
  var cameraCenterY = capitalCell.y * mainLayer.getTileHeight - mainLayer.getTileHeight /2

  val roadRenderer = new ShapeRenderer()


  import com.badlogic.gdx.Gdx
  import com.badlogic.gdx.graphics.glutils.ShapeRenderer
  import com.badlogic.gdx.math.Matrix4
  import com.badlogic.gdx.math.Vector2

  def drawRoadLine(start: Vector2, end: Vector2, lineWidth: Int, color: Color = Color.BROWN, projectionMatrix: Matrix4 = camera.combined): Unit = {
    Gdx.gl.glLineWidth(lineWidth)
    roadRenderer.setProjectionMatrix(projectionMatrix)
    roadRenderer.begin(ShapeRenderer.ShapeType.Line)
    roadRenderer.setColor(color)
    roadRenderer.line(start, end)
    roadRenderer.end()
    Gdx.gl.glLineWidth(1)
  }


  override def show(): Unit = {
    val multiplexer = new InputMultiplexer()
    multiplexer.addProcessor(stage)
    multiplexer.addProcessor(mapInputProcessor)
    Gdx.input.setInputProcessor(multiplexer)

  }

  override def render(delta: Float): Unit = {

    Gdx.gl.glClearColor(1, 0, 0, 1)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT)
    stage.getBatch.setColor(Color.WHITE)

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
    stage.act(delta)
    stage.draw()

  }

  def discoverTile(tileX: Int, tileY: Int) = {
    val tile = mapData.getCell(tileX,tileY).get
    tile.state = MapCellState.DISCOVERED
    fogOfWarLayer.setCell(tileX,tileY,null)
  }

  private def setCameraPosition(camera: OrthographicCamera, delta: Float): Unit ={


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


    if (sys.env.get("DISABLE_FOG_OF_WAR").isEmpty || sys.env("DISABLE_FOG_OF_WAR").toLowerCase() != "true"){
      renderer.renderTileLayer(fogOfWarLayer)
    }

    renderer.getBatch.end()

    stage.getBatch.begin()
    stage.getBatch.setProjectionMatrix(camera.combined)

    expeditions.foreach(expedition => {

      if(expedition.positionGlobalPixelX != expedition.destinationGlobalPixelX || expedition.positionGlobalPixelY !=expedition.destinationGlobalPixelY) {

        val currentPos = new Vector2(expedition.positionGlobalPixelX,expedition.positionGlobalPixelY)
        val destination = new Vector2(expedition.destinationGlobalPixelX,expedition.destinationGlobalPixelY)

        val direction = destination.sub(currentPos).nor()

        val tileX = (expedition.positionGlobalPixelX / mainLayer.getTileWidth).toInt
        val tileY = (expedition.positionGlobalPixelY / mainLayer.getTileHeight).toInt
        discoverTile(tileX,tileY)
        if (expedition.originalDirection.isDefined && !direction.hasSameDirection(expedition.originalDirection.get)){
          checkExpeditionTile(tileX,tileY,expeditions.head)
          discoverTile(tileX,tileY)
          expeditions.remove(0)
        } else {
          val newOriginX = expedition.positionGlobalPixelX + direction.x * expedition.speed * delta
          val newOriginY = expedition.positionGlobalPixelY + direction.y * expedition.speed * delta
          expeditions(0) = expedition.copy(positionGlobalPixelX = newOriginX, positionGlobalPixelY = newOriginY,originalDirection = Some(direction))
          stage.getBatch.draw(expedition.marker, expeditions.head.positionGlobalPixelX - expedition.marker.getWidth/2, expeditions.head.positionGlobalPixelY - expedition.marker.getHeight/2)


        }


      }
    })

    //drawing names where applicable
    locationsList.foreach(location=>{
      val pixelX : Int = (location.mapCell.x * mainLayer.getTileWidth).asInstanceOf[Int]
      val pixelY : Int = (location.mapCell.y * mainLayer.getTileHeight).asInstanceOf[Int]
      gameFont.draw(stage.getBatch,location.name,pixelX,pixelY)
    })

    gameFont.draw(stage.getBatch,s"Camera position: ($cameraCenterX,$cameraCenterY), camera viewport size: ${camera.viewportWidth}/${camera.viewportHeight} ,player position: ($cameraCenterX,$cameraCenterY)",camera.unproject(new Vector3(0,0,0)).x,camera.unproject(new Vector3(0,0,0)).y)
    stage.getBatch.end()

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

  private def mapRightClicked(screenX: Int, screenY: Int):Unit = {
    Gdx.app.log("INFO","Right clicked on map")

    val clickInfo = getClickInfo(screenX,screenY)

    val mapCell = mapData.getCell(clickInfo.tileX,clickInfo.tileY)
    mapCell.get.location match {
      case Some(_:RaiderCampInfo) =>{
        game.setScreen(new ActionMixScreen(game,this))
        return
      }
      case _=>
    }


    val table = new Table()

    val soldiersLabel = new Label("Soldiers",skin)
    var soldiersCount = 0
    val soldiersCountLabel = new Label(soldiersCount.toString,skin)
    val plusButton = new TextButton("+",skin)
    val minusButton = new TextButton("-",skin)
    table.add(soldiersLabel).space(20)
    table.add(soldiersCountLabel).space(20)
    table.add(plusButton).space(0)
    table.add(minusButton).space(0)

    plusButton.addCaptureListener(new ClickListener(){
      override def clicked (event:InputEvent, x:Float, y:Float):Unit= {
        soldiersCount +=1
        soldiersCountLabel.setText(soldiersCount.toString)
      }
    })

    minusButton.addCaptureListener(new ClickListener(){
      override def clicked (event:InputEvent, x:Float, y:Float):Unit= {
        if (soldiersCount>0){
          soldiersCount -=1
        }
        soldiersCountLabel.setText(soldiersCount.toString)
      }
    })

    val dialog = new Dialog("Choose expedition mix", skin) {
      override def result(result:Object) {
        Gdx.app.log("INFO",s"Soldiers count: ${soldiersCountLabel.getText}")
        if (result.asInstanceOf[Boolean]){
          if (expeditions.size<1) {
            val clickInfo = getClickInfo(screenX,screenY)
            val texture = assetManager.get("expedition.png",classOf[Texture])
            val coords = new Vector3(capital.mapCell.x * mainLayer.getTileWidth + texture.getWidth/2,capital.mapCell.y * mainLayer.getTileHeight +texture.getHeight/2,0)
            expeditions += ExpeditionInfo(coords.x,coords.y,coords.x,coords.y,clickInfo.pixelX, clickInfo.pixelY,texture,None)
          } else {
            Gdx.app.log("INFO","Expedition already sent")
          }
        } else {
          Gdx.app.log("INFO","Button clicked " + result)
        }
      }
    }

    dialog.getContentTable.add(table)
    dialog.button("Send", true)
    dialog.button("Cancel", false)
    dialog.key(Keys.ESCAPE, false).key(Keys.ENTER, true)
    dialog.getContentTable.pad(20)
    dialog.getTitleTable.pad(20)
    dialog.pack()
    stage.addActor(dialog)
    dialog.setPosition(cameraCenterX - dialog.getPrefWidth/2,cameraCenterY - dialog.getPrefHeight/2)
  }

  private def checkExpeditionTile(tileX:Int,tileY:Int,expedition:ExpeditionInfo): Unit ={
    val cell = mapData.getCell(tileX,tileY).get
    cell.location match {
      case Some(rci:RaiderCampInfo)=>{
        game.setScreen(new SituationScreen(rci,DefendersType.RAIDERS,game,this))
      }
      case Some(ci:CityInfo)=>{
        game.setScreen(new SituationScreen(ci,DefendersType.SOLDIERS,game,this))
      }
      case Some(ri:RuinsInfo)=>{
        locationsList -= ri
        townLayer.setCell(ri.mapCell.x,ri.mapCell.y,null)
        townLayer.setCell(tileX,tileY,null)
        val tech = technologies.find(t => !t.enabled)

        tech match  {
          case Some(t) =>
            t.enabled = true

            val dialog = new Dialog("New technology discovered", skin) {
              override def result(result:Object) {

              }
            }

            dialog.text(s"New technology discovered: ${t.name}")
            dialog.button("OK", true)
            dialog.key(Keys.ESCAPE, false).key(Keys.ENTER, true)
            dialog.getContentTable.pad(20)
            dialog.getTitleTable.pad(20)
            dialog.pack()
            stage.addActor(dialog)
            dialog.setPosition(cameraCenterX - dialog.getPrefWidth/2,cameraCenterY - dialog.getPrefHeight/2)


          case None =>
        }
      }
      case _=>
    }

  }

  def deleteCamp(camp: RaiderCampInfo) = {
    if (locationsList.contains(camp)){
      locationsList -= camp
      val desertTile = new StaticTiledMapTile(new TextureRegion(desertTileTexture))
      val desertCell = new Cell
      desertCell.setTile(desertTile)
      townLayer.setCell(camp.mapCell.x,camp.mapCell.y,desertCell)
    }
  }



}

object MapScreen{


  case class MapClickInfo(pixelX:Float, pixelY: Float, tileX:Int,tileY:Int)
  case class ExpeditionInfo(originGlobalPixelX:Float,
                            originGlobalPixelY:Float,
                            positionGlobalPixelX:Float,
                            positionGlobalPixelY:Float,
                            destinationGlobalPixelX:Float,
                            destinationGlobalPixelY:Float,
                            marker:Texture,
                            originalDirection:Option[Vector2],
                            speed:Int = 600
                           )

  sealed abstract class MapLocation(){
    def mapCell:MapCellData
    def name:String
  }
  case class RaiderCampInfo( name:String,mapCell: MapCellData) extends MapLocation()
  case class CityInfo(name:String,mapCell: MapCellData,var isOwnedByPlayer:Boolean = false) extends MapLocation()
  case class RuinsInfo(mapCell: MapCellData,name:String = "Pre-war ruins") extends MapLocation()

  case class Technology(name:String, var enabled:Boolean = false)

}
