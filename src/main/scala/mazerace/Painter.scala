package mazerace
import mazerace.MouseColor.MouseColor
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLImageElement

case class Painter(renderer: dom.CanvasRenderingContext2D,
                   miceImages: Map[MouseColor, HTMLImageElement],
                   cheeseImage: HTMLImageElement,
                   width: Double, // canvas width
                   height: Double // canvas height
                  ) {
  // This class provides methods to draw game objects
  // on a canvas

  class PainterHelper(layout: Layout) {
    val maze = layout.maze
    val cellX: Double = width / maze.COLS
    val cellY: Double = height / maze.ROWS
    val padding: Int = 10
    val imgX: Double = cellX - 2 * padding
    val imgY: Double = cellY - 2 * padding
    val bannerX: Double = width * 2/3
    val bannerY: Double = height * 1/3

    val backgroundColor = "#e8e8e8"
    val gridLineColor = "#c0c0c0"
    val wallColor = "#000000"
    val mouseColorToColor = Map(
      MouseColor.BROWN -> "#d2b486",
      MouseColor.WHITE -> "white"
    )

    def drawBackground(): Unit = {
      // background
      renderer.fillStyle = backgroundColor
      renderer.fillRect(0, 0, width, height)

      // border
      renderer.strokeStyle = wallColor
      renderer.strokeRect(0, 0, width, height)
    }

    def drawGridLines(): Unit = {
      renderer.strokeStyle = gridLineColor
      // horizontal grid lines
      (1 until maze.ROWS).foreach(row => {
        renderer.beginPath()
        renderer.moveTo(0, row * cellY)
        renderer.lineTo(width, row * cellY)
        renderer.stroke()
      })
      // vertical grid lines
      (1 until maze.COLS).foreach(col => {
        renderer.beginPath()
        renderer.moveTo(col * cellX, 0)
        renderer.lineTo(col * cellX, height)
        renderer.stroke()
      })
    }

    def drawWalls(): Unit = {
      renderer.strokeStyle = wallColor
      layout.horizontalWalls.foreach {
        case Cell(row, col) => {
          renderer.beginPath()
          renderer.moveTo(col * cellX, row * cellY)
          renderer.lineTo((col + 1) * cellX, row * cellY)
          renderer.stroke()
        }
      }
      layout.verticalWalls.foreach {
        case Cell(row, col) => {
          renderer.beginPath()
          renderer.moveTo(col * cellX, row * cellY)
          renderer.lineTo(col * cellX, (row + 1) * cellY)
          renderer.stroke()
        }
      }
    }

    def drawCircle(color: String, cell: Cell): Unit = {
      renderer.fillStyle = color
      renderer.strokeStyle = wallColor
      renderer.beginPath()
      renderer.arc(
        cell.col * cellX + cellX / 2,
        cell.row * cellY + cellY / 2,
        10,
        0,
        2 * Math.PI)
      renderer.fill()
      renderer.stroke()
    }

    def drawImage(img: HTMLImageElement, cell: Cell): Unit = {
      renderer.drawImage(img,
        cell.col * cellX + padding,
        cell.row * cellY + padding,
        imgX,
        imgY)
    }

    def drawPortals(): Unit = {
      layout.portals.foreach {
        case (color, cells) => cells.foreach(
          cell => drawCircle(
            mouseColorToColor.getOrElse(color, "red"),
            cell
          )
        )
      }
    }

    def drawMice(): Unit = {
      layout.mice.foreach {
        case (color, cell) => {
          val image = miceImages.get(color)
          image match {
            case Some(img) => drawImage(img, cell)
            // None --> will result in error
          }
        }
      }
    }

    def drawCheese(): Unit = {
      drawImage(cheeseImage, layout.cheese)
    }

    def drawWinnerBanner(mouseColor: MouseColor): Unit = {
      renderer.fillStyle = backgroundColor
      renderer.strokeStyle = wallColor
      renderer.fillRect(
        width/2 - bannerX/2,
        height/2 - bannerY/2,
        bannerX,
        bannerY
      )
      renderer.strokeRect(
        width/2 - bannerX/2,
        height/2 - bannerY/2,
        bannerX,
        bannerY
      )
      renderer.textAlign = "center"
      renderer.fillStyle = wallColor
      renderer.font = "30px Arial"
      renderer.fillText(s"${mouseColor.toString.toLowerCase.capitalize} Mouse Wins!",
        width/2,
        height/2
      )
    }
  }

  def drawLayout(layout: Layout): Unit = {
    val helper = new PainterHelper(layout)
    helper.drawBackground()
    helper.drawGridLines()
    helper.drawWalls()
    helper.drawPortals()
    helper.drawCheese()
    helper.drawMice()

    layout match {
      case endGame: EndGameLayout =>
        helper.drawWinnerBanner(endGame.winner)
      case _ =>
    }
  }
}
