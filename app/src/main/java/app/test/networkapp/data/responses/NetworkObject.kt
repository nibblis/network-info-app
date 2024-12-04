package app.test.networkapp.data.responses

import com.google.gson.annotations.SerializedName

data class NetworkObjects(
    @SerializedName("object")
    val networks: List<NetworkObject>? = null
)

data class NetworkObject(
    val type: String? = null,
    val link: Link? = null,
    @SerializedName("primary-key")
    val primaryKey: PrimaryKey? = null,
    val attributes: NetworkAttributes? = null
)