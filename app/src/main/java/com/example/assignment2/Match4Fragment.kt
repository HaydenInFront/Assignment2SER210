/**
 * @author Hayden Lacy
 * 2/25/24
 * Assignment 2
 */

package com.example.assignment2

import android.annotation.SuppressLint
import android.media.Image
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.get
import androidx.core.view.isVisible
import org.w3c.dom.Text

class Match4Fragment : Fragment() {

    //match four game object
    private lateinit var m4board: Match4

    //grid
    private lateinit var match4Grid: GridLayout

    //view
    private lateinit var view: View

    //current game turn
    private var currentTurn: Int = GameConstants.RED

    //holds the value of the hashcode for grey drawable
    private var emptyCellResource: Int = 0

    //holds current game state
    private var gameState: Int = GameConstants.PLAYING

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_match4, container, false)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //instantiates view for the entire class, and not just the function
        this.view = view

        //passes username from previous fragment and puts it in username text view
        val username = Match4FragmentArgs.fromBundle(requireArguments()).username
        view.findViewById<TextView>(R.id.username).text = username

        //assigns grid layout to var
        match4Grid = view.findViewById(R.id.match4Grid)

        //constructs the match 4 object
        m4board = Match4(view)

        //holds the hash code for the empty cell drawable
        emptyCellResource = this.resources.getDrawable(R.drawable.empty_cell_color, context?.theme).constantState.hashCode()

        //creates value for the restart button and sets on click listener to restart game
        val restartButton = view.findViewById<Button>(R.id.restartButton)
        restartButton.setOnClickListener {
            restartGame();
        }

        //fills grid with image buttons
        for (rowIndex in 0 until 6){
            for (colIndex in 0 until 6){
                //creates image button
                val imageButton = ImageButton(requireContext())
                imageButton.layoutParams = GridLayout.LayoutParams().apply {
                    //applies which column and row it'll be for the grid
                    columnSpec = GridLayout.spec(colIndex, GridLayout.FILL, 1f)
                    rowSpec = GridLayout.spec(rowIndex, GridLayout.FILL, 1f)
                }

                //sets image button to empty cell color background
                imageButton.setBackgroundResource(R.drawable.empty_cell_color)

                //sets on click listener for each image button
                imageButton.setOnClickListener {
                    //retrives hashcode of current imagebutton background drawable
                    val currentBackgroundResource = imageButton.background.constantState.hashCode()

                    //if the current turn is the player's,
                    // the background hashcode for the image button is the same as an empty cell,
                    //and the game is currently playing, then run the game cycle once
                    if (currentTurn == GameConstants.RED && currentBackgroundResource == emptyCellResource && gameState == GameConstants.PLAYING){
                        runGameCycle(imageButton)
                    }
                }

                //adds the button to the grid
                match4Grid.addView(imageButton)
            }
        }
    }

    //runs once cycle of the game
    private fun runGameCycle(cell: ImageButton){
        //simulates player's move
        runPlayerMove(cell)
        //simulates computer's move
        runComputerMove()
        //handles game state changing to win
        runCheckWinner()
    }

    //player move
    private fun runPlayerMove(cell: ImageButton){
        //sets whichever button clicked to the player drawable
        cell.setBackgroundResource(R.drawable.red_cell_color)
        //sets move in the backend on the m4board
        m4board.setMove(GameConstants.RED, match4Grid.indexOfChild(cell))
        //makes the current turn the computer
        currentTurn = GameConstants.BLUE
    }

    private fun runComputerMove(){
        //retrieves value of computer ai move
        val computerMove = m4board.computerMove
        //sets the corresponding cell on the front end to the computer drawable
        match4Grid.getChildAt(computerMove).setBackgroundResource(R.drawable.blue_cell_color)
        //sets move in the backend on the m4board
        m4board.setMove(GameConstants.BLUE, computerMove)
        //makes the current turn the player
        currentTurn = GameConstants.RED
    }

    private fun runCheckWinner(){
        //sets game state to whatever the checkForWinner method determines it is
        gameState = m4board.checkForWinner()
        //handles possible game states that aren't just continue playing
        when (gameState) {
            GameConstants.TIE -> {
                //displays tie
                view.findViewById<TextView>(R.id.winMessage).text = getString(R.string.tie_message)
                //displays restart button
                view.findViewById<Button>(R.id.restartButton).isVisible = true;
            }

            GameConstants.RED_WON -> {
                //displays red won
                view.findViewById<TextView>(R.id.winMessage).text = getString(R.string.red_won_message)
                //displays restart button
                view.findViewById<Button>(R.id.restartButton).isVisible = true;
            }

            GameConstants.BLUE_WON -> {
                //displays blue won
                view.findViewById<TextView>(R.id.winMessage).text = getString(R.string.blue_won_message)
                //displays restart button
                view.findViewById<Button>(R.id.restartButton).isVisible = true;
            }
        }
    }

    private fun restartGame() {
        //clears the board on the backend
        m4board.clearBoard()
        //removes text from message displaying who won
        view.findViewById<TextView>(R.id.winMessage).text = ""
        //sets restart button to invisible
        view.findViewById<Button>(R.id.restartButton).isVisible = false;
        //game state is set back to playing
        gameState = GameConstants.PLAYING
        //iterates through every cell on front end and sets them to empty drawable
        for (index in 0 until match4Grid.childCount) {
            match4Grid.getChildAt(index).setBackgroundResource(R.drawable.empty_cell_color)
        }
    }
}

