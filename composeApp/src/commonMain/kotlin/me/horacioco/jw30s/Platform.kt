package me.horacioco.jw30s

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform