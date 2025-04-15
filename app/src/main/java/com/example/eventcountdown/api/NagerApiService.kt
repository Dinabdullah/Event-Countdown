package com.example.eventcountdown.api

import retrofit2.http.GET
import retrofit2.http.Path

interface NagerApiService {
    @GET("api/v3/PublicHolidays/{year}/{countryCode}")
    suspend fun getPublicHolidays(
        @Path("year") year: Int,
        @Path("countryCode") countryCode: String
    ): List<Holiday>
}