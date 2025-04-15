package com.example.eventcountdown.api

class HolidayRepository(private val apiService: NagerApiService = RetrofitClient.api) {
    suspend fun getHolidays(year: Int, countryCode: String): List<Holiday> {
        return apiService.getPublicHolidays(year, countryCode)
    }
}