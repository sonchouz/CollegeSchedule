package com.example.collegeschedule.data.repository

import com.example.collegeschedule.data.api.GroupsApi
import com.example.collegeschedule.data.dto.GroupsDto
import com.example.collegeschedule.data.dto.ScheduleByDateDto

class GroupRepository(private val group: GroupsApi) {
    suspend fun loadGroups(): List<GroupsDto> {
        return group.getGroups (
        )
    }
}