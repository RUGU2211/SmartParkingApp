# Smart Parking App

A modern Android application for managing parking slots and bookings. The app allows users to find, book, and pay for parking slots in real-time.

## Features

- User Authentication
  - Email-based registration with OTP verification
  - Secure login system
  - Password recovery functionality

- Parking Slot Management
  - Real-time availability status
  - List and Map views of parking slots
  - Detailed information about each slot
  - Search and filter functionality

- Booking System
  - Easy slot booking process
  - Duration selection
  - Cost calculation
  - Booking history

- Payment Integration
  - Secure payment processing
  - Multiple payment methods support
  - Transaction history
  - Payment status tracking

- Location Services
  - GPS integration
  - Distance calculation
  - Navigation support

## Technical Stack

- **Language**: Java
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 33 (Android 13)
- **Database**: Room Persistence Library
- **Architecture**: MVVM
- **Maps Integration**: Google Maps API
- **UI Components**: Material Design 3

## Dependencies

- AndroidX Core and AppCompat
- Material Design Components
- Room Database
- Lifecycle Components
- Google Maps and Location Services
- JavaMail for email functionality
- ViewPager2
- SwipeRefreshLayout

## Setup Instructions

1. Clone the repository
2. Open the project in Android Studio
3. Add your Google Maps API key in AndroidManifest.xml
4. Update EmailService.java with your email credentials
5. Build and run the project

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/smartparking/
│   │   │   ├── activities/       # Activity classes
│   │   │   ├── adapters/        # RecyclerView adapters
│   │   │   ├── database/        # Room database and DAOs
│   │   │   ├── fragments/       # Fragment classes
│   │   │   ├── models/          # Data models
│   │   │   └── utils/           # Utility classes
│   │   └── res/
│   │       ├── drawable/        # Images and drawable resources
│   │       ├── layout/          # XML layout files
│   │       ├── menu/           # Menu resources
│   │       └── values/         # Strings, colors, styles
│   └── androidTest/            # Instrumentation tests
└── build.gradle               # App-level Gradle build file
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Material Design Guidelines
- Google Maps Platform
- Android Room Documentation
- Android Developer Documentation
