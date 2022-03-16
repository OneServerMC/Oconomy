package org.oneserver.oconomy.manager

import org.bukkit.Bukkit
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.oneserver.oconomy.Oconomy
import org.oneserver.oconomy.table.Accounts
import java.util.UUID

class AccountManager
{
    companion object
    {
        private var instance: AccountManager? = null

        fun getInstance(): AccountManager
        {
            if (instance == null)
                instance = AccountManager()

            return instance!!
        }
    }

    val accounts: MutableList<UUID> = mutableListOf()

    fun setBalance(uuid: UUID, amount: Double): Double
    {
        if (!accounts.contains(uuid))
        {
            Oconomy.plugin.economy?.createPlayerAccount(Bukkit.getOfflinePlayer(uuid))
        }

        var result: Double = -1.0

        transaction {
            Accounts.update({ Accounts.uniqueId eq uuid }) {
                it[Accounts.balance] = amount
            }

            Accounts.select { Accounts.uniqueId eq uuid }.forEach {
                result = it[Accounts.balance]
            }
        }

        return result
    }

    fun getBalance(uuid: UUID): Double
    {
        var amount: Double = 0.0

        transaction {
            Accounts.select { Accounts.uniqueId eq uuid }.forEach {
                amount = it[Accounts.balance]
            }
        }

        return amount
    }

    fun deposit(uuid: UUID, amount: Double): Double = setBalance(uuid, getBalance(uuid) + amount)

    fun withdraw(uuid: UUID, amount: Double): Double = setBalance(uuid, getBalance(uuid) - amount)
}