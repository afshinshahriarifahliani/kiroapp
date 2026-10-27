package com.emberfocus.core.model

/** Ambient background sounds. Some are gated behind Pro. */
enum class Soundscape(
    val displayName: String,
    val assetFileName: String?,
    val isPro: Boolean,
) {
    NONE("None", null, false),
    RAIN("Rain", "rain.ogg", false),
    FOREST("Forest", "forest.ogg", false),
    CAFE("Cafe", "cafe.ogg", false),
    FIREPLACE("Fireplace", "fireplace.ogg", true),
    OCEAN("Ocean", "ocean.ogg", true),
    NIGHT("Night Crickets", "night.ogg", true),
    WHITE_NOISE("White Noise", "white_noise.ogg", true);

    companion object {
        val free get() = entries.filter { !it.isPro }
    }
}
