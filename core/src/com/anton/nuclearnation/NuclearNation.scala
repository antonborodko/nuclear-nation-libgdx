package com.anton.nuclearnation

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.{BitmapFont, SpriteBatch}


class NuclearNation extends Game {
  var batch:SpriteBatch = _
  var font:BitmapFont = _


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
