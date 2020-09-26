package dev.forkhandles.bunting

import java.io.File
import java.util.Properties

interface Config {
    operator fun get(key: String): String?
    operator fun set(key: String, value: String)
}

class InMemoryConfig : Config {
    private val map = mutableMapOf<String, String>()
    override fun get(key: String) = map[key]
    override fun set(key: String, value: String) {
        map[key] = value
    }
}

class PropertiesFileConfig(private val location: File) : Config {
    private val properties: Properties by lazy { Properties().apply { if (location.exists()) location.reader().use(this::load) } }

    override fun get(key: String): String? = properties.getProperty(key)

    override fun set(key: String, value: String) {
        properties.setProperty(key, value).also {
            location.mkdirs()
            location.writer().use { properties.store(it, "") }
        }
    }
}

fun configFile(name: String) = File("${System.getProperty("user.home")}/.$name/config")