package com.example.galleryapp.ui.moments

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MomentStory(
    val title: String,
    val photoCount: Int,
    val colorHex: Long
)

data class MomentPlace(
    val name: String,
    val count: Int,
    val colorHex: Long
)

data class MomentPerson(val name: String, val colorHex: Long)

data class MomentsUiState(
    val stories: List<MomentStory>,
    val people: List<MomentPerson>,
    val places: List<MomentPlace>
)

class MomentsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(
        MomentsUiState(
            stories = listOf(
                MomentStory("Mumbai nights", 24, 0xFF1E3A5FL),
                MomentStory("Coffee runs", 12, 0xFF5C3D1FL),
                MomentStory("Sunsets", 18, 0xFF5C1F1FL),
            ),
            people = listOf(
                MomentPerson("Mum", 0xFF5C1F4DL),
                MomentPerson("Dad", 0xFF1F4D5CL),
                MomentPerson("Sara", 0xFF2D5A27L),
                MomentPerson("Arjun", 0xFF3D1F5CL),
                MomentPerson("Self", 0xFF5C2A1FL),
            ),
            places = listOf(
                MomentPlace("Mumbai", 247, 0xFF1E3A5FL),
                MomentPlace("Goa", 64, 0xFF1F4D5CL),
            )
        )
    )
    val uiState: StateFlow<MomentsUiState> = _uiState.asStateFlow()
}
