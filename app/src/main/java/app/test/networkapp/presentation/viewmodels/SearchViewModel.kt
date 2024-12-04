package app.test.networkapp.presentation.viewmodels

import app.test.networkapp.data.models.Network
import app.test.networkapp.data.models.Organization
import app.test.networkapp.data.repository.NetworkRepository
import app.test.networkapp.data.repository.OrganizationRepository
import app.test.networkapp.utils.isValidIp
import kotlinx.coroutines.launch

sealed class SearchState {
    object Initial : SearchState()
    object Loading : SearchState()
    data class Organizations(val data: List<Organization>) : SearchState()
    data class Networks(val data: List<Network>) : SearchState()
    data class Error(val message: String) : SearchState()
}

class SearchViewModel(
    private val orgRepository: OrganizationRepository,
    private val networkRepository: NetworkRepository,
) : BaseViewModel<SearchState>(SearchState.Initial) {

    fun search(query: String) = launch {
        _state.value = SearchState.Loading

        try {
            if (query.isValidIp()) {
                val networkResult = networkRepository.searchByIp(query)
                if (networkResult.isNotEmpty()) {
                    _state.value = SearchState.Networks(networkResult)
                    return@launch
                }
            } else {
                val orgResult = orgRepository.searchOrganizationByName(query)
                if (orgResult.isNotEmpty()) {
                    _state.value = SearchState.Organizations(orgResult)
                    return@launch
                }
            }
            _state.value = SearchState.Error("Ничего не найдено")
        } catch (e: Exception) {
            _state.value = SearchState.Error(e.message ?: "Неизвестная ошибка")
        }
    }
}