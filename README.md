# Лабораторна робота №2 — Gradle та Java Collections Framework

**Студент:** Бабело Андрій
**Тема:** Збірка проєкту за допомогою Gradle та робота з Java Collections Framework

## Опис проєкту

Проєкт реалізує **менеджер історії дій з обмеженою ємністю** (`ActionHistoryManager`)
у пакеті `ua.kpi.comsys.collections`.

- Історія зберігається у двонаправленій черзі `java.util.Deque` (реалізація `ArrayDeque`).
  Найновіша дія знаходиться на початку черги, найстаріша в кінці.
- Нова дія додається на початок черги (`addFirst`). Якщо після цього розмір перевищує
  `maxCapacity`, найстаріша дія видаляється з кінця черги (`removeLast`).
- `undo()` працює за принципом LIFO: видаляє і повертає найновішу дію (`pollFirst`).
- `getHistory()` повертає **копію** у вигляді `ArrayList`, тому зміни повернутого списку
  не впливають на внутрішній стан.
- Використано лише стандартні колекції `java.util.*`.

### API

| Метод | Поведінка |
|---|---|
| `ActionHistoryManager(int maxCapacity)` | `maxCapacity <= 0` → `IllegalArgumentException("maxCapacity must be greater than 0")` |
| `void performAction(String action)` | додає дію на вершину; `null`/blank → `IllegalArgumentException("Action cannot be null or blank")` |
| `String undo()` | видаляє й повертає найновішу дію; порожня історія → `null` |
| `String peekRecentAction()` | повертає найновішу дію без видалення; порожня історія → `null` |
| `List<String> getHistory()` | копія історії від найновішої до найстарішої |
| `int getHistorySize()` | поточна кількість дій |
| `void clear()` | повністю очищає історію |

### Структура

```
├── build.gradle.kts
├── settings.gradle.kts
├── gradle/
│   ├── libs.versions.toml        # Version Catalog (JUnit 5)
│   └── wrapper/                  # Gradle Wrapper
├── gradlew, gradlew.bat
└── src/
    ├── main/java/ua/kpi/comsys/collections/ActionHistoryManager.java
    └── test/java/ua/kpi/comsys/collections/ActionHistoryManagerTest.java
```

Версії залежностей задані у `gradle/libs.versions.toml`, а `build.gradle.kts` посилається
на них через `libs.junit.jupiter` і подібні псевдоніми.

## Розрахунок варіанта

Залікової книжки немає, тому використано порядковий номер у списку групи: **N = 1**.

```
V = N mod 3 = 1 mod 3 = 1
```

**Варіант 1 — Менеджер історії дій з обмеженням ємності.**

## Запуск тестів

Потрібна лише **JDK 21+**. Gradle встановлювати не треба, його завантажить Gradle Wrapper.

Linux / macOS:
```bash
./gradlew build
./gradlew test
```

Windows (PowerShell):
```powershell
.\gradlew.bat build
.\gradlew.bat test
```

Звіт про тести: `build/reports/tests/test/index.html`
Звіт про покриття (JaCoCo): `build/reports/jacoco/test/html/index.html`
