package com.moo.beans.data

enum class DarkModePreference {
    System,
    Light,
    Dark;

    companion object {
        val DEFAULT: DarkModePreference = System

        fun fromStorage(value: String?): DarkModePreference =
            entries.firstOrNull { it.name == value } ?: DEFAULT
    }
}
