package com.anton.nuclearnation

import com.anton.nuclearnation.UnitType.UnitType
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.{Dialog, Image, Label}
import com.badlogic.gdx.scenes.scene2d.utils.{ClickListener, SpriteDrawable}

class ExpeditionActor(
                       game:NuclearNation,
                       mapScreen:MapScreen,
                       texture:Option[Texture] = None,
                       val originCell:MapCellData,
                       val destinationCell:MapCellData,
                       val speed:Int = 600,
                       val owner:ControlledBy.Value = ControlledBy.PLAYER,
                       val objective:Objective.Objective,
                       val units:scala.List[UnitType],
                     ) extends Image(texture.getOrElse(game.assetManager.get("tradeCaravan.png",classOf[Texture]))
){
  addCaptureListener(new ClickListener(){
    override def clicked(event: InputEvent, x: Float, y: Float): Unit = {
      super.clicked(event, x, y)
      mapScreen.isPaused = true

      val dialog = new Dialog("", game.skin) {
        override def result(result:Object) {
          mapScreen.isPaused = false
        }
      }
      dialog.button("OK", true)
      dialog.key(Keys.ESCAPE, false).key(Keys.ENTER, true)

      dialog.getContentTable.pad(20)
      dialog.getTitleTable.pad(20)
      val title = objective match {
        case Objective.TRADE => "Trade caravan"
        case Objective.COMBAT => "Military unit"
        case Objective.PATROL => "Route patrol"
        case _ => "Unknown expedition"
      }
      dialog.getContentTable.add(new Label(title,game.skin)).fillX()
      dialog.getContentTable.row()
      dialog.getContentTable.add(new Label(s"Origin: ${originCell.location.get.name}",game.skin)).fillX()
      dialog.getContentTable.row()
      dialog.getContentTable.add(new Label(s"Destination: ${destinationCell.location.get.name}",game.skin)).fillX()

      dialog.pack()
      mapScreen.stage.addActor(dialog)
    }
  })
}
