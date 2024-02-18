# ktor-i18n

i18n extension for ktor.

## Installation

Add dependency to your `build.gradle(.kts)` or `pom.xml`:

```kotlin
api("me.nathanfallet.ktorx:ktor-i18n:2.1.0")
```

```xml

<dependency>
    <groupId>me.nathanfallet.ktorx</groupId>
    <artifactId>ktor-i18n-jvm</artifactId>
    <version>2.1.0</version>
</dependency>
```

## Usage

Install the plugin:

```kt
install(I18n) {
    supportedLocales = listOf("en", "fr").map(Locale::forLanguageTag)
}
```

And create your translations files in `resources/i18n`: (files should be named `Messages_xx.properties`)

```properties
key=Value
```

Then, you can use the `TranslateUseCase` to translate your messages:

```kt
val translateUseCase = TranslateUseCase()
val string = translateUseCase(locale, "key")
// Or with parameters
val string = translateUseCase(locale, "key", listOf("param1", "param2"))
```
