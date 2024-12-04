package app.test.networkapp.data.responses

import com.google.gson.annotations.SerializedName

data class Attribute(
    val name: String? = null,
    val value: String? = null,
    val link: Link? = null,
    @SerializedName("referenced-type")
    val referencedType: String? = null,
    val comment: String? = null
)