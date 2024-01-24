package me.nathanfallet.ktorx.repositories.sessions

import io.ktor.server.sessions.*
import me.nathanfallet.ktorx.models.sessions.Session
import me.nathanfallet.usecases.models.repositories.IModelSuspendRepository

interface ISessionsRepository : IModelSuspendRepository<Session, String, Session, String>, SessionStorage
