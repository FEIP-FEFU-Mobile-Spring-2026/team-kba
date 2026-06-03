# team-kba — Mobile Store

## О проекте
Мобильное приложение интернет-магазина одежды на Android. Реализованы каталог, детали товара, корзина с оформлением заказа, оффлайн-режим с кэшированием в Room, а также полноценная настройка качества кода (линтер, unit-тесты).

## Скриншоты

| Каталог | Детали товара | Корзина |
|---------|---------------|---------|
| ![Каталог](screenshots/catalog.png) | ![Детали](screenshots/details.png) | ![Корзина](screenshots/cart.png) |

## Архитектура

Приложение построено на **MVVM (Model-View-ViewModel)** с использованием **Repository** паттерна:

- **Presentation Layer** — Activity и BottomSheet (MainActivity, CartActivity, ProductDetailBottomSheet)
- **ViewModels** — MainViewModel (каталог), CartViewModel (корзина)
- **Repositories** — ProductsRepository (API + Room), CartRepository (корзина)
- **Data Layer** — Room Database (ProductEntity, CartItemEntity) и Retrofit (API)

**Cache-first стратегия:** при открытии каталога сначала показываются данные из кэша (Room), параллельно выполняется запрос к API. При успешном ответе кэш и UI обновляются.

## Стек технологий

| Компонент | Технология |
|-----------|------------|
| Платформа | Android |
| Язык | Kotlin |
| UI | XML layouts, Material Design 3, Bottom Sheet |
| Загрузка изображений | Glide |
| Асинхронность | Coroutines + Flow |
| Архитектура | MVVM + Repository |
| Сеть | Retrofit + OkHttp |
| Кэширование | Room Database |
| Навигация | Custom Bottom Navigation |
| Линтер | ktlint |
| Тесты | JUnit + MockK |

## Состав команды и роли

| Имя | Роль |
|------|------|
| Зайнулин Валентин | Разработчик |
| Козлова Кристина | Разработчик |
| Шехоркин Вадим | Тестировщик |

## Реализованный функционал

- Загрузка товаров из API с кэшированием в Room
- Cache-first стратегия (сначала кэш, потом API)
- Каталог с фильтрацией по категориям
- Карточка товара с изображением, названием, ценой и счётчиком
- Детали товара в BottomSheet (изображение, описание, теги, размеры, характеристики)
- Корзина с добавлением, удалением, изменением количества
- Оформление заказа с валидацией имени и email
- Бейдж на иконке корзины
- Обработка состояний загрузки и ошибок
- Поворот экрана и восстановление состояния
- Настроен ktlint (линтер)
- Unit-тесты (8 тестов: корзина и фильтры)

## Инструкция по сборке

**Требования:**
- Android Studio (последняя стабильная версия)
- JDK 11 или новее
- Android SDK (minSdk = 24, targetSdk = 34)

**Шаги для сборки:**

1. **Клонирование репозитория**
```bash
git clone https://github.com/FEIP-FEFU-Mobile-Spring-2026/team-kba.git
cd team-kba
```

text

2. **Открытие в Android Studio**
- File → Open → выберите папку `team-kba`
- Дождитесь синхронизации Gradle

3. **Сборка проекта**
- Через IDE: Build → Make Project (Ctrl+F9)
- Через терминал: `./gradlew assembleDebug`

4. **Запуск приложения**
- Подключите устройство или запустите эмулятор
- Нажмите Run (зелёная стрелка) или Shift + F10

5. **Запуск линтера и тестов**
```bash
./gradlew ktlintCheck # проверка стиля кода
./gradlew ktlintFormat # автоисправление стиля
./gradlew test # запуск всех тестов
```

## Статус

✅ Проект полностью завершён, все 6 блоков сданы.