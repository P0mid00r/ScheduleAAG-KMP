package com.pomidorka.scheduleaag.updater

import kotlinx.serialization.Serializable

@Serializable
data class ReleasesData(
    val assets: List<Asset>,
    val assets_url: String,
    val author: Author,
    val body: String,
    val created_at: String,
    val draft: Boolean,
    val html_url: String,
    val id: Int,
    val immutable: Boolean,
    val name: String,
    val node_id: String,
    val prerelease: Boolean,
    val published_at: String,
    val tag_name: String,
    val tarball_url: String,
    val target_commitish: String,
    val updated_at: String,
    val upload_url: String,
    val url: String,
    val zipball_url: String
)