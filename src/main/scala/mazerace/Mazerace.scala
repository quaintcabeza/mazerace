package mazerace

import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom
import org.scalajs.dom.html
import org.scalajs.dom.raw.{HTMLImageElement, KeyboardEvent}
import scala.concurrent.ExecutionContext.Implicits.global

@JSExport
object Mazerace {
  @JSExport
  def main(canvas: html.Canvas): Unit = {

    // set up
    val renderer = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]
    val side = 900
    val cols = 20
    val rows = 15
    val aspect: Double = cols.toDouble/rows
    canvas.width = (side * aspect).toInt
    canvas.height = side

    val maze = MazeMaker.balancedMake(rows, cols)
    val painterF = Loader.loadPainter(canvas)
    painterF.onComplete {
      case scala.util.Success(painter) =>
        val controller = Controller(maze, painter)
        canvas.onkeypress = (e: KeyboardEvent) => {
          controller.handle(e.key)
        }

      case scala.util.Failure(ex) =>
        println(ex)
    }

    canvas.focus()
  }
}
