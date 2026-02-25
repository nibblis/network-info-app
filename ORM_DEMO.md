# Демонстрация работы с ORM (Room)

Этот документ показывает, как реализовать ключевые функции при работе с локальной базой данных на примере Room.

### 1. CRUD (Create, Read, Update, Delete)

CRUD-операции реализуются в `DAO` (Data Access Object). Для вашей сущности `Organization` это может выглядеть так:

```kotlin
@Dao
interface OrganizationDao {

    // CREATE: Вставка одной или нескольких организаций
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(organization: Organization)

    // READ: Получение всех организаций в виде потока данных
    @Query("SELECT * FROM organizations ORDER BY name ASC")
    fun getAllOrganizations(): Flow<List<Organization>>

    // READ: Получение одной организации по ID
    @Query("SELECT * FROM organizations WHERE id = :id")
    suspend fun getOrganizationById(id: String): Organization?

    // UPDATE: Обновление существующей организации
    @Update
    suspend fun update(organization: Organization)

    // DELETE: Удаление организации
    @Delete
    suspend fun delete(organization: Organization)
}
```

- `suspend` функции выполняются в фоновом потоке (благодаря корутинам).
- `Flow<...>` позволяет UI автоматически обновляться при изменении данных в таблице.

### 2. Миграция базы данных

Когда вы меняете схему (например, добавляете столбец), нужно создать миграцию, чтобы у пользователей не удалились их данные.

**Шаг 1:** Измените Entity. Добавим поле `isFavorite`.

```kotlin
@Entity(tableName = "organizations")
data class Organization(
    // ... старые поля
    @ColumnInfo(name = "is_favorite", defaultValue = "0") // defaultValue важен для миграции
    val isFavorite: Boolean = false
)
```

**Шаг 2:** Увеличьте версию БД и опишите миграцию.

При создании базы данных в DI-модуле (например, Koin):

```kotlin
// 1. Увеличиваем версию с 1 до 2
private const val DATABASE_VERSION = 2

// 2. Описываем, как перейти с версии 1 на 2
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Добавляем новую колонку в таблицу organizations
        db.execSQL("ALTER TABLE organizations ADD COLUMN is_favorite INTEGER NOT NULL DEFAULT 0")
    }
}

// 3. Добавляем миграцию при создании БД
Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
    .fallbackToDestructiveMigration() // Временно, для разработки
    .addMigrations(MIGRATION_1_2) // <---- Вот здесь
    .build()

```

### 3. Тестирование DAO

Тесты для DAO обычно пишут с использованием in-memory базы данных, чтобы не затрагивать реальные данные.

```kotlin
@RunWith(AndroidJUnit4::class)
class OrganizationDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var organizationDao: OrganizationDao

    @Before
    fun setup() {
        // Создаем временную БД в памяти
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build() // allowMainThreadQueries() только для тестов!

        organizationDao = database.organizationDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndReadOrganization() = runBlocking {
        val organization = Organization(id = "1", name = "Test Corp", country = "USA", created = Date())
        
        // 1. Вставляем данные
        organizationDao.insert(organization)
        
        // 2. Читаем данные
        val retrieved = organizationDao.getOrganizationById("1")

        // 3. Проверяем, что данные сохранились корректно
        assertEquals(organization.name, retrieved?.name)
    }
}
```

### 4. Транзакции и Потоки

- **Транзакции**: Если нужно выполнить несколько операций как одну (атомарно), используется аннотация `@Transaction`. Room автоматически обернет вызов в транзакцию.

    ```kotlin
    @Transaction
    suspend fun updateOrganizationAndRelatedData(org: Organization, network: Network) {
        update(org) // Обновляем организацию
        networkDao.update(network) // Обновляем связанную сеть
        // Если здесь произойдет ошибка, оба изменения откатятся.
    }
    ```

- **Потоки**: Room отлично интегрирован с корутинами. `suspend` функции и `Flow` автоматически выполняются на фоновом потоке, защищая UI от зависаний.

### 5. Шифрование (SQLCipher)

Room поддерживает шифрование "из коробки" с помощью библиотеки **SQLCipher**.

**Шаг 1:** Добавьте зависимость в `build.gradle.kts`:

```kotlin
// build.gradle.kts
implementation(libs.androidx.sqlite.cipher)
```

**Шаг 2:** При создании базы данных передайте пароль через `openHelperFactory`.

```kotlin
val passphrase = SQLiteDatabase.getBytes("your-secret-passphrase".toCharArray())
val factory = SupportFactory(passphrase)

Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
    .openHelperFactory(factory) // <--- Вот здесь
    .addMigrations(...)
    .build()
```

Теперь вся ваша база данных будет зашифрована на диске, и без правильной парольной фразы доступ к ней будет невозможен.
