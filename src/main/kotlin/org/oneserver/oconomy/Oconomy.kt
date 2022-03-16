package org.oneserver.oconomy

import hazae41.minecraft.kutils.bukkit.PluginConfigFile
import hazae41.minecraft.kutils.bukkit.init
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.oneserver.oconomy.table.Account
import java.io.File
import java.sql.Connection

class Oconomy : AbstractOconomy()
{
    companion object
    {
        lateinit var plugin: Oconomy
    }

    override fun onEnable()
    {
        plugin = this

        init(MainConfig)
        MainConfig.autoSave = true

        val dbFolder: File = File(dataFolder, "/database")
        if (!dbFolder.exists()) dbFolder.mkdirs()

        val dbFile: File = File(dataFolder, "/database/oconomy.db")
        if (!dbFile.exists()) dbFile.createNewFile()

        Database.connect("jdbc:sqlite:${dbFile.path}", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

        transaction {
            addLogger(StdOutSqlLogger)

            SchemaUtils.create(
                Account
            )
        }
    }

    object MainConfig: PluginConfigFile("config")
    {
        var prefix by string("prefix")
        var currencyUnit by string("currencyUnit")
    }
}
