package me.nathanfallet.ktorx.database

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseTest(
    name: String,
    createTables: Transaction.() -> Unit = {},
) : IDatabase {

    private val database: Database = Database.connect(
        "jdbc:h2:mem:$name;DB_CLOSE_DELAY=-1;", "org.h2.Driver"
    )

    init {
        transaction(database) {
            createTables()
        }
    }

    override suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO, database) { block() }

}
