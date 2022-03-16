package org.oneserver.oconomy.table

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import java.util.UUID

object Accounts : Table()
{
    val id: Column<Int> = integer("id").autoIncrement()
    override val primaryKey = PrimaryKey(id)

    val uniqueId: Column<UUID> = uuid("uniqueId")

    val balance: Column<Double> = double("balance")
}