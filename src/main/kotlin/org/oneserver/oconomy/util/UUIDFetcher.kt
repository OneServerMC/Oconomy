package org.oneserver.oconomy.util

import com.google.gson.JsonParser
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.function.Consumer


class UUIDFetcher {
    private val executor: ExecutorService

    constructor(threads: Int) {
        executor = Executors.newFixedThreadPool(threads)
    }

    constructor(executor: ExecutorService) {
        this.executor = executor
    }

    @Throws(Exception::class)
    fun fetchUUID(playerName: String): UUID {
        // Get response from Mojang API
        val url = URL("https://api.mojang.com/users/profiles/minecraft/$playerName")
        val connection = url.openConnection() as HttpURLConnection
        connection.connect()
        if (connection.responseCode == 400) {
            System.err.println("There is no player with the name \"$playerName\"!")
            return UUID.randomUUID()
        }
        val inputStream = connection.inputStream
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))

        // Parse JSON response and get UUID
        val element = JsonParser().parse(bufferedReader)
        val `object` = element.asJsonObject
        val uuidAsString = `object`["id"].asString

        // Return UUID
        return parseUUIDFromString(uuidAsString)
    }

    @Throws(Exception::class)
    fun fetchName(uuid: UUID): String? {
        // Get response from Mojang API
        val url = URL("https://api.mojang.com/user/profiles/" + uuid.toString().replace("-", "") + "/names")
        val connection = url.openConnection() as HttpURLConnection
        connection.connect()
        if (connection.responseCode == 400) {
            System.err.println("There is no player with the UUID \"$uuid\"!")
            return null
        }
        val inputStream = connection.inputStream
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))

        // Parse JSON response and return name
        val element = JsonParser().parse(bufferedReader)
        val array = element.asJsonArray
        val `object` = array[0].asJsonObject
        return `object`["name"].asString
    }

    fun fetchUUIDAsync(playerName: String, consumer: Consumer<UUID?>) {
        executor.execute {
            try {
                // Get response from Mojang API
                val url = URL("https://api.mojang.com/users/profiles/minecraft/$playerName")
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()
                if (connection.responseCode == 400) {
                    System.err.println("There is no player with the name \"$playerName\"!")
                    return@execute
                }
                val inputStream = connection.inputStream
                val bufferedReader = BufferedReader(InputStreamReader(inputStream))

                // Parse JSON response and get UUID
                val element = JsonParser().parse(bufferedReader)
                val `object` = element.asJsonObject
                val uuidAsString = `object`["id"].asString
                inputStream.close()
                bufferedReader.close()

                // Return UUID
                consumer.accept(parseUUIDFromString(uuidAsString))
            } catch (e: IOException) {
                System.err.println("Couldn't connect to URL.")
                e.printStackTrace()
            }
        }
    }

    fun fetchNameAsync(uuid: UUID, consumer: Consumer<String?>) {
        executor.execute {
            try {
                // Get response from Mojang API
                val url = URL(
                    String.format(
                        "https://api.mojang.com/user/profiles/%s/names",
                        uuid.toString().replace("-", "")
                    )
                )
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()
                if (connection.responseCode == 400) {
                    System.err.println("There is no player with the UUID \"$uuid\"!")
                    return@execute
                }
                val inputStream = connection.inputStream
                val bufferedReader = BufferedReader(InputStreamReader(inputStream))

                // Parse JSON response and return name
                val element = JsonParser().parse(bufferedReader)
                val array = element.asJsonArray
                val `object` = array[0].asJsonObject
                bufferedReader.close()
                inputStream.close()
                consumer.accept(`object`["name"].asString)
            } catch (e: IOException) {
                System.err.println("Couldn't connect to URL.")
                e.printStackTrace()
            }
        }
    }

    fun shutdown() {
        executor.shutdown()
    }

    private fun parseUUIDFromString(uuidAsString: String): UUID {
        val parts = arrayOf(
            "0x" + uuidAsString.substring(0, 8),
            "0x" + uuidAsString.substring(8, 12),
            "0x" + uuidAsString.substring(12, 16),
            "0x" + uuidAsString.substring(16, 20),
            "0x" + uuidAsString.substring(20, 32)
        )
        var mostSigBits = java.lang.Long.decode(parts[0])
        mostSigBits = mostSigBits shl 16
        mostSigBits = mostSigBits or java.lang.Long.decode(parts[1])
        mostSigBits = mostSigBits shl 16
        mostSigBits = mostSigBits or java.lang.Long.decode(parts[2])
        var leastSigBits = java.lang.Long.decode(parts[3])
        leastSigBits = leastSigBits shl 48
        leastSigBits = leastSigBits or java.lang.Long.decode(parts[4])
        return UUID(mostSigBits, leastSigBits)
    }
}