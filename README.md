# mWeather - Weather Forecast Android App

A modern Android weather application built with Kotlin, featuring current weather display, forecasts, location services, and offline capabilities.

## Features

- ✅ Current weather display with detailed information
- ✅ 7-day weather forecast
- ✅ Location-based weather detection
- ✅ Search for weather in any city
- ✅ Offline caching for improved performance
- ✅ Material Design UI with smooth animations
- ✅ Swipe-to-refresh functionality
- ✅ MVVM architecture with Clean Architecture principles

## Screenshots

*Coming soon...*

## Tech Stack

- **Language**: Kotlin
- **Architecture**: MVVM + Repository Pattern
- **Dependency Injection**: Hilt
- **Network**: Retrofit + OkHttp
- **Database**: Room
- **Image Loading**: Glide
- **Location Services**: Google Play Services Location
- **Permissions**: Dexter
- **Navigation**: Android Navigation Component

## Setup Instructions

### Prerequisites
- Android Studio (latest version recommended)
- Android SDK 24+ (Android 7.0+)
- WeatherAPI.com API Key (free)

### Getting Started

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/mWeather.git
   cd mWeather
   ```

2. **Get WeatherAPI.com API Key**
   - Visit [WeatherAPI.com](https://www.weatherapi.com/signup.aspx)
   - Create a free account
   - Generate your API key

3. **Configure API Key**
   - Open `local.properties` file in the root directory
   - Replace `your_weatherapi_com_key_here` with your actual API key:
     ```
     API_KEY=your_actual_api_key_here
     ```

4. **Build and Run**
   - Open the project in Android Studio
   - Sync the project with Gradle files
   - Run the app on an emulator or physical device

## Project Structure

```
app/
├── data/
│   ├── api/               # API service interfaces
│   ├── database/          # Room database components
│   ├── repository/        # Repository implementations
│   └── models/           # Data models
├── domain/
│   ├── usecases/         # Business logic use cases
│   └── repository/       # Repository interfaces
├── presentation/
│   ├── ui/               # Activities, Fragments, ViewModels
│   ├── utils/            # Utility classes
│   └── di/               # Dependency injection modules
└── resources/
    ├── layout/           # XML layouts
    ├── values/           # Colors, strings, themes
    └── navigation/       # Navigation graph
```

## API Usage

This app uses the [WeatherAPI.com](https://www.weatherapi.com/) to fetch weather data:

- **Current Weather API**: `/v1/current.json`
- **Forecast API**: `/v1/forecast.json` (up to 7 days)
- **Built-in Location Search**: Search by city name, coordinates, or IP address

## Permissions

The app requires the following permissions:

- `ACCESS_FINE_LOCATION`: For precise location detection
- `ACCESS_COARSE_LOCATION`: For approximate location detection
- `INTERNET`: For API calls
- `ACCESS_NETWORK_STATE`: For network status checking

## Architecture

The app follows **Clean Architecture** principles with **MVVM** pattern:

- **Data Layer**: Handles data from API and local database
- **Domain Layer**: Contains business logic and use cases
- **Presentation Layer**: UI components (Activities, Fragments, ViewModels)

## Offline Support

- Weather data is cached locally using Room database
- Cache expires after 30 minutes
- App shows cached data when offline
- Automatic refresh when network becomes available

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [WeatherAPI.com](https://www.weatherapi.com/) for providing weather data API
- [Material Design](https://material.io/) for UI guidelines
- Android Jetpack libraries for modern Android development

---

**Note**: Remember to add your actual WeatherAPI.com API key to `local.properties` before running the app!