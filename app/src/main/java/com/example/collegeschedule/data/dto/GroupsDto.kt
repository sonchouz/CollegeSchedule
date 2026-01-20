package com.example.collegeschedule.data.dto

data class GroupsDto(
    val id: Int,
    val groupName: String,
    val course: Int,
    val specialty: String,
    val isFavorite: Boolean = false
)
