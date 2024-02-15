# Testing repository

To test our repository, we will use [h2](https://h2database.com/html/main.html), an in-memory database. This allows us
to test our repository without having to set up a real database, and to have a clean state for each test.

To use h2, add the following dependency to your `build.gradle.kts` file:

```kotlin
testImplementation("com.h2database:h2:2.2.224")
```

For each test, we create an in-memory database, and instantiate our repository with it. Then, we call our repository
and check that the result is the expected one.

Here is an example for the `create` method:

```kotlin
@Test
fun createTask() = runBlocking {
        val database = Database(protocol = "h2", name = "createTask")
        val repository = TasksDatabaseRepository(database)
        val task = repository.create(CreateTaskPayload("Test task"))
        val taskFromDatabase = database.suspendedTransaction {
            Tasks.selectAll().map(Tasks::toTask).singleOrNull()
        }
        assertEquals(taskFromDatabase?.id, task?.id)
        assertEquals(taskFromDatabase?.name, task?.name)
        assertEquals(taskFromDatabase?.completed, task?.completed)
        assertEquals(taskFromDatabase?.createdAt, task?.createdAt)
        assertEquals("Test task", task?.name)
        assertEquals(false, task?.completed)
    }
```

This test creates a new task in the database, and checks that the result is the expected one.

Here are examples for other methods:

```kotlin
@Test
fun getTask() = runBlocking {
        val database = Database(protocol = "h2", name = "getTask")
        val repository = TasksDatabaseRepository(database)
        val task = repository.create(CreateTaskPayload("Test task"))
            ?: fail("Failed to create task")
        val taskFromDatabase = repository.get(task.id)
        assertEquals(task.id, taskFromDatabase?.id)
        assertEquals(task.name, taskFromDatabase?.name)
        assertEquals(task.completed, taskFromDatabase?.completed)
        assertEquals(task.createdAt, taskFromDatabase?.createdAt)
    }
```

```kotlin
@Test
fun listTasks() = runBlocking {
        val database = Database(protocol = "h2", name = "listTasks")
        val repository = TasksDatabaseRepository(database)
        repository.create(CreateTaskPayload("Test task 1"))
        repository.create(CreateTaskPayload("Test task 2"))
        val tasks = repository.list()
        assertEquals(2, tasks.size)
        assertEquals("Test task 1", tasks[0].name)
        assertEquals("Test task 2", tasks[1].name)
    }
```

```kotlin
@Test
fun updateTask() = runBlocking {
        val database = Database(protocol = "h2", name = "updateTask")
        val repository = TasksDatabaseRepository(database)
        val task = repository.create(CreateTaskPayload("Test task"))
            ?: fail("Failed to create task")
        assertEquals(true, repository.update(task.id, UpdateTaskPayload("Updated task", true)))
        val taskFromDatabase = repository.get(task.id)
        assertEquals("Updated task", taskFromDatabase?.name)
        assertEquals(true, taskFromDatabase?.completed)
    }
```

```kotlin
@Test
fun deleteTask() = runBlocking {
        val database = Database(protocol = "h2", name = "deleteTask")
        val repository = TasksDatabaseRepository(database)
        val task = repository.create(CreateTaskPayload("Test task"))
            ?: fail("Failed to create task")
        assertEquals(true, repository.delete(task.id))
        val taskFromDatabase = repository.get(task.id)
        assertEquals(null, taskFromDatabase)
    }
```

Now, let's [add some logic](../usecases/add-logic.md) between our repository and our controllers.
