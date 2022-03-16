package org.oneserver.oconomy

import org.bukkit.plugin.java.JavaPlugin

class Oconomy : JavaPlugin()
{
    companion object
    {
        lateinit var plugin: Oconomy
    }

    override fun onEnable()
    {
        plugin = this
    }
}
