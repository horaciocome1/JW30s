package me.horaciocome.jw30s

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform