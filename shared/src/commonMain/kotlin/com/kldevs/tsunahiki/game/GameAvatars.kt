package com.kldevs.tsunahiki.game

import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.DrawableResource
import tsunahiki.shared.generated.resources.Res
import tsunahiki.shared.generated.resources.avatar_bear
import tsunahiki.shared.generated.resources.avatar_bunny
import tsunahiki.shared.generated.resources.avatar_cat
import tsunahiki.shared.generated.resources.avatar_dog
import tsunahiki.shared.generated.resources.avatar_fox
import tsunahiki.shared.generated.resources.avatar_owl
import tsunahiki.shared.generated.resources.avatar_panda
import tsunahiki.shared.generated.resources.avatar_penguin
import tsunahiki.shared.generated.resources.avatar_shiba
import tsunahiki.shared.generated.resources.avatar_tanuki

typealias GameAvatarID = Int

data class GameAvatar(
    val name: String,
    val drawable: DrawableResource,
    val price: Int,
)

object GameAvatars {
    val entries = listOf(
        GameAvatar("Bear", Res.drawable.avatar_bear, 0),
        GameAvatar("Bunny", Res.drawable.avatar_bunny, 120),
        GameAvatar("Cat", Res.drawable.avatar_cat, 250),
        GameAvatar("Dog", Res.drawable.avatar_dog, 250),
        GameAvatar("Fox", Res.drawable.avatar_fox, 320),
        GameAvatar("Owl", Res.drawable.avatar_owl, 325),
        GameAvatar("Panda", Res.drawable.avatar_panda, 330),
        GameAvatar("Penguin", Res.drawable.avatar_penguin, 500),
        GameAvatar("Shiba", Res.drawable.avatar_shiba, 512),
        GameAvatar("Tanuki", Res.drawable.avatar_tanuki, 800),
    )

    fun getDrawable(id: GameAvatarID) = entries[id].drawable
}