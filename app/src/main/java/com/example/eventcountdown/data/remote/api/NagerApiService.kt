package com.example.eventcountdown.data.remote.api

import com.example.eventcountdown.data.remote.model.Holiday
import retrofit2.http.GET
import retrofit2.http.Path

interface NagerApiService {
    @GET("api/v3/PublicHolidays/{year}/{countryCode}")
    suspend fun getPublicHolidays(
        @Path("year") year: Int,
        @Path("countryCode") countryCode: String
    ): List<Holiday>
}