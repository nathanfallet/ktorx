# ktor-i18n-freemarker

An i18n plugin for Ktor Freemarker

## Installation

Add dependency to your `build.gradle` or `pom.xml`:

```groovy
compile 'me.nathanfallet.ktorx:ktor-i18n-freemarker:1.9.1'
compile 'io.ktor:ktor-server-freemarker:2.3.6'
```

```xml

<dependencies>
    <dependency>
        <groupId>me.nathanfallet.ktorx</groupId>
        <artifactId>ktor-i18n-freemarker-jvm</artifactId>
        <version>1.9.1</version>
    </dependency>
    <dependency>
        <groupId>io.ktor</groupId>
        <artifactId>ktor-server-freemarker-jvm</artifactId>
        <version>2.3.6</version>
    </dependency>
</dependencies>
```

## Usage

### Setup

```kotlin
install(I18n) {
    // ... (see ktor-i18n documentation)
}
install(FreeMarker) {
    // Add our directive
    setSharedVariable("t", TDirective(i18n))

    // ... (see ktor-freemarker documentation)
}
```

### Response

Give the locale to the template, so that it can resolve automatically.

```kotlin
call.respondTemplate(
    "template.ftl",
    mapOf(
        "locale" to call.locale,
        // ...
    )
)
```

### Template

```html
<@t key="greeting" />
```

If you want to supply arguments, you can use the `args` parameter.

```html
<@t key="greeting" args=["Nathan"] />
```

You can also use the `locale` parameter to override the current locale (resolved by the model).

```html
<@t locale="fr" key="greeting" />
```
