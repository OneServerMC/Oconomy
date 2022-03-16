package org.oneserver.oconomy

import hazae41.minecraft.kutils.bukkit.PluginConfigFile
import hazae41.minecraft.kutils.bukkit.init
import hazae41.minecraft.kutils.bukkit.listen
import net.milkbowl.vault.economy.Economy
import org.bukkit.event.server.PluginEnableEvent
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.ServicePriority
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.oneserver.oconomy.commands.MoneyCommand
import org.oneserver.oconomy.economy.VaultEconomy
import org.oneserver.oconomy.listeners.PlayerLogin
import org.oneserver.oconomy.table.Accounts
import org.oneserver.oconomy.util.UUIDFetcher
import java.io.File
import java.sql.Connection

class Oconomy : AbstractOconomy()
{
    companion object
    {
        lateinit var plugin: Oconomy
    }

    var uuidFetcher: UUIDFetcher? = null
    var economy: VaultEconomy? = null

    override fun onEnable()
    {
        plugin = this

        init(MainConfig)
        MainConfig.autoSave = true

        uuidFetcher = UUIDFetcher(1)

        val dbFolder: File = File(dataFolder, "/database")
        if (!dbFolder.exists()) dbFolder.mkdirs()

        val dbFile: File = File(dataFolder, "/database/oconomy.db")
        if (!dbFile.exists()) dbFile.createNewFile()

        Database.connect("jdbc:sqlite:${dbFile.path}", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

        transaction {
            addLogger(StdOutSqlLogger)

            SchemaUtils.create(
                Accounts
            )
        }

        registerCommands(
            "money" to MoneyCommand()
        )

        registerListeners(
            PlayerLogin()
        )

        if (economy == null)
        {
            val vault: Plugin = server.pluginManager.getPlugin("Vault") ?: return

            if (!vault.isEnabled)
            {
                listen<PluginEnableEvent> {
                    if (it.plugin.name != "Vault") return@listen

                    hookVault()
                    PluginEnableEvent.getHandlerList().unregister(this)
                }
            }
            else hookVault()
        }
    }

    private fun hookVault()
    {
        if (economy != null) return

        economy = VaultEconomy(uuidFetcher!!)
        server.servicesManager.register(Economy::class.java, economy!!, this, ServicePriority.Normal)
        logger.info("Hooked Vault!")
    }

    object MainConfig: PluginConfigFile("config")
    {
        var prefix by string("prefix")
        var currencyUnit by string("currencyUnit")
    }
}
