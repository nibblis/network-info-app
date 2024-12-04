package app.test.networkapp.data.remote

import app.test.networkapp.data.responses.RipeSearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RipeApiService {
    @GET("/search.json")
    suspend fun searchOrganizations(
        @Query("query-string") query: String,
        @Query("source") source: String = "ripe",
        @Query("type-filter") typeFilter: String = "organisation",
        @Query("flags") flags: String = "no-referenced",
    ): RipeSearchResponse

    @GET("/ripe/organisation/{orgId}.json")
    suspend fun getOrganizationById(
        @Path("orgId") orgId: String
    ): RipeSearchResponse

    @GET("/search.json")
    suspend fun searchNetworksByOrganization(
        @Query("inverse-attribute") inverseAttribute: String = "org",
        @Query("type-filter") typeFilter: String = "inetnum",
        @Query("source") source: String = "ripe",
        @Query("query-string") orgId: String,
        @Query("flags") flags: String = "no-referenced",
    ): RipeSearchResponse

    @GET("/search.json")
    suspend fun searchNetworkByIp(
        @Query("query-string") ipAddress: String,
        @Query("source") source: String = "ripe",
        @Query("type-filter") typeFilter: String = "inetnum",
        @Query("flags") flags: String = "no-referenced",
    ): RipeSearchResponse
}