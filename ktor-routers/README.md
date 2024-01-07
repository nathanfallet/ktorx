# ktor-routers

Generic routers for Ktor projects.

## Installation

Add dependency to your `build.gradle` or `pom.xml`:

```groovy
compile 'me.nathanfallet.ktorx:ktor-routers:1.9.1'
```

```xml

<dependency>
    <groupId>me.nathanfallet.ktorx</groupId>
    <artifactId>ktor-routers-jvm</artifactId>
    <version>1.9.1</version>
</dependency>
```

## Usage

Declare a model as specified in [usecases](https://github.com/nathanfallet/usecases) to use it with routers:

```kotlin
// MyModel.kt
data class MyModel(
    override val id: Long,
    val property1: String,
    // ...
) : IModel<Long, CreateMyModelPayload, UpdateMyModelPayload>
```

```kotlin
// CreateMyModelPayload.kt
data class CreateMyModelPayload(
    val property1: String,
    // ...
)
```

```kotlin
// UpdateMyModelPayload.kt
data class UpdateMyModelPayload(
    val property1: String?,
    // ...
)
```

Then, create a controller and a router for this model:

```kotlin
class MyController(
    private val dependency1: Dependency1,
    // ...
) : IModelController<MyModel, Long, CreateMyModelPayload, UpdateMyModelPayload>
```

```kotlin
class MyRouter(
    controller: MyController
) : AbstractModelRouter<MyModel, Long, CreateMyModelPayload, UpdateMyModelPayload>(
    typeInfo<MyModel>(),
    typeInfo<CreateMyModelPayload>(),
    typeInfo<UpdateMyModelPayload>(),
    typeInfo<List<MyModel>>(),
    controller
)
```

You can also use some predefined types of routers, like the `APIModelRouter` ready for REST APIs.

There are also child routers, which can be used to create sub-routes.
For example, if you have a `User` model with a `Post` model where posts are created by users,
you can create a `UserRouter`, and a `PostRouter` which will be a child of the user router:

```kotlin
class PostRouter(
    controller: PostController,
    userRouter: UserRouter
) : AbstractChildModelRouter<Post, Long, CreatePostPayload, UpdatePostPayload, User>(
    typeInfo<Post>(),
    typeInfo<CreatePostPayload>(),
    typeInfo<UpdatePostPayload>(),
    typeInfo<List<Post>>(),
    controller,
    userRouter
)
```
