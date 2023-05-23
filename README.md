# Currency Converter

Simple Android app with Modern Android Development practice with openexchangerates api

## ✨ Demo

Main Screen | AutoCompleteBox | History Screen
--- | --- | ---
<img src="https://github.com/stef-ang/CurrencyConverter/assets/6779288/ff7ff113-f173-47bf-960f-071588623846" width="250" /> | <img src="https://github.com/stef-ang/CurrencyConverter/assets/6779288/28a23f88-7371-470a-8207-4dc98ebb86fe" width="250" /> | <img src="https://github.com/stef-ang/CurrencyConverter/assets/6779288/11b2e1f5-0c41-4ad3-99ab-fcfa7773c857" width="250" />

- It calculates currency conversion from selected currency to all other currencies.
- Every currency conversion calculation will be recorded as history conversion.
  - User can delete the history
- It support offline mode and to limit bandwidth, the app won't refetch remote data within 30 minutes.

## 🦾 How to use

- Generate your openexchangerates App ID [here](https://openexchangerates.org/account/app-ids).
- Open `gradle.properties` file and put your openexchangerates App ID as value of `OpenExchangeApiKey`.
- OR open `local.properties` file and add `OpenExchangeApiKey="[your_app_id]"`

## 📚 Stacks

- [Coroutines](https://developer.android.com/kotlin/coroutines) - Performing asynchronous code with sequential manner.
- [Kotlin Flow](https://developer.android.com/kotlin/flow) - Reactive streams based on coroutines that can emit multiple values sequentially.
- [Dagger Hilt](https://developer.android.com/training/dependency-injection/hilt-android) - The DI framework which reduces the boilerplate.
- [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - Presenter with its semi data persistence behavior.
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Android modern toolkit for building native UI.
- [Compose Material 3](https://developer.android.com/jetpack/compose/designsystems/material3) - Helping me present Material Design.
- [Compose Navigation Component](https://developer.android.com/jetpack/compose/navigation) - For single-activity architecture with Compose.
- [Retrofit](https://square.github.io/retrofit/) - A type-safe HTTP client for Android.
- [Room](https://developer.android.com/training/data-storage/room) - Save data in a local database and support to Kotlin Extensions and Coroutines.
- [LeakCanary](https://square.github.io/leakcanary) (Debug) - Memory leak detector.

## 📐 Architecture and Design Principles

This app adopts [MVVM](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93viewmodel) with [Unidirectional flow (UDF)](https://en.wikipedia.org/wiki/Unidirectional_Data_Flow_(computer_science)) pattern. It follows [S.O.L.I.D](https://en.wikipedia.org/wiki/SOLID) and [The Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) to achieve maintainable, scalable and testable code base. Also the code base has been structured in multi-module project.
Thanks to [this architecture-template repository](https://github.com/android/architecture-templates) because initiation of this project used that template.

## ☑️ TODO

- [ ] Delete history v2
- [ ] Click history to load the conversion
- [ ] Pagination in history screen
- [ ] Integration Test
- [ ] Splash screen

## ✍️ Author

👤 **Stefanus Anggara**

* Twitter: <a href="https://twitter.com/Stef_Anggara" target="_blank"><img alt="Twitter: Stef_Anggara" src="https://img.shields.io/twitter/follow/Stef_Anggara.svg?style=social" /></a>
* Email: anggara.stefanus@gmail.com

Feel free to ping me 😉

## 📝 License

```
Copyright © 2023 - Stefanus Anggara

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
