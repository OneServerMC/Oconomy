package org.oneserver.oconomy.table

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import java.math.BigInteger
import java.util.UUID

object Account : Table()
{
    val id: Column<Int> = integer("id").autoIncrement()
    override val primaryKey = PrimaryKey(id)

    val uniqueId: Column<UUID> = uuid("uniqueId")

    val balance: Column<Long> = long("balance")
}