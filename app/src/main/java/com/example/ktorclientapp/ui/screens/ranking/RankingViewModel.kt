package com.example.ktorclientapp.ui.screens.ranking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ktorclientapp.data.model.RankingItem
import com.example.ktorclientapp.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RankingViewModel(
    private val repositorio: GameRepository = GameRepository()
) : ViewModel() {

    private val _ranking = MutableStateFlow<List<RankingItem>>(emptyList())
    val ranking: StateFlow<List<RankingItem>> = _ranking.asStateFlow()

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    fun cargarRanking() {
        viewModelScope.launch {
            try {
                _ranking.value = repositorio.getRanking()
                _mensaje.value = ""
            } catch (e: Exception) {
                _mensaje.value = "No se pudo cargar el ranking"
            }
        }
    }
}
