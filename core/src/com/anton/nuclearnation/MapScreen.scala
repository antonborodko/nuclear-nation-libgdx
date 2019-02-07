package com.anton.nuclearnation

import java.lang.Math

import com.anton.nuclearnation.ControlledBy.ControlledBy
import com.anton.nuclearnation.MapScreen._
import com.anton.nuclearnation.UnitType.UnitType
import com.badlogic.gdx.Input.{Buttons, Keys}
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
import com.badlogic.gdx.scenes.scene2d.{Group, InputEvent, InputListener, Stage}
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle
import com.badlogic.gdx.scenes.scene2d.ui.{Button, Dialog, Image, Label, Skin, Table, TextButton}
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Timer.Task
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.badlogic.gdx.utils.Timer

import scala.collection.mutable.ListBuffer

class MapScreen(game: NuclearNation) extends Screen{

  val technologies:List[Technology] = List(Technology("Advanced tactics"),Technology("Automatic weapons"))

  val assetManager = game.assetManager

  val map = new TiledMap
  val layers = map.getLayers

  val mapWidthTiles = 30
  val mapHeightTiles = 30

  val DEFAULT_DISCOVERABLE_RADIUS = if (sys.env.get("DEFAULT_DISCOVERABLE_RADIUS").isEmpty) 1 else sys.env("DEFAULT_DISCOVERABLE_RADIUS").toInt

  val desertTileTexture = assetManager.get("desert_tile.png",classOf[Texture])
  val ruinedBuildingTexture = assetManager.get("ruined-building.png",classOf[Texture])
  val desertLayer = new TiledMapTileLayer(mapHeightTiles, mapWidthTiles, desertTileTexture.getWidth, desertTileTexture.getHeight)
  val townLayer = new TiledMapTileLayer(mapHeightTiles, mapWidthTiles, desertTileTexture.getWidth, desertTileTexture.getHeight)
  val fogOfWarLayer = new TiledMapTileLayer(mapHeightTiles, mapWidthTiles, desertTileTexture.getWidth, desertTileTexture.getHeight)
  val desertTileCell:Cell = new Cell
  val fogOfWarCell = new Cell

  val expeditionTexture = assetManager.get("expedition.png",classOf[Texture])

  val tradeCaravanTexture = assetManager.get("tradeCaravan.png",classOf[Texture])
  val militaryCaravanTexture = assetManager.get("militaryCaravan.png",classOf[Texture])


  val region = new TextureRegion(desertTileTexture)

  val raiderCampImage = new Sprite(assetManager.get("raider_camp.png",classOf[Texture]))


  val fogOfWarTexture = assetManager.get("fog_of_war_tile.png",classOf[Texture])
  val townImage = assetManager.get("town.png",classOf[Texture])

  desertTileCell.setTile(new StaticTiledMapTile(region))
  fogOfWarCell.setTile(new StaticTiledMapTile(new TextureRegion(fogOfWarTexture)))

  val coordsGenerator = new MapCoordsGenerator(mapWidthTiles,mapHeightTiles,3)

  val mapData = new MapData(mapWidthTiles,mapHeightTiles)


  val gameFont = assetManager.get("fonts/lunchtime-doubly-so/lunchds.ttf",classOf[BitmapFont])

  val skin = assetManager.get("data/commodore64/skin/uiskin.json",classOf[Skin])

  val stage = new Stage(new StretchViewport(1600,960,new OrthographicCamera()))
  val camera = stage.getCamera.asInstanceOf[OrthographicCamera]

  val unitConstructionButton = new TextButton("Units",skin)
  val centerOnCapitalButton = new TextButton("Re-center",skin)
  val pauseButton = new TextButton("Pause",skin)


  val assetChain = new AssetChain(this)


  val soldierUnit = new Image(assetManager.get("unitConstruction/soldierUnit.png",classOf[Texture]))
  val scientistUnit = new Image(assetManager.get("unitConstruction/scientistUnit.png",classOf[Texture]))
  val engineerUnit = new Image(assetManager.get("unitConstruction/engineerUnit.png",classOf[Texture]))

