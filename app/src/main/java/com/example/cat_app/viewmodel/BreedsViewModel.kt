package com.example.cat_app.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cat_app.data.api.IBreedsService
import com.example.cat_app.data.models.BreedsModel
import com.example.cat_app.network.networkConection
import com.example.cat_app.ui_ux.components.toats.toastSnackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.URLEncoder


class BreedsViewModel(private val breedsService: IBreedsService) : ViewModel() {

    private val _breedItems = mutableStateListOf<BreedsModel>()
    val breedItems: List<BreedsModel> get() = _breedItems

    val isLoading = mutableStateOf(false)
    private var reconnectJob: Job? = null
    private var isTryingToReconnect = false

    // Busca as raças da API
    fun fetchBreeds(context: Context) {
        viewModelScope.launch {
            try {
                isLoading.value = true

                val response = breedsService.getBreedsList()

                if (response.isSuccessful) {
                    val breeds = response.body() ?: emptyList()
                    _breedItems.clear()
                    _breedItems.addAll(breeds)
                    reconnectJob?.cancel()
                    isTryingToReconnect = false
                    Log.d("VIEWMODEL", "🐱 Raças carregadas com sucesso: ${breeds.size}")
                } else {
                    toastSnackbar(context, "Erro HTTP: ${response.code()} ${response.message()}")
                }

            } catch (e: HttpException) {
                when (e.code()) {
                    400 -> toastSnackbar(context, "Requisição inválida! Verifique os dados.")
                    else -> toastSnackbar(context, "Erro HTTP: ${e.code()} ${e.message()}")
                }
            } catch (e: IOException) {
                toastSnackbar(context, "Sem conexão. Tentando reconectar...")
                tentarRecarregarPeriodicamente(context)
            } catch (e: Exception) {
                toastSnackbar(context, "Erro inesperado: ${e.message}")
                Log.e("VIEWMODEL", "Erro inesperado: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    // Repetir requisição se conexão voltar
    fun tentarRecarregarPeriodicamente(context: Context) {
        if (isTryingToReconnect) return

        isTryingToReconnect = true

        reconnectJob = viewModelScope.launch {
            while (isActive) {
                delay(5000)

                if (networkConection(context)) {
                    toastSnackbar(context, "Conexão restabelecida! Recarregando raças...")
                    fetchBreeds(context)
                    isTryingToReconnect = false
                    break
                }
            }
        }
    }

    // Se precisar de função para limpar lista
    fun clearBreeds() {
        _breedItems.clear()
    }
}
