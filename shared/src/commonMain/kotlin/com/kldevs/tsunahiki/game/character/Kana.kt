package com.kldevs.tsunahiki.game.character

import com.akuleshov7.ktoml.Toml
import com.kldevs.tsunahiki.game.LanguageType
import com.kldevs.tsunahiki.game.utils.CanvasStroke
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import tsunahiki.shared.generated.resources.Res

enum class KanaScript {
    Hiragana, Katakana,
}

class KanaDescription(val script: KanaScript, val rawChar: String, val romaji: String) : ICharacterDescription {
    private var strokes: List<CanvasStroke>? = null

    override fun getCharacter(): String = rawChar
    override fun getDisplay(): String = romaji
    override suspend fun getStrokes(): List<CanvasStroke> {
        if(strokes != null)
            return strokes!!

        val unicode = rawChar[0].code.toString(16).padStart(5, '0').uppercase()
        val filename = "${unicode}.svg"
        val filepath = "files/svg/kana/${filename}"
        strokes = parseSVG(filepath)
        return strokes!!
    }
}

@Serializable
private data class Progression(
    val id: String,
    val script: String,
    val unlock_value: Int,
    val members: List<String>,
    val romaji: List<String>,
)

@Serializable
private data class KanaToml(
    val sets: List<Progression>,
)

class KanaCatalog private constructor(private val progressions: List<ProgressionLevel>, private val kanaToDescription: Map<String, KanaDescription>) : ICharacterCatalog {

    override fun getLanguage(): LanguageType = LanguageType.Kana

    override fun getAudioPath(filename: String): String = "files/sfx/jp/${filename}.ogg"

    override fun fromText(text: String): KanaDescription {
        return kanaToDescription[text]!!
    }

    override fun getProgressions(): List<ProgressionLevel> {
        return progressions
    }

    companion object {
        private var cache: KanaCatalog? = null

        suspend fun load(): KanaCatalog {
            val bytes = Res.readBytes("files/progressions/kana.toml")
            val decoded = Toml.decodeFromString<KanaToml>(bytes.decodeToString())

            val descriptions = buildMap {
                decoded.sets.forEach { p ->
                    val script = if(p.script == "hiragana") { KanaScript.Hiragana } else { KanaScript.Katakana }

                    p.members.zip(p.romaji).forEach { (ch, romaji) ->
                        this[ch] = KanaDescription(script, ch, romaji);
                    }
                }
            };
            val progs = decoded.sets.map {
                ProgressionLevel(
                    unlockLevel = it.unlock_value,
                    name = it.id,
                    members = it.members,
                    display = it.romaji,
                )
            }

            cache = KanaCatalog(progs, descriptions)

            return cache!!
        }

        fun getOrNull(): KanaCatalog? = cache
    }
}