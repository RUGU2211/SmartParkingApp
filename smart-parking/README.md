# Smart Parking App

A modern Android application for managing parking spaces efficiently.

## Features

- User Authentication with Email Verification (OTP)
- Real-time Parking Slot Availability
- Interactive Map View
- List View of Available Slots
- Booking Management System
- Secure Payment Integration
- Email Notifications

## Technical Stack

- **Architecture:** MVVM (Model-View-ViewModel)
- **Database:** Room Persistence Library
- **UI Components:** Material Design
- **Maps Integration:** Google Maps API
- **Authentication:** Custom Email-based OTP System
- **Testing:** JUnit, Espresso

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/smartparking/
│   │   │   ├── activities/
│   │   │   ├── adapters/
│   │   │   ├── database/
│   │   │   ├── fragments/
│   │   │   ├── models/
│   │   │   └── utils/
│   │   └── res/
│   └── test/
└── build.gradle
```

## Setup

1. Clone the repository
2. Open the project in Android Studio
3. Add your Google Maps API key in `local.properties`:
   ```
   MAPS_API_KEY=your_api_key_here
   ```
4. Build and run the project

## Testing

The project includes both unit tests and instrumentation tests:

- Unit Tests: `./gradlew test`
- Instrumentation Tests: `./gradlew connectedAndroidTest`

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct and the process for submitting pull requests.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
