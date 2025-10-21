package com.govAtizapan.beneficiojoven.viewmodel.categoriasVM

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.govAtizapan.beneficiojoven.model.categorias.CategoryRepository
import com.govAtizapan.beneficiojoven.model.categorias.CategoryResponseGET
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoriaVM : ViewModel() {

    private val repository = CategoryRepository()

    private val _categorias = MutableStateFlow<List<CategoryResponseGET>>(emptyList())
    val categorias: StateFlow<List<CategoryResponseGET>> = _categorias

    fun cargarCategorias() {
        viewModelScope.launch {
            val data = repository.obtenerCategoria()
            _categorias.value = data
        }
    }
}