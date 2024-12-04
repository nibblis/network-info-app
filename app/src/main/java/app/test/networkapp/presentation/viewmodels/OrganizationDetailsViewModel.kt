package app.test.networkapp.presentation.viewmodels

import app.test.networkapp.data.models.Network
import app.test.networkapp.data.models.Organization
import app.test.networkapp.data.repository.OrganizationRepository
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OrganizationDetailsState(
    val organization: Organization? = null,
    val networks: List<Network> = emptyList(),
    val showProgressBar: Boolean = false
)

class OrganizationDetailsViewModel(
    organizationId: String,
    private val orgRepository: OrganizationRepository,
) : BaseViewModel<OrganizationDetailsState>(OrganizationDetailsState()) {

    init {
        loadOrganizationDetails(organizationId)
    }

    private fun loadOrganizationDetails(orgId: String) = launch {
        _state.update { it.copy(showProgressBar = true) }
        try {
            val org = orgRepository.getOrganizationById(orgId)
            val networksList = orgRepository.getOrganizationNetworks(orgId)
            _state.update {
                it.copy(
                    organization = org,
                    networks = networksList
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            _state.update { it.copy(showProgressBar = false) }
        }
    }
}
