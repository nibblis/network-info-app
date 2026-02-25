# Демонстрация знаний SQL

### Схема данных (упрощенно)

*   `organizations` (`id`, `name`, `country`)
*   `networks` (`id`, `organization_id`, `status`)

### 1. Запросы с JOIN и агрегацией

**Задача:** Посчитать количество активных сетей для каждой организации.

```sql
SELECT
    o.name, -- Название организации
    COUNT(n.id) AS active_networks_count -- Считаем кол-во активных сетей
FROM
    organizations o
JOIN
    networks n ON o.id = n.organization_id -- Соединяем таблицы
WHERE
    n.status = 'active' -- Фильтруем по статусу
GROUP BY
    o.name; -- Группируем для подсчета по каждой организации
```

### 2. Транзакции

**Транзакция** — это группа операций, которая выполняется как единое целое. Либо все операции успешны (`COMMIT`), либо все отменяются (`ROLLBACK`). Это гарантирует целостность данных.

```sql
BEGIN TRANSACTION;
-- Добавляем организацию
INSERT INTO organizations (id, name) VALUES (1, 'Новая Компания');
-- Добавляем ее сеть. Если тут будет ошибка, первая вставка тоже отменится.
INSERT INTO networks (id, organization_id) VALUES (101, 1);
COMMIT;
```

### 3. EXPLAIN

Команда `EXPLAIN` показывает, как база данных будет выполнять запрос. Это помогает найти медленные места и оптимизировать их, например, добавив индексы.

```sql
EXPLAIN QUERY PLAN SELECT * FROM organizations WHERE country = 'USA';
```
**Результат `EXPLAIN`** покажет, используется ли для поиска индекс или происходит полный перебор таблицы (`SCAN TABLE`), что на больших данных очень медленно.
