# Deeploft - Setup Guide

This guide explains how to set up and run the Deeploft project locally.

## Prerequisites

Before starting, ensure you have the following installed:

- Android Studio
- Java JDK
- Maven
- Git
- Android Emulator or a physical Android device with Wireless Debugging enabled

---

## Installation & Running the Project

### 1. Clone the Repository

If you haven't cloned the repository:

```bash
git clone <repository-url>
cd <repository-name>
```

If you already have the repository:

```bash
git pull
```

---

### 2. Switch to the `testing-new` Branch

The complete project (Android application and backend) is available in the **`testing-new`** branch.

```bash
git checkout testing-new
```

---

### 3. Open the Project

Open the project using **Android Studio**.

---

### 4. Update the Backend Base URL

Navigate to:

```
Deeploft
└── app
    └── src
        └── main
            └── java
                └── com
                    └── example
                        └── deeploft
                            └── network
                                └── RetrofitClient.java
```

Locate the `BASE_URL` and replace the existing IP address with your computer's **Wireless LAN IPv4 Address**, while keeping the port as **8080**.

Example:

```java
private static final String BASE_URL = "http://192.168.1.10:8080/";
```

#### Finding Your IPv4 Address

Open **Command Prompt** and run:

```bash
ipconfig
```

Under **Wireless LAN adapter Wi-Fi**, locate:

```
IPv4 Address . . . . . . . . . . : 192.168.1.10
```

Replace the IP address in `RetrofitClient.java` with your own IPv4 address.

---

### 5. Start the Spring Boot Backend

Open the **Terminal** inside Android Studio.

Navigate to the backend folder:

```bash
cd backend
```

Run the Spring Boot application:

```bash
mvn spring-boot:run
```

Wait until the backend starts successfully on port **8080**.

---

### 6. Run the Android Application

Run the application using either:

- Android Emulator (Virtual Device)
- Physical Android device with **Wireless Debugging** enabled

Click the **Run ▶** button at the top of Android Studio.

---

## Login Instructions

To access the application as an administrator:

1. Launch the application.
2. On the **Home** page, select **Join as Instructor**.
3. Use the following email address:

```text
ashan@deeploft.com
```

4. Enter your name
5. Enter **any password of your choice**.
6. Click **Register**.

> **Note:** The application recognizes `ashan@deeploft.com` as the administrator account. Any password can be used for login.

---
## Course Bying Instructions

1. when buying a course as a student, select paypal as the payment from given payment options.
2. touch in pay and confirm button
3. course will be unlocked (don't need to provide any payment details)

## Project Structure

```
Deeploft/
│
├── app/                  # Android application
├── backend/              # Spring Boot backend
└── README.md
```

---

## Notes

- Ensure your Android device and your computer are connected to the **same Wi-Fi network**.
- The Spring Boot backend must be running before launching the Android application.
- If your computer's IPv4 address changes, update the `BASE_URL` in `RetrofitClient.java` accordingly.
- The complete project is available in the **`testing-new`** branch.

---

## Troubleshooting

### App cannot connect to the backend

- Verify that the backend is running successfully.
- Confirm that the correct IPv4 address is configured in `RetrofitClient.java`.
- Ensure both your computer and Android device are connected to the same Wi-Fi network.
- Check that port **8080** is not blocked by your firewall.

### Maven command not found

Ensure Maven is installed and added to your system's `PATH` environment variable.

---

## License

This project is intended for educational purposes.
