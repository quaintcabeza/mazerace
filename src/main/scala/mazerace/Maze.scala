package mazerace

import mazerace.Dir.Dir

case class Maze(ROWS: Int, COLS: Int, connections: Map[Cell, Set[Cell]]) {
  lazy val verticalWalls: Seq[Cell] = for {
    // Return a list of cells that have a wall
    // to their left.
    row <- 0 until ROWS
    col <- 1 until COLS
    left = Cell(row, col - 1)
    cell = Cell(row, col)
    if !areConnected(left, cell)
  } yield cell

  lazy val horizontalWalls: Seq[Cell] = for {
    // Return a list of cells that have a wall
    // to their top.
    row <- 1 until ROWS
    col <- 0 until COLS
    top = Cell(row - 1, col)
    cell = Cell(row, col)
    if !areConnected(top, cell)
  } yield cell

  def areConnected(cell1: Cell, cell2: Cell): Boolean = {
    connections.getOrElse(cell1, Set()).contains(cell2)
  }

  def getNeighbor(cell: Cell, dir: Dir): MazeThing = {
    def checkNeighborConnected(n: Cell): MazeThing = {
      if (areConnected(cell, n)) n
      else Wall()
    }

    dir match {
      case Dir.LEFT => checkNeighborConnected(
        Cell(cell.row, cell.col - 1))
      case Dir.RIGHT => checkNeighborConnected(
        Cell(cell.row, cell.col + 1))
      case Dir.UP => checkNeighborConnected(
        Cell(cell.row - 1, cell.col))
      case Dir.DOWN => checkNeighborConnected(
        Cell(cell.row + 1, cell.col))
    }
  }
}

