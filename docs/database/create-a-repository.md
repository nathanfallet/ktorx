# Create a repository

A repository is a class that allows us to interact with our database. It's the place where we will write our SQL queries
and map the results to our models.

Inside the `repositories` package, create your first repository interface:

```kotlin
interface ITasksRepository : IModelSuspendRepository<Task, Long, CreateTaskPayload, UpdateTaskPayload> {

    // You can declare custom methods here, but we will use the default ones for now

}
```

Before implementing it, we need to create our table in the database. Create a new `Tasks.kt` file in the `database`
package:

```kotlin
object Tasks : Table() {

    val id = long("id").autoIncrement()
    val name = varchar("name", 255)
    val completed = bool("completed")
    val createdAt = varchar("created_at", 255)

    override val primaryKey = PrimaryKey(id)

    fun toTask(
        row: ResultRow
    ) = Task(
        id = row[id],
        name = row[name],
        completed = row[completed],
        createdAt = row[createdAt].toInstant()
    )

}
```

Then, implement it in a new file in the `database` package:

```kotlin
class TasksDatabaseRepository(
    private val database: IDatabase,
) : ITasksRepository {

    init {
        database.transaction {
            SchemaUtils.create(Tasks)
        }
    }

    override suspend fun list(context: IContext?): List<Task> =
        database.suspendedTransaction {
            Tasks.selectAll().map(Tasks::toTask)
        }

    override suspend fun get(id: Long, context: IContext?): Task? =
        database.suspendedTransaction {
            Tasks.selectAll().where { Tasks.id eq id }.map(Tasks::toTask).singleOrNull()
        }

    override suspend fun create(payload: CreateTaskPayload, context: IContext?): Task? =
        database.suspendedTransaction {
            Tasks.insert {
                it[name] = payload.name
                it[completed] = false
                it[createdAt] = Clock.System.now().toString()
            }.resultedValues?.map(Tasks::toTask)?.singleOrNull()
        }

    override suspend fun update(id: Long, payload: UpdateTaskPayload, context: IContext?): Boolean =
        database.suspendedTransaction {
            Tasks.update({ Tasks.id eq id }) {
                it[name] = payload.name
                it[completed] = payload.completed
            }
        } == 1

    override suspend fun delete(id: Long, context: IContext?): Boolean =
        database.suspendedTransaction {
            Tasks.deleteWhere { Tasks.id eq id }
        } == 1

}
```

Finally, to make our repository available in our application, we need to declare it in our `Koin.kt` file:

```kotlin
val repositoryModule = module {
    single<ITasksRepository> { TasksDatabaseRepository(get()) }
}

modules(
    // Existing modules...
    repositoryModule,
)
```

Finally, we can [test our repository](testing-repository.md).
