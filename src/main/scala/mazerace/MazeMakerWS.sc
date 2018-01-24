import mazerace.{Cell, MazeState}

// Use worksheet for running eyeball tests
val obj = MazeState(4, 4)
val c1 = Cell(1,1)
val c2 = Cell(1,2)
val c3 = Cell(1,3)
obj.connect(c1, c2)
obj.getConnections(c1)
obj.visit(c1)
obj.visit(c2)
obj.getUnvisitedNeighbours(c1)
obj.getUnvisitedNeighbours(c2)
obj.getUnvisitedNeighbours(c3)
obj.getUnvisitedNeighbours(Cell(1,2))


