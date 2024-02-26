package com.example.assignment2

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.widget.GridLayout
import androidx.core.content.ContextCompat
import kotlin.random.Random

class Match4(private var view: View) : IGame {
    // game board in 2D array initialized to zeros
    private val board = Array(GameConstants.ROWS) { IntArray(GameConstants.COLS) { 0 } }

    //holds data on the location of a computer move as well as if its stale
    //moves are stale when there are no viable open slots next to them
    private class Move(cell: Int) {
        var location = cell;
        var isStale = false;
    }

    //list that holds all computer moves
    private val computerMoveIndex: MutableList<Move> = ArrayList();

    override fun clearBoard() {
        //iterates every square and sets to empty
        for (rowIndex in 0..<GameConstants.ROWS) {
            for (colIndex in 0..<GameConstants.COLS) {
                board[rowIndex][colIndex] = GameConstants.EMPTY;
            }
        }
    }

    //gets location and translates into coords for board
    override fun setMove(player: Int, location: Int) {
        val row = location / GameConstants.ROWS
        val col = location % GameConstants.COLS
        if (board[row][col] == GameConstants.EMPTY){
            board[row][col] = player;
        }
    }


    override val computerMove: Int
        get() {
            //if computer has not placed first move
            if (computerMoveIndex.isEmpty()) {
                val newMove = Move(Random.nextInt(36))
                computerMoveIndex += newMove
                return newMove.location
            }
            //generates next move
            else {
                //creates new move object that retrieves next cell based on algorithm of getNextMove()
                val newMove = Move(getNextMove);
                //adds new move to index of all computer moves
                computerMoveIndex += newMove;
                //returns new placement by computer of move
                return newMove.location;
            }
            //no open cells
            return -1
        }

    //follows algorithm to get the next computer move
    private val getNextMove: Int
        //goes through array of computer moves, from most recent to oldest
        get() {
            for (index in computerMoveIndex.size - 1 downTo 0) {
                val move = computerMoveIndex[index];
                //if move has open slots
                if (!move.isStale) {
                    //checks right cell and if open chooses it
                    if (checkRightCell(move) == GameConstants.EMPTY) {
                        return move.location + 1;
                    }
                    //checks left cell and if open chooses it
                    else if (checkLeftCell(move) == GameConstants.EMPTY) {
                        return move.location - 1;
                    }
                    //checks left diagonal cell and if open chooses it
                    else if (checkTopLeftDiag(move) == GameConstants.EMPTY) {
                        return move.location - 7;
                    }
                    //checks right diagonal cell and if open chooses it
                    else if (checkTopRightDiag(move) == GameConstants.EMPTY) {
                        return move.location - 5;
                    }
                    else if (checkBottomRightDiag(move) == GameConstants.EMPTY) {
                        return move.location + 7;
                    }
                    else if (checkBottomLeftDiag(move) == GameConstants.EMPTY) {
                        return move.location + 5;
                    }
                    //checks top cell and if open chooses it
                    else if (checkTop(move) == GameConstants.EMPTY) {
                        return move.location - 6;
                    }
                    //if move has no open cells, mark it as stale
                    move.isStale = true;
                }
            }
            //returns if all cells  are stale, almost impossible to encounter this error
            return -1;
        }

    //checks right cell and returns status
    private fun checkRightCell(currCell: Move): Int {
        //only checks right cell if the cell is not on the furthest right column
        return if (!isRightColumn(currCell)) {
            board[(currCell.location + 1) / GameConstants.ROWS][(currCell.location + 1) % GameConstants.COLS];
        }
        //no right cell exists
        else {
            -1;
        }
    }

    //checks left cell and returns status
    private fun checkLeftCell(currCell: Move): Int {
        //only checks left cell if the cell is not on the furthest left column
        return if (!isLeftColumn(currCell)) {
            board[(currCell.location - 1) / GameConstants.ROWS][(currCell.location - 1) % GameConstants.COLS];
        }
        //no left cell exists
        else {
            -1;
        }
    }

    //checks the right diagonal cell and returns status
    private fun checkTopRightDiag(currCell: Move): Int {
        //if cell is on top row or is on the furthest right column no right diag cell exists
        return if (currCell.location < 6 || isRightColumn(currCell)) {
            -1;
        }
        //right diag cell exists, returns cell status
        else {
            board[(currCell.location - 5) / GameConstants.ROWS][(currCell.location - 5) % GameConstants.COLS];
        }
    }

    //checks left diag cell and returns status
    private fun checkTopLeftDiag(currCell: Move): Int {
        //if cell is on top row is on the furthest left column no left diag exists
        return if (currCell.location < 6 || isLeftColumn(currCell)) {
            -1;
        }
        //left diag cell exists, returns cell status
        else {
            board[(currCell.location - 7) / GameConstants.ROWS][(currCell.location - 7) % GameConstants.COLS];
        }
    }

    private fun checkBottomRightDiag(currCell: Move): Int {
        return if (currCell.location > 29 || isRightColumn(currCell)) {
            - 1;
        } else {
            board[(currCell.location + 7)/GameConstants.ROWS][(currCell.location + 7) % GameConstants.COLS];
        }
    }

    private fun checkBottomLeftDiag(currCell: Move): Int {
        return if (currCell.location > 29 || isLeftColumn(currCell)){
            -1;
        } else {
            board[(currCell.location + 5)/GameConstants.ROWS][(currCell.location + 5) % GameConstants.COLS];
        }
    }

