/**
 * @author Hayden Lacy
 * 2/25/24
 * Assignment 2
 */

package com.example.assignment2

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController


class MainFragment : Fragment() {



    lateinit var navController: NavController;

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //nav controller
        navController = Navigation.findNavController(view);

        //holds username entered
        val usernameField = view.findViewById<EditText>(R.id.usernameField)

        //when click start
        view.findViewById<Button>(R.id.startButton).setOnClickListener {
            //check is user put in username, if not then don't let user move on
            if (usernameField.text.toString() != ""){
                //sets username to text entered
                val username = usernameField.text.toString()
                //passes on username to main game
                val action = MainFragmentDirections.actionMainFragmentToMatch4Fragment(username)
                view.findNavController().navigate(action)

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }
}