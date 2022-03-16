package org.oneserver.oconomy

import hazae41.minecraft.kutils.bukkit.PluginConfigFile
import hazae41.minecraft.kutils.bukkit.init

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
    }

    object MainConfig: PluginConfigFile("config")
    {
        var prefix by string("prefix")
        var currencyUnit by string("currencyUnit")
    }
}
