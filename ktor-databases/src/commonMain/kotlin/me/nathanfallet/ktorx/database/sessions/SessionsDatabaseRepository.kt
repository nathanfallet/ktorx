package me.nathanfallet.ktorx.database.sessions

import me.nathanfallet.ktorx.models.sessions.Session
import me.nathanfallet.ktorx.repositories.sessions.ISessionsRepository
import me.nathanfallet.surexposed.database.IDatabase
import me.nathanfallet.usecases.context.IContext
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.sql.SQLException

class SessionsDatabaseRepository(
    private val database: IDatabase,
) : ISessionsRepository {

    init {
        database.transaction {
            SchemaUtils.create(Sessions)
        }
    }

    override suspend fun invalidate(id: String) {
        if (!delete(id)) throw NoSuchElementException("Session $id not found")
    }

    override suspend fun read(id: String): String {
        return get(id)?.value ?: throw NoSuchElementException("Session $id not found")
    }

    override suspend fun write(id: String, value: String) {
        try {
            create(Session(id, value))
        } catch (e: SQLException) {
            update(id, value)
        }
    }

    override suspend fun get(id: String, context: IContext?): Session? {
        return database.suspendedTransaction {
            Sessions.selectAll().where { Sessions.id eq id }.map(Sessions::toSession).singleOrNull()
        }
    }

    override suspend fun create(payload: Session, context: IContext?): Session? {
        return database.suspendedTransaction {
            Sessions.insert {
                it[id] = payload.id
                it[value] = payload.value
            }
            payload
        }
    }

    override suspend fun update(id: String, payload: String, context: IContext?): Boolean {
        return database.suspendedTransaction {
            Sessions.update({ Sessions.id eq id }) {
                it[value] = payload
            }
        } == 1
    }

    override suspend fun delete(id: String, context: IContext?): Boolean {
        return database.suspendedTransaction {
            Sessions.deleteWhere { Sessions.id eq id }
        } == 1
    }

}
