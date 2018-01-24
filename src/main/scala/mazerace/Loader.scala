package mazerace

import scala.concurrent.Future
import org.scalajs.dom
import org.scalajs.dom.html
import org.scalajs.dom.raw.HTMLImageElement

import scala.concurrent.Promise
import scala.concurrent.ExecutionContext.Implicits.global

object Loader {
  private def loadImage(fileName: String): Future[HTMLImageElement] = {
    // Asynchronously fetch image
    val promise = Promise[HTMLImageElement]()
    val img = dom.document.createElement("img").asInstanceOf[HTMLImageElement]
    img.onload = (e: dom.Event) => {
      promise.success(img)
    }
    try {
      img.src = fileName
    }
    catch {
      case ex => promise.failure(ex)
    }
    promise.future
  }

  def loadPainter(canvas: html.Canvas): Future[Painter] = {
    val renderer = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

    // Fetch images asynchronously, then make a Painter
    val brownMouse = loadImage("brown_mouse.png")
    val whiteMouse = loadImage("white_mouse.png")
    val cheese     = loadImage("cheese.png")
    for {
      brownMouseImg <- brownMouse
      whiteMouseImg <- whiteMouse
      cheeseImg     <- cheese
      mice = Map(
        MouseColor.BROWN -> brownMouseImg,
        MouseColor.WHITE -> whiteMouseImg
      )
    } yield Painter(renderer, mice, cheeseImg, canvas.width, canvas.height)
  }
}
