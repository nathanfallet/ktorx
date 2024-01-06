package me.nathanfallet.ktorx.database

interface IDatabase {

    suspend fun <T> dbQuery(block: suspend () -> T): T

}