  val soldierCard = new AssetCard(soldierUnit,"SOLDIER",game.soldierCounter,game,Some(UnitType.SOLDIER))
  val engineerCard = new AssetCard(engineerUnit,"ENGINEER",game.engineerCounter,game,Some(UnitType.ENGINEER))
  val scientistCard = new AssetCard(scientistUnit,"SCIENTIST",game.scientistCounter,game,Some(UnitType.SCIENTIST))

  var isPaused = false

  val music = Gdx.audio.newMusic(Gdx.files.internal("music/POL-dark-crossing-short.mp3"))

  val randomCaravanSpawnChance =  sys.env.get("RANDOM_CARAVAN_SPAWN_CHANCE") match {
    case Some(v)=>v.toLowerCase().toInt
    case None=>1
  }

  val ownedCaravanSpawnChance =  sys.env.get("OWNED_CARAVAN_SPAWN_CHANCE") match {
    case Some(v)=>v.toLowerCase().toInt
    case None=>30
  }


  val caravanSpeed =  sys.env.get("CARAVAN_SPEED") match {
    case Some(v)=>v.toLowerCase().toInt
    case None=>100
  }


  val mapDebugOutputEnabled = sys.env.get("ENABLE_MAP_DEBUG_OUTPUT") match {
    case Some(v)=>v.toLowerCase().toBoolean
    case None=>false
  }


  unitConstructionButton.addCaptureListener(new ClickListener(){
    override def clicked (event:InputEvent, x:Float, y:Float):Unit= {
      game.setScreen(new UnitConstructionScreen(game,MapScreen.this))
    }
  })

  centerOnCapitalButton.addCaptureListener(new ClickListener(){
    override def clicked (event:InputEvent, x:Float, y:Float):Unit= {
      centerScreen()
    }
  })

  pauseButton.addCaptureListener(new ClickListener(){
    override def clicked (event:InputEvent, x:Float, y:Float):Unit= {
      pauseGame()
    }
  })

  val buttonsGroup = new Group()
  val tileGroup = new Group()
  buttonsGroup.addActor(unitConstructionButton)
  buttonsGroup.addActor(centerOnCapitalButton)
  buttonsGroup.addActor(pauseButton)
  stage.addActor(tileGroup)
  stage.addActor(buttonsGroup)


  Timer.schedule(() => {
    val cities = locations.filter(l => l.isInstanceOf[CityInfo])
    //spawning random expeditions unless paused
    if (!isPaused){
      if (Random.nextInt(100)<randomCaravanSpawnChance){
        val randomSourceCity = cities(Random.nextInt(cities.size)).asInstanceOf[CityInfo]
        val citiesExcludingSource = cities.filter(l=>l!=randomSourceCity)
        val randomDestCity = citiesExcludingSource(Random.nextInt(citiesExcludingSource.size)).asInstanceOf[CityInfo]

        sendExpedition(randomSourceCity.mapCell,randomDestCity.mapCell,tradeCaravanTexture,owner = ControlledBy.COMPUTER,units = scala.List[UnitType](),Objective.TRADE,speed = caravanSpeed)
      }

      //spawning more frequent expeditions between cities and capital
      val playerOwnedCities = cities.filter(c=>c.asInstanceOf[CityInfo].isOwnedByPlayer).asInstanceOf[ListBuffer[CityInfo]]
      val playerOwnedCitiesExcludingCapital = playerOwnedCities.filter(c=>c != capital)


      if (playerOwnedCities.size>1 && Random.nextInt(100)<ownedCaravanSpawnChance) {
        val randomSourceCity = playerOwnedCities(Random.nextInt(playerOwnedCities.size))
        val randomDestCity = randomSourceCity match {
          case `capital` => playerOwnedCitiesExcludingCapital(Random.nextInt(playerOwnedCitiesExcludingCapital.size))
          case _ => capital
        }

        val objective = Random.shuffle(List(Objective.TRADE, Objective.PATROL)).head

        val texture = objective match {
          case Objective.PATROL => militaryCaravanTexture
          case _ => tradeCaravanTexture
        }

        sendExpedition(randomSourceCity.mapCell, randomDestCity.mapCell, texture, owner = ControlledBy.COMPUTER, units = scala.List[UnitType](), objective, speed = caravanSpeed)
      }
    }

  },0,1)

