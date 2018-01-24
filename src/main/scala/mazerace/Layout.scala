package mazerace

import mazerace.Dir.Dir
import mazerace.MouseColor.MouseColor

import scala.util.Random

trait Layout {
  val maze: Maze
  val mice: Map[MouseColor, Cell]
  val cheese: Cell
  val portals: Map[MouseColor, Set[Cell]]
  val horizontalWalls: Seq[Cell] = maze.horizontalWalls
  val verticalWalls: Seq[Cell] = maze.verticalWalls
  def moveMouse(mouseColor: MouseColor, dir: Dir): Layout
}

case class GameLayout(override val maze: Maze,
                      override val mice: Map[MouseColor, Cell],
                      override val cheese: Cell,
                      override val portals: Map[MouseColor, Set[Cell]]) extends Layout {

  // private data
  private val specialCellMap: Map[Cell, MazeThing] =
    // map that tells you where all the special maze
    // objects are: mice, cheese, portals etc
    mice.map {
      case (color, cell) => cell -> Mouse(color)
    } ++
    portals.flatMap {
      case (color, cells) => cells.map(cell => cell -> Portal(color, cell))
    } +
    { cheese -> Cheese()}


  // private methods
  private def getNeighbor(cell: Cell, dir: Dir): MazeThing = {
    val n = maze.getNeighbor(cell, dir)
    n match {
      case Wall() => Wall()
      case cell: Cell =>
        specialCellMap.getOrElse(cell, cell)
    }
  }

  private def getRandomCell: Cell = {
    val gen = new Random()
    val res = Cell(gen.nextInt(maze.ROWS), gen.nextInt(maze.COLS))
    if (specialCellMap.contains(res)) {
      // try again
      getRandomCell
    }
    else {
      res
    }
  }


  // public methods
  def moveMouse(mouseColor: MouseColor, dir: Dir): Layout = {
    // Move mouse of color 'mouseColor' in the specified
    // direction 'dir'.

    val pos = mice.getOrElse(mouseColor, Cell(0, 0))
    val next = getNeighbor(pos, dir)
    next match {
      case Wall() =>
        // bumped into wall
        this

      case Mouse(_) =>
        // bumped into other mouse
        this

      case Cheese() =>
        // game over
        EndGameLayout(
          mouseColor,
          maze,
          mice.updated(mouseColor, cheese),
          cheese,
          portals)

      case Portal(portalColor, cell) =>
        val portalsForColor = portals.getOrElse(portalColor, Set())
        this.copy(
          mice = mice
            .updated(mouseColor, cell)
            .updated(portalColor, getRandomCell),
          portals = portals.updated(portalColor, portalsForColor - cell)
        )

      case cell: Cell =>
        this.copy(
          mice = mice.updated(mouseColor, cell)
        )
    }
  }
}

case class EndGameLayout(winner: MouseColor,
                         override val maze: Maze,
                         override val mice: Map[MouseColor, Cell],
                         override val cheese: Cell,
                         override val portals: Map[MouseColor, Set[Cell]]
                        ) extends Layout {

  def moveMouse(mouseColor: MouseColor, dir: Dir): Layout = this
}

