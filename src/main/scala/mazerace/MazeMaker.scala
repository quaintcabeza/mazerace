package mazerace

import scala.collection.mutable
import scala.util.Random

case class MazeState(ROWS: Int,
                     COLS: Int,
                     connections: Map[Cell, Set[Cell]] = Map(),
                     visited: Set[Cell] = Set()) {

  def getConnections(cell: Cell): Set[Cell] = {
    // Get the set of cells connected to 'cell'.
    connections.getOrElse(cell, Set())
  }

  private def oneWayConnect(c1: Cell, c2: Cell): MazeState = {
    // Add a connection from 'c1' to 'c2'.
    val cons = getConnections(c1)
    this.copy(connections = connections.updated(c1, cons + c2))
  }

  def connect(cell1: Cell, cell2: Cell): MazeState = {
    // Add a connection from 'c1' to 'c2' and 'c2' to 'c1'.
    oneWayConnect(cell1, cell2).oneWayConnect(cell2, cell1)
  }

  def areConnected(cell1: Cell, cell2: Cell): Boolean = {
    // Check if a two-way connection exists between
    // 'cell1' and 'cell2'.
    getConnections(cell1).contains(cell2) &&
    getConnections(cell2).contains(cell1)
  }

  def visit(cell: Cell): MazeState = {
    // Mark 'cell' as visited.
    this.copy(visited = visited + cell)
  }

  def getNeighbours(cell: Cell): Seq[Cell] = {
    // Get all neighbouring cells of 'cell'.
    val row = cell.row
    val col = cell.col
    (for {
      c <- List(col - 1, col + 1)
      if (c >= 0 && c < COLS)
    } yield Cell(row, c)) ++
      (for {
        r <- List(row - 1, row + 1)
        if (r >= 0 && r < ROWS)
      } yield Cell(r, col))
  }

  def getUnvisitedNeighbours(cell: Cell): Seq[Cell] = {
    // Get all neighbouring cells of 'cell' that have
    // not been visited.
    getNeighbours(cell).filterNot(visited.contains)
  }

  def getRandomUnvisitedNeighbour(cell: Cell): Option[Cell] = {
    // Get a random neighbouring cell of 'cell' that has
    // not been visited.
    val neighbours = getUnvisitedNeighbours(cell)
    if (neighbours.isEmpty) {
      None
    }
    else {
      val gen = new Random();
      Some(neighbours(gen.nextInt(neighbours.length)))
    }
  }

  def dfs(curr: Cell): MazeState = {
    // Generate a maze by running a depth first algorithm
    // to recursively connect and visit random neighbours,
    // starting with 'curr'.
    val next = getRandomUnvisitedNeighbour(curr)
    next match {
      case Some(n: Cell) => {
        visit(curr).connect(curr, n).dfs(n).dfs(curr)
        // - mark 'curr' as 'visited'
        // - connect 'curr' and a random neighbour 'n'
        // - recursively run dfs on 'n'
        // - then run dfs again on 'curr', in case there
        //   are still some unvisited neighbours
      }
      case None => visit(curr)
        // - mark 'curr' as 'visited' and return
    }
  }

  def shiftBy(row: Int, col: Int): MazeState = {
    // Shift all rows by 'row' and all columns by 'col'.
    def shift(cell: Cell): Cell = {
      Cell(cell.row + row, cell.col + col)
    }

    this.copy(
      connections = connections.map {
        case (c, conns) => (shift(c) -> conns.map(shift))
      },
      visited = visited.map(shift)
    )
  }

  def combine(insertAt: Cell, other: MazeState): MazeState = {
    // Create a new maze by inserting 'other' at 'insertAt'.
    MazeState(
      Math.max(ROWS, insertAt.row + other.ROWS),
      Math.max(COLS, insertAt.col + other.COLS),
      connections ++ other.connections,
      visited ++ other.visited
    )
  }
}

object MazeMaker {
  // This class provides methods that can be used
  // to generate a maze

  val balancedMakeThreshold = 6
  val gen = new Random();

  private def runMake(rows: Int, cols: Int): MazeState = {
    // Generate a maze using the 'dfs' algorithm.
    // Return a 'MazeState' object.
    val state = MazeState(rows, cols);
    val initialCell = Cell(gen.nextInt(rows), gen.nextInt(cols))
    state.dfs(initialCell)
  }

  def make(rows: Int, cols: Int): Maze = {
    // Generate a maze using the 'dfs' algorithm.
    val state = runMake(rows, cols)
    Maze(rows, cols, state.connections)
  }

  private def runBalancedMake(rows: Int, cols: Int): MazeState = {
    if (rows > balancedMakeThreshold) {
      // split up rows, and generate two smaller mazes
      val R2 = rows/2
      val s00 = runBalancedMake(R2, cols)
      val s10 = runBalancedMake(rows - R2, cols).shiftBy(R2, 0)

      // combine mazes
      val s = s00.combine(Cell(R2, 0), s10)

      // Now add connecting paths between the two mazes.
      // The number of paths should be at least 1, and is
      // determined by the length of the columns
      val numPaths = Math.max(1, cols/balancedMakeThreshold)
      (0 until numPaths).foldLeft(s)((state, i) => {
        // Add path from random cell in row R2 to its top neighbour
        val cell = Cell(R2, gen.nextInt(cols/numPaths) + i * cols/numPaths)
        state.connect(cell, Cell(cell.row - 1, cell.col))
      })
    }

    else if (cols > balancedMakeThreshold) {
      // split up cols, and generate two smaller mazes
      val C2 = cols/2
      val s00 = runBalancedMake(rows, C2)
      val s01 = runBalancedMake(rows, cols - C2).shiftBy(0, C2)

      // combine mazes
      val s = s00.combine(Cell(0, C2), s01)

      // Now add connecting paths between the two mazes.
      // The number of paths should be at least 1, and is
      // determined by the length of the rows
      val numPaths = Math.max(1, rows/balancedMakeThreshold)
      (0 until numPaths).foldLeft(s)((state, i) => {
        // Add path from random cell in col C2 to its left neighbour
        val cell = Cell(gen.nextInt(rows/numPaths) + i * rows/numPaths, C2)
        state.connect(cell, Cell(cell.row, cell.col - 1))
      })
    }
    else {
      // Run regular algorithm
      runMake(rows, cols)
    }
  }

  def balancedMake(rows: Int, cols: Int): Maze = {
    // Running 'dfs' on the entire maze can lead to long
    // corridors and tired mice. This algorithm generates
    // smaller maze chunks, and then stitches them up into
    // the big maze
    val state = runBalancedMake(rows, cols)
    Maze(rows, cols, state.connections)
  }
}