  def pauseGame(): Unit ={
    isPaused = !isPaused
  }


  val mapInputProcessor = new InputProcessor() {

    override def touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = {
      if (button == Input.Buttons.RIGHT) {
        mapRightClicked(screenX,screenY)
        true
      } else if (button == Input.Buttons.LEFT) {
          mapLeftClicked(screenX, screenY)
          true
      } else {
        false
      }
    }

    override def keyDown(keycode: Int): Boolean = {true}

    override def keyUp(keycode: Int): Boolean = {

      keycode match {
        case Input.Keys.U=>
          game.setScreen(new UnitConstructionScreen(game,MapScreen.this))
          true
        case Input.Keys.SPACE=>
          centerScreen()
          true
        case Input.Keys.P=>
          pauseGame()
          true
        case _=> false
      }
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

    if (game.DISABLE_FOG_OF_WAR){
      mapData.getCell(x,y).get.state = MapCellState.VISITED
    }

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

  var cameraCenterX = 0f
  var cameraCenterY = 0f

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

  def centerScreen(): Unit ={
    cameraCenterX = capitalCell.x * desertLayer.getTileWidth - desertLayer.getTileWidth/2
    cameraCenterY = capitalCell.y * desertLayer.getTileHeight - desertLayer.getTileHeight /2
  }



  override def show(): Unit = {

   Timer.instance().start()
    import com.badlogic.gdx.Gdx

    if (!game.NO_MUSIC){
      music.setLooping(true)
      music.setVolume(0.1f)
      music.play()
    }

    val multiplexer = new InputMultiplexer()
    multiplexer.addProcessor(stage)
    multiplexer.addProcessor(mapInputProcessor)
    Gdx.input.setInputProcessor(multiplexer)

    centerScreen()
    visitArea(capital.mapCell.x,capital.mapCell.y)

  }

  override def render( d: Float): Unit = {

    val _delta= if (isPaused) 0 else d

    Gdx.gl.glClearColor(1, 0, 0, 1)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT)
    stage.getBatch.setColor(Color.WHITE)

    if (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W)){
      cameraCenterY += 25
    }

    if (Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S)){
      cameraCenterY -= 25
    }

