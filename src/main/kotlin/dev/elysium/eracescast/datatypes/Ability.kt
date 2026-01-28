package dev.elysium.eracescast.datatypes

import kotlinx.serialization.Serializable

@Serializable
data class Ability(
    val id: String,
    val name: String? = null,
    val description: String? = null
)