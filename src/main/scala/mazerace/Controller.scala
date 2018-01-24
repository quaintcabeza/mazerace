package mazerace

import mazerace.Dir.Dir
import mazerace.MouseColor.MouseColor
import scala.util.Random

class GenerateLayout(ROWS: Int, COLS: Int, numPortals: Int) {
  private val gen = new Random()

  private def generatePortals(): Set[Cell] = {
    (for {
      i <- 0 until numPortals
      row = gen.nextInt(ROWS)
      col = gen.nextInt(COLS)
      cell = Cell(row, col)
    } yield cell).toSet
  }

  // public data
  val mice: Map[MouseColor, Cell] = Map(
    MouseColor.BROWN -> Cell(0, 0),
    MouseColor.WHITE -> Cell(0, COLS-1)
  )

  // Put cheese anywhere but the first row
  val cheese: Cell = Cell(gen.nextInt(ROWS - 1) + 1, gen.nextInt(COLS))

  val portals: Map[MouseColor, Set[Cell]] =
    MouseColor.values.foldLeft(Map[MouseColor, Set[Cell]]())(
      (map, color) => map + (color -> generatePortals()))
}


case class Controller(maze: Maze, painter: Painter) {
  // This class generates a layout for the game,
  // and provides a method to handle user input

  val numPortals = 10
  val initialState = new GenerateLayout(maze.ROWS, maze.COLS, numPortals)

  val keyMap: Map[String, (MouseColor, Dir)] = Map(
    "w" -> (MouseColor.BROWN, Dir.UP),
    "s" -> (MouseColor.BROWN, Dir.DOWN),
    "a" -> (MouseColor.BROWN, Dir.LEFT),
    "d" -> (MouseColor.BROWN, Dir.RIGHT),
    "i" -> (MouseColor.WHITE, Dir.UP),
    "k" -> (MouseColor.WHITE, Dir.DOWN),
    "j" -> (MouseColor.WHITE, Dir.LEFT),
    "l" -> (MouseColor.WHITE, Dir.RIGHT)
  )

  private var layout: Layout = GameLayout(
    maze,
    initialState.mice,
    initialState.cheese,
    initialState.portals
  )

  painter.drawLayout(layout) // draw game layout on canvas

  private def handleAction(color: MouseColor, dir: Dir): Unit = {
    val newLayout = layout.moveMouse(color, dir)
    newLayout match {
      case gameOver: EndGameLayout =>
        println("Game over!")
        layout = gameOver
        painter.drawLayout(layout)

      case gameOn: GameLayout =>
        if (gameOn eq layout) {
          // Layout object didn't change.
          // Mouse must have bumped into obstacle.
          println("bump!")
        }
        else {
          // Update layout and redraw.
          layout = gameOn
          painter.drawLayout(layout)
        }
    }
  }

  def handle(key: String): Unit = {
    val action = keyMap.get(key)
    action match {
      case Some((color, dir)) =>
        handleAction(color, dir)
      case None =>
    }
  }
}
