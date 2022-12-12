# nab-weather  

## Overall app's architecture  

This application uses a simplified version of Uncle Bob's Clean Architecture:  

<img src="/assets/app_arch_clean.svg"/>  

1. Entity: the inner most (or the most abstract layer) of the application. For this application, this layer contains only 1 model class `WeatherInfo`.  
2. Domain: this layer contains application specific business logic such as determining whether retrieving data from remote or local datasource. This layer depends on **Entity** layer and contains the `WeatherInfoRepository`.  
3. Data & Presentation: these 2 layers are the most detail layers and depend on **Domain**.  
    - Data: providing data from either local or remote datasource (`WeatherInfoLocalDatasource` or `WeatherInfoRemoteDatasource`).  
    - Presentation: contains user interface such as weather info list screen (i.e. `MainActivity`).  

The above architecture is just an logical architecture. Its goal is enforcing the dependency rules described in Clean Architecture, which is the inner layer is the more abstract layer, and the outer layer only knows about and depends on inner layer.  

## Code structure  

This project is modular. Each module contains public classes placed under root package. Any other private classes of that module is placed in `/internal` folder.  

Modules dependency graph:  
<img src="/assets/module_dependency_graph.svg"/>

  - `depsconstraints`: providing build information such as `targetSdk`, `minSdk`, `applicationId`, etc. and project 3rd libraries.  
  - `common`: providing common classes for other modules such as Android `Context`.  
  - `entity`: physical representation of **Entity** layer.  
  - `domain`: physical representation of **Domain** layer.  
  - `data`: physical representation of **Data** layer.  
  - `app`: Android app entrypoint and physical representation of **Presentation** layer.  
  
The reason why module `app` has dependency on `domain`, `data` and `common` is module `app` need to construct dependency graph using [Dagger2](https://github.com/google/dagger).  

## Principles, best practices applied in this app

Single source of truth.

  - Inspired by [Single source of truth](https://developer.android.com/topic/architecture#single-source-of-truth) principle, the data model is designed as follow:  

    <img src="/assets/ssot.svg"/>  

Dependency Injection (using **Dagger2**).  

SOLID (Single responsibility, Open-closed, Liskov substitution, Interface segregation and Dependency Inversion).  

DRY (Don't Repeat Yourself).  

Design patterns: Singleton, Factory, Builder, etc.  

## Requirement checklist  

### Functionalities  

:white_check_mark: The application is a simple Android application which is written by Java/Kotlin.  
:white_check_mark: The application is able to retrieve the weather information from OpenWeatherMaps API.  
:white_check_mark: The application is able to allow user to input the searching term.  
:white_check_mark: The application is able to proceed searching with a condition of the search term length must be from 3 characters or above.  
:white_check_mark: The application is able to render the searched results as a list of weather items.  
:white_check_mark: The application is able to support caching mechanism so as to prevent the app from generating a bunch of API requests.  
:white_check_mark: The application is able to manage caching mechanism & lifecycle.  
:white_check_mark: The application is able to handle failures.  
:white_check_mark: The application is able to support the disability to scale large text for who can't see the text clearly.  
:white_check_mark: The application is able to support the disability to read out the text using VoiceOver controls.  

### Expected outputs

1. :white_check_mark: Programming language: Kotlin.  
2. :white_check_mark: Design app's architecture (Clean & MVVM).  
3. :x: Apply LiveData mechanism (used `StateFlow` instead).  
4. :white_check_mark: UI should be looks like in attachment.  
5. :white_check_mark: Write UnitTests.  
6. :x: Acceptance Tests.  
7. :white_check_mark: Exception handling.  
8. :white_check_mark: Caching handling.  
  - Cached data longer than 10 minutes will be delete by a cron job run by `WorkManager` (i.e. `WeatherInfoCleanupWorker`).  
9. Secure Android app from:  
  - :white_check_mark: Decompile APK (Obfuscation).  
  - :x: Rooted device.  
  - :white_check_mark: Data transmission via network.  
    - Use platform default for network security config that trust only a predefined CAs list specified by Android.  
    - In case backend server has specific CAs, app will switch to pinned CA/SSL config to ensure secured data transmission.  
  - :x: Encryption for sensitive information.  
    - Currently, app has only one sensitive information which is OpenWeatherAPI `appId`. This `appId` is currently not being protected, but there is one solution that I didn't implement yet: Using an native library to inject `appId` to a `AppIdContainer` Kotlin class at runtime. In that solution, `appId` will be compiled into C/C++ bytecode, so it will be hard to decompile. To make this solution further effective, app need to apply anti-debug solution, because attacker can attach a debugger into app's process then read the memory region of the `AppIdContainer` class and figure out the `appId`.  
    - For other sensitive information, for example weather data, if these data need to encryption, we can use [SQLCipher](https://github.com/sqlcipher/android-database-sqlcipher#using-sqlcipher-for-android-with-room) to encrypt data store in Room.  
10. Accessibility for Disability Supports:  
  - :white_check_mark: Talkback: Use a screen reader.  
  - :white_check_mark: Scaling Text: Display size and font size: To change the size of items on your screen, adjust the display size or font size.  
