package com.anton.nuclearnation

import java.io.File

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.{ExternalFileHandleResolver, InternalFileHandleResolver}
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.freetype.{FreeTypeFontGenerator, FreeTypeFontGeneratorLoader, FreetypeFontLoader}
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter
import com.badlogic.gdx.graphics.g2d.{BitmapFont, SpriteBatch}
import com.badlogic.gdx.scenes.scene2d.ui.Skin


class NuclearNation extends Game {
  var batch:SpriteBatch = _
  val assetManager = new AssetManager

  val resolver = new InternalFileHandleResolver
  val fontGenerator = new FreeTypeFontGeneratorLoader(resolver)

  assetManager.setLoader(classOf[FreeTypeFontGenerator], fontGenerator)
  assetManager.setLoader(classOf[BitmapFont], ".ttf", new FreetypeFontLoader(resolver))

  val gameFontParam = new FreeTypeFontLoaderParameter()






  override def create(): Unit = {
    batch = new SpriteBatch


    gameFontParam.fontFileName = "fonts/lunchtime-doubly-so/lunchds.ttf"
    gameFontParam.fontParameters.size = 30
    assetManager.load("fonts/lunchtime-doubly-so/lunchds.ttf", classOf[BitmapFont], gameFontParam)

    assetManager.load("raider_camp.png",classOf[Texture])
    assetManager.load("desert_tile.png",classOf[Texture])
    assetManager.load("fog_of_war_tile.png",classOf[Texture])
    assetManager.load("town.png",classOf[Texture])
    assetManager.load("raider-facing-left.png",classOf[Texture])
    assetManager.load("soldier-facing-right.png",classOf[Texture])
    assetManager.finishLoading()

    this.setScreen(new MapScreen(this))
  }


  override def render(): Unit = {
    super.render()
  }

  override def dispose(): Unit = {
    batch.dispose()
    assetManager.dispose()
  }

}
