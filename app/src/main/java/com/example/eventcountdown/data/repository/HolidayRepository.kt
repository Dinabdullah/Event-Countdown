package com.example.eventcountdown.data.repository

import com.example.eventcountdown.data.remote.model.Holiday
import com.example.eventcountdown.data.remote.api.NagerApiService
import com.example.eventcountdown.data.remote.api.RetrofitClient

class HolidayRepository(private val apiService: NagerApiService = RetrofitClient.api) {
    suspend fun getHolidays(year: Int, countryCode: String): List<Holiday> {
        return apiService.getPublicHolidays(year, countryCode)
    }
}