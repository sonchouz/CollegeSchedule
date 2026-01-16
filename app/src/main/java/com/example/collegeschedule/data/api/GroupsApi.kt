package com.example.collegeschedule.data.api

import com.example.collegeschedule.data.dto.GroupsDto
import retrofit2.http.GET

interface GroupsApi {
    @GET("api/group")
    suspend fun getGroups(

    ):List<GroupsDto>
}