package com.kldevs.tsunahiki

interface Platform{
    val name: String
}

expect fun getPlatform(): Platform