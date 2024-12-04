package app.test.networkapp.utils

import app.test.networkapp.data.models.Network
import app.test.networkapp.data.models.Organization
import app.test.networkapp.data.responses.RipeSearchResponse

private const val IP_REGEX =
    "^((25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})\\.){3}(25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})$"

fun extractNetworks(response: RipeSearchResponse): List<Network> {
    return response.objects?.networks?.mapNotNull { networkObject ->
        val attributes = networkObject.attributes?.attribute ?: return@mapNotNull null

        val inetnum = attributes.find { it.name == "inetnum" }?.value ?: return@mapNotNull null
        val netname = attributes.find { it.name == "netname" }?.value ?: return@mapNotNull null
        val country = attributes.find { it.name == "country" }?.value
        val organizationId = attributes.find { it.name == "org" }?.value ?: return@mapNotNull null

        Network(
            inetnum = inetnum,
            netname = netname,
            country = country,
            organizationId = organizationId
        )
    } ?: emptyList()
}

fun extracOrganizations(response: RipeSearchResponse): List<Organization> {
    return response.objects?.networks?.mapNotNull { networkObject ->
        val attributes = networkObject.attributes?.attribute ?: return@mapNotNull null

        val name = attributes.find { it.name == "org-name" }?.value ?: return@mapNotNull null
        val country = attributes.find { it.name == "country" }?.value
        val created = attributes.find { it.name == "created" }?.value ?: return@mapNotNull null
        val organizationId =
            attributes.find { it.name == "organisation" }?.value ?: return@mapNotNull null

        Organization(
            id = organizationId,
            name = name,
            country = country,
            created = created.toDate()
        )
    } ?: emptyList()
}

fun String.isValidIp(): Boolean {
    return Regex(IP_REGEX).matches(this)
}