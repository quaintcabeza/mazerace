package mazerace

import mazerace.MouseColor.MouseColor

object MouseColor extends Enumeration {
  type MouseColor = Value
  val BROWN, WHITE = Value
}

object Dir extends Enumeration {
  type Dir = Value
  val UP, DOWN, LEFT, RIGHT = Value
}

sealed trait MazeThing {}

case class Wall() extends MazeThing {}

case class Cell(row: Int, col: Int) extends MazeThing {}

case class Portal(color: MouseColor, cell: Cell) extends MazeThing {}

case class Mouse(color: MouseColor) extends MazeThing {}

case class Cheese() extends MazeThing {}
