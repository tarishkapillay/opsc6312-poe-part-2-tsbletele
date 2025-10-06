package com.example.cineflix.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cineflix.R
import com.example.cineflix.viewmodel.MovieDetailViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class MovieDetailActivity : AppCompatActivity() {

    private val viewModel: MovieDetailViewModel by viewModels()
    private lateinit var adapter: MovieDetailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        // Views
        val movieTitle = findViewById<TextView>(R.id.movieTitle)
        val movieOverview = findViewById<TextView>(R.id.movieOverview)
        val genreChips = findViewById<ChipGroup>(R.id.genreChips)
        val recyclerMore = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerMoreLikeThis)
        val backButton = findViewById<ImageButton>(R.id.backButton)
        val shareButton = findViewById<ImageButton>(R.id.shareButton)

        // Recycler setup
        adapter = MovieDetailAdapter()
        recyclerMore.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerMore.adapter = adapter

        // Observe LiveData
        viewModel.movie.observe(this) { movie ->
            movieTitle.text = movie.title
            movieOverview.text = movie.overview

            // Genre chips
            genreChips.removeAllViews()
            movie.genres.forEach { genre ->
                val chip = Chip(this)
                chip.text = genre
                chip.isClickable = false
                chip.isCheckable = false
                genreChips.addView(chip)
            }

            // Update "More like this"
            adapter.updateList(movie.similarTitles)
        }

        // Simulate data load (you can replace this with real movie ID)
        viewModel.loadMovieDetails(1)

        // Back button
        backButton.setOnClickListener { finish() }

        // Share button
        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out ${movieTitle.text} on CineFlix!")
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }

        // Example of rating click handling
        setupUserRating()
    }

    /** Handles interactive star rating by the user */
    private fun setupUserRating() {
        val starIds = listOf(R.id.star1, R.id.star2, R.id.star3, R.id.star4, R.id.star5)
        val stars = starIds.map { findViewById<ImageButton>(it) }

        for (i in stars.indices) {
            stars[i].setOnClickListener {
                for (j in stars.indices) {
                    val drawable =
                        if (j <= i) R.drawable.star_filled else R.drawable.star_outline
                    stars[j].setImageResource(drawable)
                }
                Toast.makeText(this, "You rated ${i + 1} stars!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
