package mazerace
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom
import org.scalajs.dom.html

import scala.scalajs.js
import scala.util.Random

@JSExport
object Mazerace {
  @JSExport
  def main(canvas: html.Canvas): Unit = {

    // set up
    val renderer = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]
    canvas.width = canvas.parentElement.clientWidth
    canvas.height = 400

    renderer.font = "50px sans-serif"
    renderer.textAlign = "center"
    renderer.textBaseline = "middle"
    renderer.fillStyle = "black"
    renderer.fillText("Hello World!", canvas.width/2, canvas.height/2)

    canvas.focus()
  }
}
