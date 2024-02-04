# Create a database

Start by adding the dependency to your `build.gradle.kts` file.

```kotlin
implementation("me.nathanfallet.surexposed:surexposed:1.0.0")
```

Now, in the `database` package, create a new file called `Database.kt` and add the following code: (this is the sample
code from [surexposed](https://github.com/nathanfallet/surexposed))

```kotlin
class Database(
    protocol: String,
    host: String = "",
    name: String = "",
    user: String = "",
    password: String = "",
) : IDatabase {

    private val database: org.jetbrains.exposed.sql.Database = when (protocol) {
        "mysql" -> org.jetbrains.exposed.sql.Database.connect(
            "jdbc:mysql://$host:3306/$name", "com.mysql.cj.jdbc.Driver",
            user, password
        )

        "h2" -> org.jetbrains.exposed.sql.Database.connect(
            "jdbc:h2:mem:$name;DB_CLOSE_DELAY=-1;", "org.h2.Driver"
        )

        else -> throw Exception("Unsupported database protocol: $protocol")
    }

    override fun <T> transaction(statement: Transaction.() -> T): T = transaction(database, statement)

    override suspend fun <T> suspendedTransaction(statement: suspend Transaction.() -> T): T =
        newSuspendedTransaction(Dispatchers.IO, database) { statement() }

}
```

If not declared yet, add the database configuration to your `application.conf` file:

```hocon
database {
    protocol = "mysql"
    host = "localhost"
    name = "tasks"
    user = "root"
    password = ""
    host = ${?DB_HOST}
    name = ${?DB_NAME}
    user = ${?DB_USER}
    password = ${?DB_PASSWORD}
}
```

Then, create our database in our `Koin.kt` file:

```kotlin
val databaseModule = module {
    single<IDatabase> {
        Database(
            environment.config.property("database.protocol").getString(),
            environment.config.property("database.host").getString(),
            environment.config.property("database.name").getString(),
            environment.config.property("database.user").getString(),
            environment.config.property("database.password").getString()
        )
    }
}

modules(
    // Existing modules...
    databaseModule,
)
```

Now, it's time to [create our first repository](create-a-repository.md) to interact with our database.
