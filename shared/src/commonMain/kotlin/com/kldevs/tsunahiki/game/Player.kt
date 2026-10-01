package com.kldevs.tsunahiki.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.setValue
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path

enum class LanguageType(val idx: Int) {
    Kana(0),
}

@Serializable
data class LanguageData(
    var level: Int = 1,
    var xp: Int = 0,
)

@Serializable
class LanguageLevels(var entries: List<LanguageData>) {
    fun getLanguage(language: LanguageType) =
        entries[language.idx]
}

data class PlayerDisplay(
    val name: String,
    val avatar: GameAvatarID = 0,
)

@Serializable
data class PlayerData(
    val name: String = "Player",
    val avatar: GameAvatarID = 0,
    var languages: LanguageLevels = LanguageLevels(List(LanguageType.entries.size) { LanguageData() }),
    val coins: Int = 0,
    val unlockedAvatarIds: HashSet<Int> = hashSetOf(),
)

class PlayerState {
    var name: String by mutableStateOf("Player")
    var languages: LanguageLevels by mutableStateOf(LanguageLevels(List(LanguageType.entries.size) { LanguageData() }))
    var avatar by mutableStateOf(0)
    var coins: Int by mutableStateOf(0)
    var unlockedAvatarIds = mutableStateSetOf<Int>()

    fun toSave(): PlayerData =
        PlayerData(
            name, avatar, languages, coins, unlockedAvatarIds.toHashSet(),
        )

    fun applySave(data: PlayerData) {
        name = data.name
        avatar = data.avatar
        languages = data.languages
        coins = data.coins
        unlockedAvatarIds.clear()
        unlockedAvatarIds.addAll(data.unlockedAvatarIds)
    }

    fun getDisplay(): PlayerDisplay = PlayerDisplay(name, avatar)

    fun isUnlockedAvatar(avatarId: Int) =
        (avatarId in unlockedAvatarIds)

    fun unlockAvatar(avatarId: Int): Boolean {
        val avatar = GameAvatars.entries[avatarId]

        if(coins < avatar.price)
            return false

        coins -= avatar.price
        unlockedAvatarIds.add(avatarId)

        return true
    }
}

class PlayerRepository(
    private val fs: FileSystem,
    private val dir: Path,
) {
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    fun load(): PlayerData {
        val file = dir / "player.json"
        if(!fs.exists(file)) return PlayerData()
        return json.decodeFromString(fs.read(file) { readUtf8() })
    }

    fun save(data: PlayerData) {
        val file = dir / "player.json"
        val tmp = dir / "player.json.tmp"
        fs.write(tmp) { writeUtf8(json.encodeToString(data)) }
        fs.atomicMove(tmp, file)
    }
}