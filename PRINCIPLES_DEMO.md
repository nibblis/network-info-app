# Демонстрация принципов KISS, DRY, YAGNI

Этот документ в формате код-ревью объясняет, как применять ключевые принципы разработки для улучшения кодовой базы.

---

## Принцип 1: KISS (Keep It Simple, Stupid)

**KISS** — "Делай проще, дурачок". Принцип гласит, что большинство систем работают лучше, если они остаются простыми. Не нужно усложнять без необходимости.

### Код-ревью: Упрощение логики

**Ситуация:** Представим, что в репозитории есть метод для получения названий организаций, отфильтрованных и преобразованных в верхний регистр.

**❌ Плохой пример (излишне сложно):**

```kotlin
// OrganizationRepository.kt
fun getUsaOrganizationNames(): Flow<List<String>> {
    return dao.getAllOrganizations()
        .flatMapConcat { organizations ->
            flow { emit(organizations) } // Ненужное создание нового потока
        }
        .map { orgList ->
            // Фильтруем, потом мапим - можно сделать за один проход
            orgList.filter { it.country == "USA" }
                   .map { it.name.uppercase() }
        }
}
```

**✔️ Хороший пример (просто и понятно):**

```kotlin
// OrganizationRepository.kt
fun getUsaOrganizationNames(): Flow<List<String>> {
    // Используем один оператор `map` для всех преобразований
    return dao.getAllOrganizations().map { organizations ->
        organizations
            .filter { it.country == "USA" }
            .map { it.name.uppercase() }
    }
}
```

---

## Принцип 2: DRY (Don’t Repeat Yourself)

**DRY** — "Не повторяйся". Этот принцип направлен на уменьшение дублирования кода. Вместо копипасты следует выносить повторяющуюся логику в общие функции или классы.

### Код-ревью: Устранение дублирования

**Ситуация:** В разных частях приложения нам нужно форматировать дату создания организации в определенный строковый формат.

**❌ Плохой пример (дублирование):**

```kotlin
// OrganizationsScreen.kt
val date = organization.created
val formattedDate = if (date != null) {
    SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date)
} else {
    "N/A"
}

// OrganizationDetailsScreen.kt
val date2 = organization.created
val formattedDate2 = if (date2 != null) {
    SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date2)
} else {
    "N/A"
}
```

**✔️ Хороший пример (вынос в `DateConverter` или `extension`):**

Создадим общий `TypeConverter` для Room, который также можно использовать в других местах.

```kotlin
// DateConverter.kt (у вас уже есть похожий, его можно расширить)
object DateConverter {
    private val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    fun formatDate(date: Date?): String {
        return date?.let { formatter.format(it) } ?: "N/A"
    }
    
    // ... другие методы для Room
}

// Использование:
val formattedDate = DateConverter.formatDate(organization.created)
```

---

## Принцип 3: YAGNI (You Ain’t Gonna Need It)

**YAGNI** — "Тебе это не понадобится". Не стоит добавлять функциональность "на всякий случай". Это приводит к раздутому, сложному и трудноподдерживаемому коду.

### Код-ревью: Удаление лишних абстракций

**Ситуация:** Разработчик решил, что в будущем у нас будет много разных репозиториев, и создал для них сложный базовый класс.

**❌ Плохой пример (избыточная абстракция):**

```kotlin
// GenericRepository.kt
abstract class GenericRepository<T, ID>(
    private val apiService: Any, // Используем Any, чтобы было универсально
    private val dao: Any
) {
    abstract fun getById(id: ID): Flow<T?>
    abstract fun getAll(): Flow<List<T>>
    abstract suspend fun refresh()
    // ... еще 10 универсальных, но неиспользуемых методов
}

// OrganizationRepository наследуется от GenericRepository, но использует только 2 из 13 методов
```

**✔️ Хороший пример (простота и конкретика):**

Лучше создать простой и конкретный репозиторий. Если в будущем понадобится общая логика, ее можно будет вынести позже (рефакторинг).

```kotlin
// OrganizationRepository.kt
class OrganizationRepository(private val api: ApiService, private val dao: OrganizationDao) {
    
    fun getOrganizations(): Flow<List<Organization>> {
        return dao.getAllOrganizations()
    }

    suspend fun refreshOrganizations() {
        // ... логика обновления
    }
}
```
