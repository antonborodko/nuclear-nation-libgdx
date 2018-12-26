package com.anton.nuclearnation

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.freetype.{FreeTypeFontGenerator, FreeTypeFontGeneratorLoader, FreetypeFontLoader}
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter
import com.badlogic.gdx.graphics.g2d.{BitmapFont, SpriteBatch}


class NuclearNation extends Game {
  var batch:SpriteBatch = _
  var font:BitmapFont = _
  val assetManager = new AssetManager
  val resolver = new InternalFileHandleResolver
  val fontGenerator = new FreeTypeFontGeneratorLoader(resolver)
  assetManager.setLoader(classOf[FreeTypeFontGenerator], fontGenerator)
  assetManager.setLoader(classOf[BitmapFont], ".ttf", new FreetypeFontLoader(resolver))

  val mySmallFont = new FreeTypeFontLoaderParameter()
  mySmallFont.fontFileName = "fonts/lunchtime-doubly-so/lunchds.ttf"
  mySmallFont.fontParameters.size = 30
  assetManager.load("fonts/lunchtime-doubly-so/lunchds.ttf", classOf[BitmapFont], mySmallFont)



  override def create(): Unit = {
    batch = new SpriteBatch
    font = new BitmapFont
    this.setScreen(new MapScreen(this))
  }


  override def render(): Unit = {
    super.render()
  }

  override def dispose(): Unit = {
    batch.dispose()
    font.dispose()
  }

}
