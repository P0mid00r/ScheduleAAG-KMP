package com.pomidorka.scheduleaag.updater

data class Version(
    val major: Int,
    val minor: Int,
    val patch: Int,
    val isBeta: Boolean,
) {
    override fun toString() = "$major.$minor.$patch"

    operator fun compareTo(v2: Version): Int {
        return compareValuesBy(this, v2,
            { it.major },
            { it.minor },
            { it.patch },
        )
    }
}

fun String.parseVersion(): Version {
    val line = this.removePrefix("v").trim()
    val nums = mutableListOf<Int>()
    val args = line.split("-")
    args.first()
        .split(".")
        .forEach {
        nums.add(it.toInt())
    }
    val isBeta = if (args.count() > 1)
        args.last().lowercase().contains("beta")
    else false

    return when(nums.count()) {
        3 -> Version(
            major = nums[0],
            minor = nums[1],
            patch = nums[2],
            isBeta = isBeta,
        )
        2 -> Version(
            major = nums[0],
            minor = nums[1],
            patch = 0,
            isBeta = isBeta,
        )
        1 -> Version(
            major = nums[0],
            minor = 0,
            patch = 0,
            isBeta = isBeta,
        )
        else -> throw IllegalArgumentException()
    }
}