    if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)){
      cameraCenterX-=25
    }

    if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D)){
      cameraCenterX += 25
    }


    setCameraPosition(camera,_delta)
    stage.act(_delta)
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

  def visitArea(centerTileX:Int,centerTileY:Int): Unit ={
    //println(s"Called visitArea with coords $centerTileX,$centerTileY")
    for (
      x<-centerTileX-1 to centerTileX+1;
      y<-centerTileY-1 to centerTileY+1
    ) yield {
      //println(s"Calling visitTile with coords $x,$y")
      visitTile(x,y,1)
    }

  }


  def visitTile(tileX: Int, tileY: Int, radiusTiles:Int=DEFAULT_DISCOVERABLE_RADIUS)  {
    val tile = mapData.getCell(tileX,tileY) match {
      case Some(t)=>t
      case None=>
        //println(s"*** Tile not defined: $tileX,$tileY ****")
        return
    }

    tile.state = MapCellState.VISITED
    fogOfWarLayer.setCell(tileX,tileY,null)

    //removing actor over current tile if exists
    tileGroup.getChildren.items.filter(a=>a!=null).find(a=>{
      val userObject = Option(a.getUserObject)
      userObject match {
        case Some(coords: ActorMapCoords) =>
          coords.tileX == tileX && coords.tileY == tileY
        case _ =>
          false
      }
    }) match {
      case Some(a)=>
        //println(s"Removing actor over $tileX,$tileY")
        a.remove()
      case None=>
    }

    //getting covered tiles within radius
    val tilesAround = for (
      x<-tileX - radiusTiles to tileX + radiusTiles;
      y<-tileY - radiusTiles to tileY + radiusTiles
    ) yield {
      if (!(x == tileX && y == tileY)){
        val cell = mapData.getCell(x,y)
        cell
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


    if (!game.DISABLE_FOG_OF_WAR){
      renderer.renderTileLayer(fogOfWarLayer)
    }

    renderer.getBatch.end()

    stage.getBatch.begin()
    stage.getBatch.setProjectionMatrix(camera.combined)

    stage.getActors.toArray.filter(actor=>actor.isInstanceOf[ExpeditionActor]).foreach(a => {

      val expeditionActor = a.asInstanceOf[ExpeditionActor]
      val destinationPixelX = expeditionActor.destinationCell.x * desertLayer.getTileWidth + expeditionActor.getPrefWidth/2
      val destinationPixelY = expeditionActor.destinationCell.y * desertLayer.getTileHeight + expeditionActor.getPrefHeight/2

      val actor = expeditionActor
      if(actor.getX != destinationPixelX || actor.getY() != destinationPixelY) {

        val destination = new Vector2(destinationPixelX,destinationPixelY)
        val oldDirection = new Vector2(destination).sub(new Vector2(actor.getX,actor.getY)).nor()

        val deltaX = oldDirection.x * expeditionActor.speed * delta
        val deltaY = oldDirection.y * expeditionActor.speed * delta
        val newPositionX = actor.getX + deltaX
        val newPositionY = actor.getY + deltaY

        val newPos = new Vector2(newPositionX,newPositionY)

        val tileX = (newPositionX / desertLayer.getTileWidth).toInt
        val tileY = (newPositionY / desertLayer.getTileHeight).toInt

        val newDirection = new Vector2(destination).sub(newPos).nor()


        if (expeditionActor.owner == ControlledBy.PLAYER) {
          visitTile(tileX,tileY)
        }
        expeditionActor.moveBy(deltaX,deltaY)

        if (newDirection.hasSameDirection(oldDirection)){
          val cell = mapData.getCell(tileX,tileY).get
          if ((expeditionActor.owner == ControlledBy.COMPUTER && cell.state == MapCellState.VISITED) || expeditionActor.owner == ControlledBy.PLAYER) {
            actor.setVisible(true)
          } else {
            actor.setVisible(false)
          }
        } else {
          checkExpeditionTile(tileX,tileY,expeditionActor)
          expeditionActor.remove()
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

    if (mapDebugOutputEnabled){
      gameFont.draw(stage.getBatch,s"Camera position: ($cameraCenterX,$cameraCenterY), camera viewport size: ${camera.viewportWidth}/${camera.viewportHeight} ,player position: ($cameraCenterX,$cameraCenterY)",camera.unproject(new Vector3(0,0,0)).x,camera.unproject(new Vector3(0,0,0)).y)
    }
    stage.getBatch.end()

    val unitConstructionButtonCoords = camera.unproject(new Vector3(stage.getViewport.getScreenWidth - unitConstructionButton.getPrefWidth,stage.getViewport.getScreenHeight,0))
    unitConstructionButton.setPosition(unitConstructionButtonCoords.x,unitConstructionButtonCoords.y)
    centerOnCapitalButton.setPosition(unitConstructionButton.getX - centerOnCapitalButton.getPrefWidth-5,unitConstructionButton.getY)
    pauseButton.setPosition(centerOnCapitalButton.getX - pauseButton.getPrefWidth - 5,centerOnCapitalButton.getY)
  }

  override def resize(width: Int, height: Int): Unit = {}

  override def pause(): Unit = {}

  override def resume(): Unit = {}



  override def hide(): Unit = {
    Timer.instance().stop()
    music.stop()
  }

  override def dispose(): Unit = {
    map.dispose()
    renderer.dispose()
    music.dispose()
  }

  private def getClickInfo(cameraXPixel:Float,cameraYPixel:Float):MapClickInfo = {
    val coordX = camera.unproject(new Vector3(cameraXPixel,0,0)).x
    val coordY = camera.unproject(new Vector3(0,cameraYPixel,0)).y
    val clickedTileX = (coordX / desertLayer.getTileWidth).toInt
    val clickedTileY = (coordY / desertLayer.getTileHeight).toInt
    MapClickInfo(coordX,coordY,clickedTileX,clickedTileY)
  }

  private def mapLeftClicked(screenX:Int,screenY:Int): Unit ={

    //clearing dialogs
    stage
      .getActors
      .toArray
      .filter(a=>a.isInstanceOf[Dialog])
      .foreach(a=>
        a.remove()
      )

    val clickInfo = getClickInfo(screenX,screenY)
    val mapCell = mapData.getCell(clickInfo.tileX,clickInfo.tileY)
    if (mapCell.get.state != MapCellState.HIDDEN) {
      mapCell.get.location match {
        case Some(ci: CityInfo) => {
          game.setScreen(new CityScreen(ci, game, this))
        }
        case _ =>
      }
    }
  }

  private def mapRightClicked(screenX: Int, screenY: Int):Unit = {
    Gdx.app.log("INFO","Right clicked on map")

    val clickInfo = getClickInfo(screenX,screenY)

    val mapCell = mapData.getCell(clickInfo.tileX,clickInfo.tileY)

    if (mapCell.get.state != MapCellState.HIDDEN) {
      mapCell.get.location match {
        case Some(ri: RaiderCampInfo) => {
          game.setScreen(new ActionMixScreen(ri, game, this))
        }
        case Some(ci: CityInfo) => {
          if (!ci.isOwnedByPlayer){
            val dialog = new Dialog("", skin) {
              override def result(result:Object) {
                if (result.asInstanceOf[Boolean]) {

                }
                isPaused = false
              }
            }

            val table = dialog.getContentTable
            val availableUnitsTable = new Table().center()
            val actionMixTable = new Table().center()


            availableUnitsTable.add(soldierCard).pad(10,10,10,10)
            availableUnitsTable.add(engineerCard).pad(10,10,10,10)
            availableUnitsTable.add(scientistCard).pad(10,10,10,10)

            soldierCard.addCaptureListener(new ClickListener(Buttons.LEFT){
              override def clicked(event: InputEvent, x: Float, y: Float): Unit = {
                availableUnitsTable.removeActor(soldierCard)
                actionMixTable.add(soldierCard).pad(10,10,10,10)
                soldierCard.clearListeners()
//                soldierCard.addCaptureListener(new ClickListener(Buttons.LEFT){
//                  actionMixTable.removeActor(soldierCard)
//                  availableUnitsTable.add(soldierCard).pad(10,10,10,10)
//                })
              }
            })

            table.add(new Label(s"Design your action upon: ${ci.name}",skin)).fillX()
            table.row()
            table.add(new Label("Available units:",skin)).fillX()
            table.row()
            table.add(availableUnitsTable)
            table.row()
            table.add(new Label("Action mix units:",skin))
            table.row()
            table.add(actionMixTable)
            table.row()
            table.add(new Label("Outcome:",skin))

            dialog.button("OK", true).button("CANCEL",false)
            dialog.key(Keys.ESCAPE, false).key(Keys.ENTER, true)
            dialog.pack()
            isPaused = true
            stage.addActor(dialog)
            dialog.setPosition(cameraCenterX - dialog.getPrefWidth/2,cameraCenterY - dialog.getPrefHeight/2)
          }

        }
        case Some(ri: RuinsInfo) =>
          val dialog = new Dialog("", skin) {
            override def result(result:Object) {
              if (result.asInstanceOf[Boolean]) {
                game.setScreen(new ActionMixScreen(ri, game, MapScreen.this))
              }
            }
          }

          dialog.button("Send Expedition", true)
          dialog.key(Keys.ESCAPE, false).key(Keys.ENTER, true)
          dialog.pack()
          dialog.setModal(false)
          stage.addActor(dialog)
          dialog.setPosition(clickInfo.pixelX - dialog.getPrefWidth/2,clickInfo.pixelY - dialog.getPrefHeight/2)

        case _ =>
      }
    } else { //map cell is not discovered
        game.setScreen(new ActionMixScreen(CoveredAreaInfo(mapCell.get), game, this))
    }

  }

  def sendExpedition(originCell:MapCellData = mapData.getCell(capital.mapCell.x,capital.mapCell.y).get,
                     destCell:MapCellData,
                     texture:Texture = expeditionTexture,
                     owner:ControlledBy.ControlledBy = ControlledBy.PLAYER,
                     units:scala.List[UnitType],
                     objective: Objective.Objective,
                     speed: Int = 600
                    ): Unit ={

    val actor = new ExpeditionActor(
      game,
      this,
      Some(texture),
      originCell,
      destCell,
      owner = owner,
      units = units,
      objective = objective,
      speed = speed
    )
    val actorCoords = actor.screenToLocalCoordinates(new Vector2(originCell.x * desertLayer.getTileWidth  + actor.getPrefWidth/2,originCell.y * desertLayer.getTileHeight + actor.getPrefHeight/2))
    actor.setPosition(actorCoords.x,actorCoords.y)
    stage.addActor(actor)
  }

  private def discoverTile(x:Int,y:Int): Unit ={
    val t = mapData.getCell(x,y)
    if (t.isEmpty){
      println(s"*** Unable to discover: tile is empty ** $x,$y")
    }
    if (t.isDefined && t.get.state == MapCellState.HIDDEN){
//      println(s"Discovering tile $x,$y")
      fogOfWarLayer.setCell(x, y, null)
      t.get.state = MapCellState.DISCOVERED
      val image = Option(townLayer.getCell(x,y)) match {
        case Some(_)=>
          new Image(townLayer.getCell(x,y).getTile.getTextureRegion)
        case None=>new Image(desertLayer.getCell(x,y).getTile.getTextureRegion)
      }

      image.setColor(Color.GRAY)
      image.setUserObject(ActorMapCoords(x,y))
      tileGroup.addActor(image)
      val coords = new Vector3(x * desertLayer.getTileWidth, y * desertLayer.getTileHeight, 0)
      image.setPosition(coords.x, coords.y)
    }
  }




  private def checkExpeditionTile(tileX:Int,tileY:Int,expedition:ExpeditionActor): Unit ={
    if (expedition.owner == ControlledBy.COMPUTER) return
    val cell = mapData.getCell(tileX,tileY)
    cell match {
      case Some(c)=>
        c.location match {
          case Some(rci:RaiderCampInfo)=>{
            game.setScreen(new SituationScreen(rci,DefendersType.RAIDERS,game,this,expedition.units))
          }
          case Some(ci:CityInfo)=>{
            game.setScreen(new SituationScreen(ci,DefendersType.SOLDIERS,game,this,expedition.units))
          }
          case Some(ri:RuinsInfo)=>
            game.setScreen(new CardGameScreen(ri,game,this,expedition.units))

          case _=>
        }
      case None=>
        println(s"Unknown cell visited $tileX,$tileY")
    }


  }

  def discoverTech(ancientRuins:RuinsInfo): Unit ={
    locations -= ancientRuins
    ancientRuins.mapCell.location = None
    townLayer.setCell(ancientRuins.mapCell.x,ancientRuins.mapCell.y,null)

    val dialog = new Dialog("", skin) {
      override def result(result:Object) {

      }
    }
    val index = Random.nextInt(technologies.size)
    dialog.text(s"Technology discovered: ${technologies(index).name}")
    dialog.button("OK", true)
    dialog.key(Keys.ESCAPE, false).key(Keys.ENTER, true)
    dialog.getContentTable.pad(20)
    dialog.getTitleTable.pad(20)
    dialog.pack()
    stage.addActor(dialog)
    dialog.setPosition(cameraCenterX - dialog.getPrefWidth/2,cameraCenterY - dialog.getPrefHeight/2)
  }

  def conquerCity(city: CityInfo) = {
    city.isOwnedByPlayer = true
    visitArea(city.mapCell.x,city.mapCell.y)
  }




  def deleteCamp(camp: RaiderCampInfo) = {
    if (locations.contains(camp)){
      locations -= camp
      camp.mapCell.location = None
      val desertTile = new StaticTiledMapTile(new TextureRegion(desertTileTexture))
      val desertCell = new Cell
      desertCell.setTile(desertTile)
      townLayer.setCell(camp.mapCell.x,camp.mapCell.y,desertCell)
    }
  }



}

object MapScreen{


  case class MapClickInfo(pixelX:Float, pixelY: Float, tileX:Int,tileY:Int)
  case class ExpeditionActor(
                              originCell:MapCellData,
                              destinationCell:MapCellData,
                              actor:ExpeditionActor,
                              speed:Int = 600,
                              owner:ControlledBy.ControlledBy = ControlledBy.PLAYER,
                              objective:Objective.Objective,
                              units:scala.List[UnitType],
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
