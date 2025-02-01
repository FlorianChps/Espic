package com.fchps.data.api

import com.fchps.data.dto.FeedsResponse
import retrofit2.http.GET

interface FakeEsportApi {

    @GET("https://acea289659f04656987ba3ac96351266.api.mockbin.io/")
    suspend fun getFeed(): FeedsResponse
}