# Демонстрация знаний по Build tools и зависимостям

Этот документ объясняет, как устроен процесс сборки в Android-проекте с использованием Gradle.

### 1. Сборка проекта

Сборка Android-приложения — это процесс, который превращает ваш исходный код и ресурсы в `APK` или `AAB` файл, который можно установить на устройство.

**Что делает Gradle (упрощенно):**
1.  **Компилирует код**: Превращает ваш Kotlin/Java код в байт-код.
2.  **Обрабатывает ресурсы**: Оптимизирует изображения, объединяет манифесты и т.д.
3.  **Управляет зависимостями**: Скачивает все необходимые библиотеки (например, Retrofit, Room).
4.  **Подписывает приложение**: Применяет цифровую подпись к `release` сборке.

Запустить сборку можно из Android Studio (меню **Build -> Make Project**) или из терминала: `./gradlew assembleDebug`.

### 2. Подключение зависимостей

В современных проектах зависимости управляются централизованно через **каталог версий (version catalog)**.

**Как это работает:**
1.  **Файл `gradle/libs.versions.toml`**: Здесь объявляются все зависимости и их версии.

    ```toml
    [versions]
    koin = "3.5.3"
    retrofit = "2.9.0"

    [libraries]
    koin-android = { module = "io.insert-koin:koin-android", version.ref = "koin" }
    retrofit = { module = "com.squareup.retrofit2:retrofit", version.ref = "retrofit" }
    ```

2.  **Файл `app/build.gradle.kts`**: Здесь зависимость подключается по ее псевдониму (алиасу).

    ```kotlin
    dependencies {
        implementation(libs.koin.android)
        implementation(libs.retrofit)
    }
    ```

**Преимущества:** Все версии в одном месте, легко обновлять, нет риска использовать разные версии одной и той же библиотеки в разных модулях.

### 3. Build Types и Product Flavors

#### Build Types (Типы сборки)

Определяют, **как** собирается ваше приложение. По умолчанию их два:
-   `debug`: Для разработки. Включает отладочную информацию, не обфусцируется.
-   `release`: Для публикации. Оптимизируется (R8/ProGuard), подписывается релизным ключом.

**Пример настройки в `build.gradle.kts`:**
```kotlin
android {
    buildTypes {
        getByName("debug") { isMinifyEnabled = false }
        getByName("release") { isMinifyEnabled = true }
    }
}
```

#### Product Flavors (Варианты продукта)

Определяют, **что** собирается. Они позволяют создавать разные версии приложения из одной кодовой базы. Например: `free` и `pro`, или версии для разных магазинов приложений.

**Пример:** `free` и `pro` версии с разными applicationId.
```kotlin
android {
    flavorDimensions += "version"
    productFlavors {
        create("free") {
            dimension = "version"
            applicationIdSuffix = ".free"
        }
        create("pro") {
            dimension = "version"
            applicationIdSuffix = ".pro"
        }
    }
}
```
В итоге вы получите 4 **варианта сборки**: `freeDebug`, `freeRelease`, `proDebug`, `proRelease`.

### 4. Подписание сборок (Signing Configs)

Чтобы установить `release`-версию на устройство или загрузить в Google Play, приложение должно быть подписано цифровым ключом. Это подтверждает, что вы являетесь его автором.

**Настройка в `build.gradle.kts`:**
```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("my-release-key.keystore")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }

    buildTypes {
        getByName("release") {
            // Говорим release-сборке использовать эту подпись
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```
**Важно:** Никогда не храните пароли и ключи в системе контроля версий! Их лучше передавать через переменные окружения.

### 5. Кэширование и воспроизводимость сборки

#### Build Cache (Кэш сборки)

Gradle очень умен. Он кэширует результат каждой задачи (например, компиляции модуля). Если вы не меняли код этого модуля, Gradle возьмет результат из кэша, а не будет выполнять задачу заново. Это **значительно ускоряет** последующие сборки.

Включить кэш можно, добавив в `gradle.properties`: `org.gradle.caching=true`.

#### Воспроизводимость сборки (Reproducible Builds)

Это гарантия того, что сборка одного и того же коммита на разных машинах или в разное время даст **байт-в-байт идентичный `APK/AAB`**. Это важно для безопасности и уверенности в том, что код, который вы тестировали, — это тот же самый код, который попадет к пользователю. Gradle и Android плагин имеют множество встроенных механизмов для достижения этой цели.
