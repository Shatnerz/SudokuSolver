Solution Method - combination of applying constraints and backtracking

First apply these two simple constraints[1]
(1) If a square has only one possible value, then eliminate that value from the square's peers.
(2) If a unit has only one possible place for a value, then put the value there.

Then, if not solved already, find the square with the fewest possible choices.
Try each choice (in numerical order), repeating above steps, until finding a solution

**How reverting the changes works
Before each change we push the box to a stack and the values to a separate stack.
When the list of unsolved Boxes is changed, we push a specific box to the box stack and the unsolved list to a separate stack.
During the search() function, we push a bookmark Box to the box stack
revert() then pops the box stack, if its not a special (bookmark) box, we set the values of said box to the pop of the values stack
If it the popped box calls for changing the unsolved list, we update the unsolved list to the pop of the unsolved stack
If the popped box is a bookmark (breaker) box, we stop reverting the changes

**Some terms I used - in case they arent defined well everywhere
Box - place where a number can go
values - all possible numbers that A box can legally have with the current board
peers - other Boxes which affect the current Box we talking about
unit - a group of N boxes which must contain numbers 1 to N. Each board has 3N units, one for each column/row and one for each unique SIZE by SIZE square
Grid - board or grid of numbers

**Lastly - those 2 constraints were taken straight from Norvig. They are a well written explanation of the simplest ideas used in sudoku.

**Acknowledgements
Peter Norvig[1] for presenting a well written, concise example in python of the depth first search method used to solve 3x3 puzzles. This helped keep my code organized and help me use sensible names for methods and variables.
Sudoku dragon[2] for documenting human used Sudoku strategies
Wikipedia page of Sudoku solving alogrithms[3] for giving a quick breakdown on the effectiveness and speed of each method
website[4] and papers [5] and [6] for convincing me not to spend more time trying for a stochastic approach better than my current approach

**Refs
[1] http://norvig.com/sudoku.html
[2] http://www.sudokudragon.com/sudokustrategy.htm
[3] http://en.wikipedia.org/wiki/Sudoku_solving_algorithms
[4] http://home.iitk.ac.in/~pratikkr/cs365/projects/
[5] http://home.iitk.ac.in/~pratikkr/cs365/projects/report.pdf
[6] http://orca.cf.ac.uk/27751/1/LEWIS%20on%20the%20combination%20of%20constraint%20programming%20and%20the%20stochasitc%20search%20the%20sudoku%20case.pdf

