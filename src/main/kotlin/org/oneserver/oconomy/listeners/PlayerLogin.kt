package org.oneserver.oconomy.listeners

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import org.oneserver.oconomy.Oconomy

class PlayerLogin : Listener
{
    @EventHandler
    fun onPlayerLogin(e: PlayerLoginEvent)
    {
        val player: Player = e.player

        if (Oconomy.plugin.economy?.hasAccount(player.uniqueId)!!) return

        Oconomy.plugin.economy?.createPlayerAccount(player)
        Oconomy.plugin.logger.info("Created an account for ${player.name}.")
    }
}