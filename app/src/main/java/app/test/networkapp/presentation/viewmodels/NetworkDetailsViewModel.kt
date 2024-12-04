package app.test.networkapp.presentation.viewmodels

import app.test.networkapp.data.models.Network
import app.test.networkapp.data.repository.NetworkRepository
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NetworkDetailsState(
    val network: Network? = null,
    val showProgressBar: Boolean = false,
)

class NetworkDetailsViewModel(
    inetnum: String,
    private val networkRepository: NetworkRepository,
) : BaseViewModel<NetworkDetailsState>(NetworkDetailsState()) {

    init {
        loadNetworkDetails(inetnum)
    }

    private fun loadNetworkDetails(inetnum: String) = launch {
        _state.update { it.copy(showProgressBar = true) }
        try {
            val network = networkRepository.getNetworkByInetnum(inetnum)
            _state.update {
                it.copy(
                    network = network
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            _state.update { it.copy(showProgressBar = false) }
        }
    }
}