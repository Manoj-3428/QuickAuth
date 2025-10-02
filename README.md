# 🔐 QuickAuth - Secure Authentication App

A modern, secure authentication application built with Android, Firebase, and Material Design 3. QuickAuth provides a seamless user experience with phone number verification, local notifications, and a beautiful UI.

## ✨ Features

### 🔐 **Authentication System**
- **Phone Number Verification**: Secure OTP-based authentication using Firebase Phone Auth
- **Sign Up Flow**: Complete user registration with name, email, and phone number
- **Sign In Flow**: Quick login for existing users
- **OTP Verification**: 6-digit OTP verification with beautiful animated input fields
- **Session Management**: Persistent user sessions with secure data storage

### 📱 **User Interface**
- **Material Design 3**: Modern, clean interface following Google's design guidelines
- **Dark Mode Support**: Consistent theming across light and dark modes
- **Smooth Animations**: Beautiful entrance animations and transitions
- **Responsive Design**: Optimized for all screen sizes
- **Custom App Icon**: Professional QuickAuth branding throughout

### 🏠 **Dashboard Features**
- **Home Screen**: Welcome dashboard with feature highlights
- **Settings Screen**: User profile management and app settings
- **Notifications Screen**: Interactive notification system with swipe-to-delete
- **Bottom Navigation**: Easy navigation between main sections

### 🔔 **Notification System**
- **Welcome Notifications**: Personalized welcome messages for new users
- **Welcome Back**: Greeting notifications for returning users
- **Permission Handling**: Smart notification permission requests
- **Local Notifications**: No server dependency for notifications
- **Badge Counter**: Dynamic notification count in navigation bar

### 🎨 **UI/UX Enhancements**
- **Animated OTP Input**: Smooth, interactive OTP entry with auto-focus
- **Phone Number Masking**: Secure display of phone numbers (e.g., 91******89)
- **Multi-line Email Support**: Auto-expanding email input fields
- **Swipe Gestures**: Swipe-to-delete notifications with confirmation
- **Loading States**: Visual feedback during authentication processes

### 🔒 **Security Features**
- **Firebase Integration**: Secure backend with Firebase Authentication
- **Data Encryption**: Secure storage of user credentials
- **Session Security**: Automatic session management and logout
- **Input Validation**: Comprehensive form validation and error handling

## 🛠️ **Technical Stack**

- **Language**: Kotlin
- **UI Framework**: Android Views with Data Binding
- **Backend**: Firebase Authentication & Firestore
- **Design**: Material Design 3
- **Architecture**: MVVM with Repository Pattern
- **Dependencies**: Firebase SDK, Material Components

## 📋 **Requirements**

- Android 6.0 (API level 23) or higher
- Internet connection for Firebase services
- Phone number for authentication

## 🚀 **Getting Started**

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/quickauth.git
   ```

2. **Open in Android Studio**
   - Import the project
   - Sync Gradle files

3. **Firebase Setup**
   - Create a Firebase project
   - Add your app to Firebase console
   - Download `google-services.json`
   - Place it in `app/` directory

4. **Configure Firebase**
   - Enable Phone Authentication
   - Set up Firestore database
   - Configure security rules

5. **Build and Run**
   ```bash
   ./gradlew assembleDebug
   ```

## 📱 **App Screenshots**

### Authentication Flow
- **Splash Screen**: QuickAuth logo with smooth animations
- **Sign Up**: User registration with form validation
- **Sign In**: Quick login for existing users
- **OTP Verification**: Secure 6-digit code verification

### Main App
- **Dashboard**: Feature-rich home screen
- **Settings**: User profile and app settings
- **Notifications**: Interactive notification management

## 🔧 **Configuration**

### Firebase Setup
1. Enable Phone Authentication in Firebase Console
2. Configure Firestore security rules
3. Add your app's SHA-1 fingerprint for release builds

### Notification Permissions
- App automatically requests notification permissions on Android 13+
- Graceful degradation if permissions are denied
- Notifications enhance user experience but aren't required

## 📊 **Features Overview**

| Feature | Description | Status |
|---------|-------------|--------|
| Phone Authentication | Firebase OTP verification | ✅ Complete |
| User Registration | Full signup flow with validation | ✅ Complete |
| Session Management | Persistent user sessions | ✅ Complete |
| Local Notifications | Welcome and status notifications | ✅ Complete |
| Dark Mode | Consistent theming support | ✅ Complete |
| Animations | Smooth UI transitions | ✅ Complete |
| Swipe Gestures | Interactive notification management | ✅ Complete |
| Material Design 3 | Modern UI components | ✅ Complete |

## 🎯 **User Journey**

1. **First Time User**
   - Opens app → Sees splash screen
   - Signs up → Enters details → Receives OTP
   - Verifies OTP → Account created → Welcome notification
   - Access dashboard with personalized experience

2. **Returning User**
   - Opens app → Signs in → Enters phone number
   - Receives OTP → Verifies → Welcome back notification
   - Access dashboard with saved preferences

## 🔒 **Security & Privacy**

- **Data Security**: All user data encrypted and stored securely
- **Firebase Security**: Industry-standard authentication
- **Local Storage**: Secure session management
- **No Data Sharing**: User data stays within the app
- **Permission Respect**: Only requests necessary permissions

## 🚀 **Performance**

- **Fast Loading**: Optimized app startup
- **Smooth Animations**: 60fps UI transitions
- **Efficient Memory**: Minimal resource usage
- **Quick Authentication**: Fast OTP verification
- **Responsive UI**: Instant user feedback


Screenshots:

