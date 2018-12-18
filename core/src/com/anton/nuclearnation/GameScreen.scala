//package com.anton.nuclearnation
//
//import java.util
//
//import com.badlogic.gdx.{Gdx, Input, Screen}
//import com.badlogic.gdx.audio.{Music, Sound}
//import com.badlogic.gdx.graphics.{GL20, OrthographicCamera, Texture}
//import com.badlogic.gdx.math.{MathUtils, Rectangle, Vector3}
//import com.badlogic.gdx.utils.{Array, TimeUtils}
//import scala.collection.JavaConverters
//
//class GameScreen(val game: NuclearNation) extends Screen { // load the images for the droplet and the bucket, 64x64 pixels each
//  private var dropImage: Texture = _
//  private var bucketImage: Texture = _
//  private var dropSound: Sound = _
//  private var rainMusic: Music = _
//  private var camera: OrthographicCamera = _
//  private var bucket: Rectangle = _
//  private var raindrops: Array[Rectangle] = _
//  private var lastDropTime: Long = 0L
//  private var dropsGathered: Int = 0
//
//  dropImage = new Texture(Gdx.files.internal("droplet.png"))
//  bucketImage = new Texture(Gdx.files.internal("bucket.png"))
//  // load the drop sound effect and the rain background "music"
//  dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"))
//  rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"))
//  rainMusic.setLooping(true)
//  // create the camera and the SpriteBatch
//  camera = new OrthographicCamera
//  camera.setToOrtho(false, 800, 480)
//  // create a Rectangle to logically represent the bucket
//  bucket = new Rectangle
//  bucket.x = 800 / 2 - 64 / 2 // center the bucket horizontally
//
//  bucket.y = 20 // bottom left corner of the bucket is 20 pixels above
//
//  // the bottom screen edge
//  bucket.width = 64
//  bucket.height = 64
//  // create the raindrops array and spawn the first raindrop
//  raindrops = new Array[Rectangle]
//
//  spawnRaindrop()
//
//
//  private def spawnRaindrop(): Unit = {
//    val raindrop: Rectangle = new Rectangle
//    raindrop.x = MathUtils.random(0, 800 - 64)
//    raindrop.y = 480
//    raindrop.width = dropImage.getWidth
//    raindrop.height = dropImage.getHeight
//    raindrops.add(raindrop)
//    lastDropTime = TimeUtils.nanoTime
//  }
//
//
//
//  override def render(delta: Float): Unit = { // clear the screen with a dark blue color. The
//    // arguments to glClearColor are the red, green
//    // blue and alpha component in the range [0,1]
//    // of the color to be used to clear the screen.
//    Gdx.gl.glClearColor(0, 0, 0.2f, 1)
//    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
//    // tell the camera to update its matrices.
//    camera.update()
//    // tell the SpriteBatch to render in the
//    // coordinate system specified by the camera.
//    game.batch.setProjectionMatrix(camera.combined)
//    // begin a new batch and draw the bucket and
//    // all drops
//    game.batch.begin()
//    game.font.draw(game.batch, "Drops Collected: " + dropsGathered, 0, 480)
//    game.batch.draw(bucketImage, bucket.x, bucket.y, bucket.width, bucket.height)
//
//    for (raindrop <- raindrops.toArray) {
//      game.batch.draw(dropImage, raindrop.x, raindrop.y)
//    }
//    game.batch.end()
//    // process user input
//    if (Gdx.input.isTouched) {
//      val touchPos: Vector3 = new Vector3
//      touchPos.set(Gdx.input.getX, Gdx.input.getY, 0)
//      camera.unproject(touchPos)
//      bucket.x = touchPos.x - 64 / 2
//    }
//    if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket.x -= 200 * Gdx.graphics.getDeltaTime
//    if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket.x += 200 * Gdx.graphics.getDeltaTime
//    // make sure the bucket stays within the screen bounds
//    if (bucket.x < 0) bucket.x = 0
//    if (bucket.x > 800 - 64) bucket.x = 800 - 64
//    // check if we need to create a new raindrop
//    if (TimeUtils.nanoTime - lastDropTime > 1000000000) spawnRaindrop()
//    // move the raindrops, remove any that are beneath the bottom edge of
//    // the screen or that hit the bucket. In the later case we increase the
//    // value our drops counter and add a sound effect.
//    val iter: util.Iterator[Rectangle] = raindrops.iterator
//    while ( {
//      iter.hasNext
//    }) {
//      val raindrop: Rectangle = iter.next
//      raindrop.y -= 200 * Gdx.graphics.getDeltaTime
//      if (raindrop.y + dropImage.getHeight < 0) iter.remove()
//      if (raindrop.overlaps(bucket)) {
//        dropsGathered += 1
//        dropSound.play
//        iter.remove()
//      }
//    }
//  }
//
//  override def resize(width: Int, height: Int): Unit = {
//  }
//
//  override def show(): Unit = { // start the playback of the background music
//    // when the screen is shown
//    rainMusic.play()
//  }
//
//  override def hide(): Unit = {
//  }
//
//  override def pause(): Unit = {
//  }
//
//  override def resume(): Unit = {
//  }
//
//  override def dispose(): Unit = {
//    dropImage.dispose()
//    bucketImage.dispose()
//    dropSound.dispose()
//    rainMusic.dispose()
//  }
//}