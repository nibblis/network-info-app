package app.test.networkapp.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screens {
    @Serializable
    data object Search : Screens()

    @Serializable
    data class Organization(val orgId: String) : Screens()

    @Serializable
    data class Network(val inetnum: String) : Screens()
}