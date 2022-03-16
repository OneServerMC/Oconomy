package org.oneserver.oconomy.economy

import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.oneserver.oconomy.manager.AccountManager
import org.oneserver.oconomy.table.Accounts
import org.oneserver.oconomy.util.UUIDFetcher
import java.util.UUID

class VaultEconomy(val uuidFetcher: UUIDFetcher) : Economy
{
    override fun isEnabled(): Boolean = true

    override fun getName(): String = "Oconomy"

    override fun hasBankSupport(): Boolean = false

    override fun fractionalDigits(): Int = 0

    override fun format(amount: Double): String = amount.toString().replace(".0", "")

    override fun currencyNamePlural(): String? = null

    override fun currencyNameSingular(): String? = null

    fun hasAccount(uuid: UUID): Boolean = AccountManager.getInstance().accounts.contains(uuid)

    override fun hasAccount(playerName: String): Boolean = AccountManager.getInstance().accounts.contains(uuidFetcher.fetchUUID(playerName))

    override fun hasAccount(player: OfflinePlayer): Boolean = AccountManager.getInstance().accounts.contains(player.uniqueId)

    override fun hasAccount(playerName: String, worldName: String): Boolean = false

    override fun hasAccount(player: OfflinePlayer, worldName: String): Boolean = false

    override fun getBalance(playerName: String): Double = AccountManager.getInstance().getBalance(uuidFetcher.fetchUUID(playerName))

    override fun getBalance(player: OfflinePlayer): Double = AccountManager.getInstance().getBalance(player.uniqueId)

    override fun getBalance(playerName: String, world: String): Double = 0.0

    override fun getBalance(player: OfflinePlayer, world: String): Double = 0.0

    override fun has(playerName: String, amount: Double): Boolean = getBalance(playerName) >= amount

    override fun has(player: OfflinePlayer, amount: Double): Boolean = AccountManager.getInstance().getBalance(player.uniqueId) >= amount

    override fun has(playerName: String?, worldName: String?, amount: Double): Boolean = false

    override fun has(player: OfflinePlayer?, worldName: String?, amount: Double): Boolean = false

    override fun withdrawPlayer(playerName: String, amount: Double): EconomyResponse = withdrawPlayer(Bukkit.getOfflinePlayer(uuidFetcher.fetchUUID(playerName)), amount)

    override fun withdrawPlayer(player: OfflinePlayer, amount: Double): EconomyResponse =
        if (AccountManager.getInstance().withdraw(player.uniqueId, amount) == -1.0)
        {
            EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Balance does not exist.")
        }
        else EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.SUCCESS, "OK")

    override fun withdrawPlayer(playerName: String, worldName: String, amount: Double): EconomyResponse? = null

    override fun withdrawPlayer(player: OfflinePlayer, worldName: String, amount: Double): EconomyResponse? = null

    override fun depositPlayer(playerName: String, amount: Double): EconomyResponse = depositPlayer(Bukkit.getOfflinePlayer(uuidFetcher.fetchUUID(playerName)), amount)

    override fun depositPlayer(player: OfflinePlayer, amount: Double): EconomyResponse =
        if (AccountManager.getInstance().deposit(player.uniqueId, amount) == -1.0)
        {
            EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Balance does not exist.")
        }
        else EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.SUCCESS, "OK")

    override fun depositPlayer(playerName: String, worldName: String, amount: Double): EconomyResponse? = null

    override fun depositPlayer(player: OfflinePlayer, worldName: String, amount: Double): EconomyResponse? = null

    override fun createBank(name: String, player: String): EconomyResponse? = null

    override fun createBank(name: String, player: OfflinePlayer): EconomyResponse? = null

    override fun deleteBank(name: String): EconomyResponse? = null

    override fun bankBalance(name: String): EconomyResponse? = null

    override fun bankHas(name: String, amount: Double): EconomyResponse? = null

    override fun bankWithdraw(name: String, amount: Double): EconomyResponse? = null

    override fun bankDeposit(name: String, amount: Double): EconomyResponse? = null

    override fun isBankOwner(name: String, playerName: String): EconomyResponse? = null

    override fun isBankOwner(name: String, player: OfflinePlayer): EconomyResponse? = null

    override fun isBankMember(name: String, playerName: String): EconomyResponse? = null

    override fun isBankMember(name: String, player: OfflinePlayer): EconomyResponse? = null

    override fun getBanks(): MutableList<String>? = null

    override fun createPlayerAccount(playerName: String): Boolean = createPlayerAccount(Bukkit.getOfflinePlayer(uuidFetcher.fetchUUID(playerName)))

    override fun createPlayerAccount(player: OfflinePlayer): Boolean
    {
        var id = -1

        transaction {
            id = Accounts.insert {
                it[Accounts.uniqueId] = player.uniqueId
                it[Accounts.balance] = 0.0
            }[Accounts.id]
        }

        return id != -1
    }

    override fun createPlayerAccount(playerName: String, worldName: String): Boolean = false

    override fun createPlayerAccount(player: OfflinePlayer, worldName: String): Boolean = false
}