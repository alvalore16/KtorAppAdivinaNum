package com.example.ktorclientapp.ui.screens.viewUser.ranking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ktorclientapp.data.model.Ranking
import com.example.ktorclientapp.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RankingViewModel(
    private val repositorio: GameRepository = GameRepository()
) : ViewModel() {

    // Jugadores ya ordenados, el servidor nos los manda en orden
    private val _ranking = MutableStateFlow<List<Ranking>>(emptyList())
    val ranking: StateFlow<List<Ranking>> = _ranking.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun cargarRanking() {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null

            try {
                _ranking.value = repositorio.getRanking()
            } catch (e: Exception) {
                _error.value = "No se pudo cargar el ranking"
            } finally {
                _cargando.value = false
            }
        }
    }
}
