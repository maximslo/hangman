package com.example.hangmanhw3

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var hangmanProgress: ImageView
    private lateinit var guessingWord: TextView
    private lateinit var newGameButton: Button
    private lateinit var currList: List<String>
    private lateinit var hintText: TextView
    private lateinit var hintButton: Button

    private var currPair: String = ""
    private var currWord: String = ""
    private var currHint: String = ""
    private var numGuesses = 0
    private var hintState = 0
    private val guessedLetters = mutableSetOf<Button>()
    private val allLetters = mutableSetOf<Button>()
    companion object {
        const val GUESSING_WORD_KEY = "GUESSING_WORD_KEY"
    }

}