    //checks top cell and returns status
    private fun checkTop(currCell: Move): Int {
        //if cell is on top row, no top cell exists
        return if (currCell.location < 6) {
            -1;
        }
        //top cell exists, returns top cell status
        else {
            (board[(currCell.location - 6) / GameConstants.ROWS][(currCell.location - 6) % GameConstants.COLS]);
        }
    }

    private fun checkBottom(currCell: Move): Int {
        return if (currCell.location > 29) {
            -1;
        } else {
            board[(currCell.location + 6) / GameConstants.ROWS][(currCell.location + 6) % GameConstants.COLS];
        }
    }

    //returns boolean on if cell is on the furthest left column
    private fun isLeftColumn(currCell: Move): Boolean {
        //if cell is cleanly divisible by 6, then it must be on the furthest left column
        //the furthest left column only contains multiples of 6
        return currCell.location % 6 == 0
    }

    //returns boolean on if cell is furthest right column
    private fun isRightColumn(currCell: Move): Boolean {
        //if cell when subtracted by 5 is cleanly divisible by 6, then it must be on the furthest right column
        //all cells that when subtracted by 5, are a cell in the furthest left column, are located on the furthest right column
        return (currCell.location - 5) % 6 == 0
    }

    override fun checkForWinner(): Int {
        //counts wins
        var blueWins = 0;
        var redWins = 0;

        //iterates through each cell and checks for each type of win pattern
        for (row in 0..<GameConstants.ROWS){
            for (col in 0..<GameConstants.COLS){
                //current cell we are checking
                val currPlayer = board[row][col];

                //will only move forward if current cell has something placed by a player
                if (currPlayer != GameConstants.EMPTY){

                    //checks if viable for horizontal check
                    //the head cell of a horizontal win must at least 4 cells to the left
                    if (col < GameConstants.COLS - 3){
                        //horizontal check
                        //checks the next three cells to the right for current player
                        if (board[row][col + 1] == currPlayer
                            && board[row][col + 2] == currPlayer
                            && board[row][col + 3] == currPlayer
                        ) {
                            //returns value for blue or red won respectively
                            when (currPlayer) {
                                GameConstants.BLUE -> blueWins++
                                GameConstants.RED -> redWins++
                            }
                        }
                    }

                    //checks if viable for vertical check
                    //the head cell of a vertical win must be at least 4 cells up
                    if (row < GameConstants.ROWS - 3){
                        //vertical check
                        //checks if the next three cells below for current player
                        if (board[row + 1][col] == currPlayer
                            && board[row + 2][col] == currPlayer
                            && board[row + 3][col] == currPlayer
                        ) {
                            //returns value for blue or red won respectively
                            when (currPlayer) {
                                GameConstants.BLUE -> blueWins++
                                GameConstants.RED -> redWins++
                            }
                        }
                    }

                    //checks if viable for ascending diagonal check
                    //the head of ascending diagonal win must be at least 4 cells up and 4 cells to the right
                    if (row < GameConstants.ROWS - 3 && col >= 3){
                        //ascending diagonal check
                        //checks if the next three cells each are down and left by 1
                        if (board[row + 1][col - 1] == currPlayer
                            && board[row + 2][col - 2] == currPlayer
                            && board[row + 3][col - 3] == currPlayer
                        ) {
                            //returns value for blue or red won respectively
                            when (currPlayer) {
                                GameConstants.BLUE -> blueWins++
                                GameConstants.RED -> redWins++
                            }
                        }
                    }

                    //checks if viable for descending diagonal check
                    //the head of a descending diagonal must be at least 3 cells up and 3 cells to the left
                    if (row < GameConstants.ROWS - 3 && col < GameConstants.COLS - 3){
                        //descending diagonal check
                        //checks if next three cells are each down and to the right by 1
                        if (board[row + 1][col + 1] == currPlayer
                            && board[row + 2][col + 2] == currPlayer
                            && board[row + 3][col + 3] == currPlayer
                        ) {
                            //returns value for blue or red won respectively
                            when (currPlayer) {
                                GameConstants.BLUE -> blueWins++
                                GameConstants.RED -> redWins++
                            }
                        }
                    }
                }
            }
        }

        //returns game status based off wins by red and blue
        return if (blueWins == 0 && redWins == 0) {
            GameConstants.PLAYING;
        } else if (redWins == blueWins) {
            GameConstants.TIE;
        } else if (redWins > blueWins) {
            GameConstants.RED_WON;
        } else {
            GameConstants.BLUE_WON;
        }
    }

    /**
     * Print the game board
     */
    fun printBoard() {
        for (row in 0..<GameConstants.ROWS) {
            for (col in 0..<GameConstants.COLS) {
                printCell(board[row][col]) // print each of the cells
                if (col != GameConstants.COLS - 1) {
                    print("|") // print vertical partition
                }
            }
            println()
            if (row != GameConstants.ROWS - 1) {
                println("-----------") // print horizontal partition
            }
        }
        println()
    }

    /**
     * Print a cell with the specified "content"
     * @param content either BLUE, RED or EMPTY
     */
    fun printCell(content: Int) {
        when (content) {
            GameConstants.EMPTY -> print("   ")
            GameConstants.BLUE -> print(" B ")
            GameConstants.RED -> print(" R ")
        }
    }
}