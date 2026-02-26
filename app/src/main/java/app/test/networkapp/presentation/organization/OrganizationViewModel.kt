package app.test.networkapp.presentation.organization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.test.networkapp.data.repository.OrganizationRepository
import app.test.networkapp.data.models.Organization
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

sealed interface OrganizationUiState {
    data object Loading : OrganizationUiState
    data class Success(val organizations: List<Organization>) : OrganizationUiState
    data class Error(val message: String) : OrganizationUiState
}

class OrganizationViewModel(private val repository: OrganizationRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<OrganizationUiState>(OrganizationUiState.Loading)
    val uiState: StateFlow<OrganizationUiState> = _uiState.asStateFlow()

    init {
        loadOrganizations()
    }

    private fun loadOrganizations() {
        repository.getOrganizations()
            .onEach { organizations ->
                _uiState.value = OrganizationUiState.Success(organizations)
            }
            .catch { e ->
                _uiState.value = OrganizationUiState.Error(e.localizedMessage ?: "An unknown error occurred")
            }
            .launchIn(viewModelScope) // Запускаем и автоматически отменяем при уничтожении ViewModel
    }
}
