package com.anton.nuclearnation

import java.lang.Math

import com.anton.nuclearnation.MapScreen._
import com.anton.nuclearnation.UnitType.UnitType
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx._
import com.badlogic.gdx.graphics.Pixmap.Format
import com.badlogic.gdx.graphics._
import com.badlogic.gdx.graphics.g2d.{BitmapFont, Sprite, TextureRegion}
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
import com.badlogic.gdx.scenes.scene2d.ui.{Button, Dialog, Image, Label, Skin, Table, TextButton}
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

  val expeditionTexture =   assetManager.get("expedition.png",classOf[Texture])


  val region = new TextureRegion(desertTileTexture)

  val raiderCampImage = new Sprite(assetManager.get("raider_camp.png",classOf[Texture]))


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

  val unitConstructionButton = new TextButton("Units",skin)
  val centerOnCapitalButton = new TextButton("Re-center",skin)

  unitConstructionButton.addCaptureListener(new ClickListener(){
    override def clicked (event:InputEvent, x:Float, y:Float):Unit= {
      game.setScreen(new UnitConstructionScreen(game,MapScreen.this))
    }
  })

  centerOnCapitalButton.addCaptureListener(new ClickListener(){
    override def clicked (event:InputEvent, x:Float, y:Float):Unit= {
      cameraCenterX = capitalCell.x * desertLayer.getTileWidth - desertLayer.getTileWidth/2
      cameraCenterY = capitalCell.y * desertLayer.getTileHeight - desertLayer.getTileHeight /2
    }
  })

  stage.addActor(unitConstructionButton)
  stage.addActor(centerOnCapitalButton)



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

  val locations = ListBuffer[MapLocation]()

  val cityNames = List[String]("New Reno","Modoc","Arroyo")
  val raiderCampsNames = List[String]("Mad Dogs","Knives","Jokers")

  val capitalCoords = coordsGenerator.getCoords

  val capitalCell = mapData.cells.find(cell=>cell.x == capitalCoords._1 && cell.y == capitalCoords._2).get
  val capital = CityInfo("Hope",capitalCell,isOwnedByPlayer = true)
  capitalCell.state = MapCellState.VISITED
  capitalCell.location = Some(capital)
  locations += capital


  for (_<-0 until 10) yield {
    val coords = coordsGenerator.getCoords
    val cell = mapData.getCell(coords._1,coords._2).get
    val ruin = RuinsInfo(cell)
    cell.location = Some(ruin)
    locations += ruin
  }


  cityNames.foreach(cityName=> {
    val coords = coordsGenerator.getCoords
    val cityCell = mapData.getCell(coords._1,coords._2).get
    val city = CityInfo(cityName, cityCell)
    cityCell.location = Some(city)
    locations += city
    Gdx.app.log("INFO",s"Generated city $cityName at coords $coords")
  })

  raiderCampsNames.foreach(raiderCampName=> {
    val coords = coordsGenerator.getCoords
    val raiderCell = mapData.getCell(coords._1,coords._2).get
    val raiderCamp = RaiderCampInfo(raiderCampName, raiderCell)
    raiderCell.location = Some(raiderCamp)
    locations += raiderCamp
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

    if (mapData.cells.find(cell=>cell.x == x && cell.y == y).get.state == MapCellState.HIDDEN){
      fogOfWarLayer.setCell(x,y,fogOfWarCell)
    }

  }
  map.getLayers.add(townLayer)
  map.getLayers.add(desertLayer)

  //uncovering random locations
  val randomUncoveredLocations = Random.shuffle(locations).take(3)

  randomUncoveredLocations.foreach(location => {
    discoverTile(location.mapCell.x,location.mapCell.y)
  })





  println("Map generated")

  val renderer = new OrthogonalTiledMapRenderer(map, 1f)

  val mapWidthPixels = (desertLayer.getWidth * desertLayer.getTileWidth).asInstanceOf[Int]
  val mapHeightPixels = (desertLayer.getHeight * desertLayer.getTileHeight).asInstanceOf[Int]

  var cameraCenterX = capitalCell.x * desertLayer.getTileWidth - desertLayer.getTileWidth/2
  var cameraCenterY = capitalCell.y * desertLayer.getTileHeight - desertLayer.getTileHeight /2

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

    //drawing roads between cities
    locations
        .filter(location=>location.isInstanceOf[CityInfo])
        .filter(location=>location!=capital)
        .foreach(location => {
          val city = location.asInstanceOf[CityInfo]
          if (city.isOwnedByPlayer) {
            drawRoadLine(new Vector2(capital.mapCell.x * desertLayer.getTileWidth + townImage.getWidth /2, capital.mapCell.y * desertLayer.getTileHeight + townImage.getHeight/2),
              new Vector2(city.mapCell.x * desertLayer.getTileWidth + townImage.getWidth/2, city.mapCell.y * desertLayer.getTileHeight + townImage.getHeight/2), 3)
          }
        })

  }

  case class ActorMapCoords(tileX:Int,tileY:Int)

  def visitTile(tileX: Int, tileY: Int, radiusTiles:Int=5)  {
    val tile = mapData.getCell(tileX,tileY).get
    tile.state = MapCellState.VISITED

    //removing actor over current tile if exists
    stage.getActors.items.filter(a=>a!=null).find(a=>{
      val userObject = Option(a.getUserObject)
      userObject match {
        case Some(coords: ActorMapCoords) =>
          coords.tileX == tileX && coords.tileY == tileY
        case _ =>
          false
      }
    }) match {
      case Some(a)=>
        a.remove()
      case None=>
    }

    //getting covered tiles within radius
    val tilesAround = for (
      x<-tileX - radiusTiles to tileX + radiusTiles;
      y<-tileY - radiusTiles to tileY + radiusTiles
    ) yield {
      if (!(x == tileX && y == tileY)){
        mapData.getCell(x,y)
      } else {
        None
      }
    }
    tilesAround.filter(t=>t.isDefined).foreach(t=>{
      if (t.isDefined && t.get.state == MapCellState.HIDDEN) {
        discoverTile(t.get.x,t.get.y)
      }

    })
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

        val tileX = (expedition.positionGlobalPixelX / desertLayer.getTileWidth).toInt
        val tileY = (expedition.positionGlobalPixelY / desertLayer.getTileHeight).toInt
        visitTile(tileX,tileY)
        if (expedition.originalDirection.isDefined && !direction.hasSameDirection(expedition.originalDirection.get)){
          checkExpeditionTile(tileX,tileY,expeditions.head)
          visitTile(tileX,tileY)
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
    locations.foreach(location=>{
      val mapCell = location.mapCell
      if (mapCell.state == MapCellState.VISITED){
        val pixelX : Int = (location.mapCell.x * desertLayer.getTileWidth).asInstanceOf[Int]
        val pixelY : Int = (location.mapCell.y * desertLayer.getTileHeight).asInstanceOf[Int]
        gameFont.draw(stage.getBatch,location.name,pixelX,pixelY)
      }
    })

    gameFont.draw(stage.getBatch,s"Camera position: ($cameraCenterX,$cameraCenterY), camera viewport size: ${camera.viewportWidth}/${camera.viewportHeight} ,player position: ($cameraCenterX,$cameraCenterY)",camera.unproject(new Vector3(0,0,0)).x,camera.unproject(new Vector3(0,0,0)).y)
    stage.getBatch.end()

    val unitConstructionButtonCoords = camera.unproject(new Vector3(stage.getViewport.getScreenWidth - unitConstructionButton.getPrefWidth,stage.getViewport.getScreenHeight,0))
    unitConstructionButton.setPosition(unitConstructionButtonCoords.x,unitConstructionButtonCoords.y)
    centerOnCapitalButton.setPosition(unitConstructionButton.getX - centerOnCapitalButton.getPrefWidth-5,unitConstructionButton.getY)
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
    val clickedTileX = (coordX / desertLayer.getTileWidth).toInt
    val clickedTileY = (coordY / desertLayer.getTileHeight).toInt
    MapClickInfo(coordX,coordY,clickedTileX,clickedTileY)
  }

  private def mapRightClicked(screenX: Int, screenY: Int):Unit = {
    Gdx.app.log("INFO","Right clicked on map")

    val clickInfo = getClickInfo(screenX,screenY)

    val mapCell = mapData.getCell(clickInfo.tileX,clickInfo.tileY)
    if (mapCell.get.state == MapCellState.VISITED) {
      mapCell.get.location match {
        case Some(ri: RaiderCampInfo) => {
          game.setScreen(new ActionMixScreen(ri, game, this))
          return
        }
        case Some(ci: CityInfo) => {
          game.setScreen(new ActionMixScreen(ci, game, this))
          return
        }
        case _ =>
      }
    } else { //map cell is not discovered
        game.setScreen(new ActionMixScreen(CoveredAreaInfo(mapCell.get), game, this))
        return
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
            sendExpedition(new Vector2(capital.mapCell.x,capital.mapCell.y),new Vector2(clickInfo.tileX,clickInfo.tileY),texture,scala.List[UnitType]())
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

  def sendExpedition(sourceTile:Vector2 = new Vector2(capital.mapCell.x,capital.mapCell.y),
                     destTile:Vector2,texture:Texture = expeditionTexture,
                     units:scala.List[UnitType]
                    ): Unit ={
    expeditions += ExpeditionInfo(
      sourceTile.x * desertLayer.getTileWidth  + texture.getWidth/2,
      sourceTile.y * desertLayer.getTileHeight +texture.getHeight/2,
      sourceTile.x * desertLayer.getTileWidth  + texture.getWidth/2,
      sourceTile.y * desertLayer.getTileHeight +texture.getHeight/2,
      destTile.x * desertLayer.getTileWidth +texture.getWidth/2,
      destTile.y * desertLayer.getTileHeight +texture.getHeight/2,
      texture,
      None,
      units = units
    )
  }

  private def discoverTile(x:Int,y:Int): Unit ={
    val t = mapData.getCell(x,y)
    if (t.isDefined && t.get.state == MapCellState.HIDDEN){
      fogOfWarLayer.setCell(x, y, null)
      t.get.state = MapCellState.DISCOVERED
      val image = Option(townLayer.getCell(x,y)) match {
        case Some(_)=>
          new Image(townLayer.getCell(x,y).getTile.getTextureRegion)
        case None=>new Image(desertLayer.getCell(x,y).getTile.getTextureRegion)
      }

      image.setColor(Color.GRAY)
      image.setUserObject(ActorMapCoords(x,y))
      stage.addActor(image)
      val coords = new Vector3(x * desertLayer.getTileWidth, y * desertLayer.getTileHeight, 0)
      image.setPosition(coords.x, coords.y)
    }
  }




  private def checkExpeditionTile(tileX:Int,tileY:Int,expedition:ExpeditionInfo): Unit ={
    val cell = mapData.getCell(tileX,tileY).get
    cell.location match {
      case Some(rci:RaiderCampInfo)=>{
        game.setScreen(new SituationScreen(rci,DefendersType.RAIDERS,game,this,List[UnitType]()))
      }
      case Some(ci:CityInfo)=>{
        game.setScreen(new SituationScreen(ci,DefendersType.SOLDIERS,game,this,List[UnitType]()))
      }
      case Some(ri:RuinsInfo)=>{
        locations -= ri
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
    if (locations.contains(camp)){
      locations -= camp
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
                            speed:Int = 600,
                            units:scala.List[UnitType]
                           )

  sealed abstract class MapLocation(){
    def mapCell:MapCellData
    def name:String
  }
  case class RaiderCampInfo( name:String,mapCell: MapCellData) extends MapLocation()
  case class CityInfo(name:String,mapCell: MapCellData,var isOwnedByPlayer:Boolean = false) extends MapLocation()
  case class RuinsInfo(mapCell: MapCellData,name:String = "Pre-war ruins") extends MapLocation()
  case class CoveredAreaInfo(mapCell: MapCellData,name:String="") extends MapLocation

  case class Technology(name:String, var enabled:Boolean = false)

}
