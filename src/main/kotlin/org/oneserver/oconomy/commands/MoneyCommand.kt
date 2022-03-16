package org.oneserver.oconomy.commands

import hazae41.minecraft.kutils.bukkit.msg
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.oneserver.oconomy.Oconomy
import org.oneserver.oconomy.manager.AccountManager
import java.util.UUID

class MoneyCommand : CommandExecutor
{
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean
    {
        val player: Player = sender as Player

        if (args.isEmpty())
        {
            sender.msg("&6あなたの所持金: &e${Oconomy.plugin.economy?.getBalance(player)}${Oconomy.MainConfig.currencyUnit}")
            return true
        }

        when (args[0])
        {
            "help" -> {
            }

            "show" -> {

                if (args.size > 1)
                {
                    sender.msg("&6${ if (player.name == args[1]) "あなた" else args[1]}の所持金: &e${Oconomy.plugin.economy?.getBalance(args[1])}${Oconomy.MainConfig.currencyUnit}")
                    return true
                }

                sender.msg("&6あなたの所持金: &e${Oconomy.plugin.economy?.getBalance(player)}${Oconomy.MainConfig.currencyUnit}")
            }

            "set" -> {

                if (args.size == 1)
                {
                    sender.msg("&cプレイヤーを指定してください。")
                    return true
                }

                if (args.size == 2)
                {
                    sender.msg("&c金額を指定してください。")
                    return true
                }

                AccountManager.getInstance().setBalance(Oconomy.plugin.uuidFetcher?.fetchUUID(args[1]) ?: return true, args[2].toDouble())
                sender.msg("&a${args[1]}の所持金を${args[2]}${Oconomy.MainConfig.currencyUnit}に設定しました。")
            }

            "give" -> {

                if (args.size == 1)
                {
                    sender.msg("&cプレイヤーを指定してください。")
                    return true
                }

                if (args.size == 2)
                {
                    sender.msg("&c金額を指定してください。")
                    return true
                }

                Oconomy.plugin.economy?.depositPlayer(args[1], args[2].toDouble())
                sender.msg("&a${args[1]}に${args[2]}${Oconomy.MainConfig.currencyUnit}を付与しました。")
            }

            "take" -> {

                if (args.size == 1)
                {
                    sender.msg("&cプレイヤーを指定してください。")
                    return true
                }

                if (args.size == 2)
                {
                    sender.msg("&c金額を指定してください。")
                    return true
                }

                if (!Oconomy.plugin.economy?.has(args[1], args[2].toDouble())!!)
                {
                    sender.msg("&cそのプレイヤーは徴収しようとした金額を所持していません。")
                    return true
                }

                Oconomy.plugin.economy?.withdrawPlayer(args[1], args[2].toDouble())
                sender.msg("&a${args[1]}から${args[2]}${Oconomy.MainConfig.currencyUnit}を徴収しました。")
            }

            "pay" -> {
            }
        }

        return true
    }
}