# Демонстрация навыков кэширования данных

Этот документ описывает подходы к кэшированию в Android-приложении: в памяти, на диске и на уровне HTTP-запросов.

### 1. In-memory кэш (Кэш в оперативной памяти)

In-memory кэш — самый быстрый, так как данные хранятся прямо в ОЗУ. Идеально подходит для часто используемых, но не критически важных данных, которые не жалко потерять при перезапуске приложения. Стандартная реализация — `LruCache`.

**Пример:** Кэш для объектов `Organization`.

```kotlin
import androidx.collection.LruCache
import app.test.networkapp.data.models.Organization

// Определяем максимальный размер кэша (например, 50 объектов)
val memoryCache = LruCache<String, Organization>(50)

fun getOrganizationFromMemory(id: String): Organization? {
    return memoryCache.get(id) // Быстрое получение из ОЗУ
}

fun cacheOrganizationInMemory(organization: Organization) {
    memoryCache.put(organization.id, organization)
}
```

### 2. Disk кэш (Кэш на диске с помощью Room)

Проект уже использует Room, который отлично подходит на роль дискового кэша. Это основа шаблона **"Single Source of Truth"** (Единый источник правды), где UI всегда читает данные из базы, а репозиторий отвечает за их обновление из сети.

**Принцип работы репозитория:**
1.  UI запрашивает данные.
2.  Репозиторий сначала возвращает данные из Room (это быстро).
3.  Затем репозиторий делает фоновый запрос в сеть.
4.  Если из сети приходят свежие данные, репозиторий сохраняет их в Room.
5.  UI, подписанный на `Flow` из Room, автоматически обновляется.

```kotlin
class OrganizationRepository(private val api: ApiService, private val dao: OrganizationDao) {
    
    fun getOrganizations(): Flow<List<Organization>> {
        // 1. UI всегда получает актуальные данные из базы
        return dao.getAllOrganizations()
    }

    suspend fun refreshOrganizations() {
        try {
            // 2. Запрашиваем свежие данные из сети
            val freshData = api.getOrganizations()
            // 3. Сохраняем их в Room, затирая старые
            dao.insertAll(freshData)
        } catch (e: Exception) {
            // Обработка ошибки сети
        }
    }
}
```

### 3. HTTP-кэш (ETag/Cache-Control)

Этот вид кэширования настраивается в OkHttp (сетевой клиент, который использует Retrofit). Он позволяет избежать повторной загрузки данных, если они не изменились на сервере.

**Как работает:**
-   **`Cache-Control`**: Заголовок ответа сервера, который говорит клиенту, как долго (в секундах) можно использовать кэшированный ответ. `Cache-Control: max-age=3600` означает, что ответ можно использовать в течение часа.
-   **`ETag`**: Уникальный идентификатор версии ресурса. При повторном запросе клиент отправляет `If-None-Match: <ETag>`. Если данные не изменились, сервер отвечает `304 Not Modified` с пустым телом, экономя трафик.

**Настройка в Koin/DI модуле:**

```kotlin
// 1. Определяем размер и путь для кэша
val cacheSize = (10 * 1024 * 1024).toLong() // 10 MB
val cache = Cache(context.cacheDir, cacheSize)

// 2. Создаем OkHttpClient с этим кэшем
val okHttpClient = OkHttpClient.Builder()
    .cache(cache) // <--- Подключаем HTTP-кэш
    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
    .build()

// 3. Используем этот клиент в Retrofit
Retrofit.Builder()
    .baseUrl("...")
    .client(okHttpClient) // <--- Передаем наш клиент
    .addConverterFactory(GsonConverterFactory.create())
    .build()
```
Теперь OkHttp будет автоматически кэшировать ответы, если сервер отправляет правильные заголовки.

### 4. Инвалидация кэша и Метрики

**Инвалидация (как понять, что кэш устарел):**
-   **TTL (Time-To-Live)**: Самый простой способ. При сохранении данных в Room записываем текущее время. Перед тем как отдать данные, проверяем, не прошло ли слишком много времени (например, 15 минут).

    *Добавляем поле в Entity:*
    `@ColumnInfo(name = "last_updated") val lastUpdated: Long`

    *Проверка в репозитории:*
    `val isCacheStale = (System.currentTimeMillis() - lastUpdated) > 15 * 60 * 1000`

-   **Версионирование ключей**: Если структура данных меняется, можно изменить ключ, по которому они хранятся (например `user_v1_id` -> `user_v2_id`), чтобы старый кэш не использовался. В Room это решается миграциями.

**Метрики (как измерить эффективность):**
-   **HTTP-кэш**: OkHttp предоставляет встроенные метрики.

    ```kotlin
    val hitRate = okHttpClient.cache?.hitCount() ?: 0
    val networkRate = okHttpClient.cache?.networkCount() ?: 0
    val requestCount = okHttpClient.cache?.requestCount() ?: 0
    println("Cache hits: $hitRate, Network requests: $networkRate")
    ```

-   **Disk-кэш**: Можно реализовать вручную в репозитории, добавив счетчики.

    ```kotlin
    class CacheMetrics {
        var diskHits = 0
        var networkHits = 0
    }
    // В репозитории увеличивать нужный счетчик
    ```
