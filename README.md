Below is an updated **README** with icons/emojis that make it more visually appealing. Feel free to adjust any icon choices to best suit your application’s style.

---

# ✂️ HairQueue

HairQueue is an Android application designed to manage appointments for a hair salon. The app allows users to book appointments, view available and occupied slots, and manage their schedules.

## ✨ Features

- **🔐 User Registration and Authentication**: Users can create accounts and log in.
- **🗓️ Appointment Booking**: Users can view available slots and book them.
- **👩‍💼 Admin Management**: Admins can adjust schedules, set constraints, and oversee all bookings.
- **🔥 Firebase Integration**: The app leverages Firebase Realtime Database for storing user and appointment info.

## ⚙️ Installation

1. **Clone the repository**:
   ```sh
   git clone https://github.com/yourusername/hairqueue.git
   ```
2. **Open the project** in Android Studio.
3. **Sync the Gradle** files.
4. **Configure Firebase**:
   - Add your `google-services.json` file in the `app` directory.
   - Make sure **Firebase Realtime Database** and **Firebase Authentication** are enabled in your Firebase project.

## 🚀 Usage

### 👤 User Registration

1. Open the app and go to the registration screen.
2. Fill in your details to sign up.
3. Log in using your newly created credentials.

### 📅 Booking an Appointment

1. Navigate to the *Available Appointments* screen.
2. Select a **date** to see available time slots.
3. Tap on a desired slot to **book** your appointment.

### 🛠️ Admin Management

1. Log in with **admin** privileges.
2. Go to the *Schedule Management* screen.
3. Set working hours, lunch breaks, and constraints.
4. **Save** to update the schedule for all users.

## 🏗️ Project Structure

- `app/src/main/java/com/example/hairqueue/activities`: Main activity and other activity classes.
- `app/src/main/java/com/example/hairqueue/Fragments`: Fragment classes for different screens/views.
- `app/src/main/java/com/example/hairqueue/Adapters`: RecyclerView adapters.
- `app/src/main/java/com/example/hairqueue/Models`: Data model classes (e.g., AppointmentModel).
- `app/src/main/res/layout`: XML layout files for various screens.
- `app/src/main/res/values`: Resource files like `strings.xml`, `colors.xml`, and `styles.xml`.

## 📦 Dependencies

- **AndroidX** Libraries
- **Firebase Realtime Database**
- **Firebase Authentication**
- **Material Components**

## 🤝 Contributing

1. **Fork** the repository.
2. Create a **new branch** (`git checkout -b feature-branch`).
3. Make your changes and **commit** them (`git commit -m 'Add some feature'`).
4. **Push** to your branch (`git push origin feature-branch`).
5. Open a **pull request** to merge changes.


## 🙌 Acknowledgements

- [Firebase](https://firebase.google.com/)
- [Android Studio](https://developer.android.com/studio)
- [Material Design](https://material.io/design)
