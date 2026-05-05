package com.example.galleryapp.ui.trash

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class TrashItem(
    val id: Long,
    val daysLeft: Int
)

sealed interface TrashUiState {
    data object Loading : TrashUiState
    data class Success(
        val items: List<TrashItem>,
        val selectMode: Boolean = false,
        val selectedIds: Set<Long> = emptySet()
    ) : TrashUiState
}

class TrashViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<TrashUiState>(
        TrashUiState.Success(
            items = (0L until 24L).map { i ->
                TrashItem(id = i, daysLeft = 30 - ((i * 7 + 3) % 28).toInt())
            }
        )
    )
    val uiState: StateFlow<TrashUiState> = _uiState.asStateFlow()

    fun enterSelectMode() = updateSuccess { it.copy(selectMode = true) }

    fun exitSelectMode() = updateSuccess { it.copy(selectMode = false, selectedIds = emptySet()) }

    fun toggleSelection(id: Long) = updateSuccess { state ->
        val updated = if (id in state.selectedIds) state.selectedIds - id else state.selectedIds + id
        state.copy(selectedIds = updated)
    }

    fun selectAll() = updateSuccess { state ->
        state.copy(selectedIds = state.items.map { it.id }.toSet())
    }

    fun restoreSelected() = updateSuccess { state ->
        state.copy(
            items = state.items.filterNot { it.id in state.selectedIds },
            selectMode = false,
            selectedIds = emptySet()
        )
    }

    fun deleteSelected() = updateSuccess { state ->
        state.copy(
            items = state.items.filterNot { it.id in state.selectedIds },
            selectMode = false,
            selectedIds = emptySet()
        )
    }

    private fun updateSuccess(block: (TrashUiState.Success) -> TrashUiState.Success) {
        _uiState.update { current ->
            if (current is TrashUiState.Success) block(current) else current
        }
    }
}
