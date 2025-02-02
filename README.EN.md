Here’s the English translation of your document:  

---

<div align="center">  

<img src="https://github.com/founchoo/GongYun-for-Android/assets/24630338/3d5c2914-0592-4058-9b54-00e958c62b63" alt="Logo" width="100">  

# GongYun  
</div>  

## Introduction  

Welcome to the **GongYun** GitHub open-source page! This is a third-party academic management system designed for **Hubei University of Technology** students. It runs on the **Android** platform and supports a minimum version of **Android 8.0 (API level 26)**.  

This application is developed using the Jetpack Compose framework and written in Kotlin.  

During operation, the app **encrypts** and stores sensitive user data locally (such as student ID, password, enrollment year, and current academic year/semester). These details are only used for network requests and will not be used for other purposes.  

The reason for storing student ID and password is as follows: Even when selecting the "Remember Me" option while logging into the university's academic system, the returned session cookie expires just two hours after login. To prevent users from having to repeatedly enter their credentials after this period, the app stores them locally. If a network request fails due to an expired session, the app will automatically log in on behalf of the user.  

## Features  

Currently, the app offers the following functionalities:  

1. **Course-related**  
   - View class schedules for different academic years and weeks  
   - Home screen widget displaying the current day's schedule  
   - View course notes (e.g., online classes, project courses)  
   - Search for teacher schedules  
   - Class reminders  
   - View planned courses  
   - Find available classrooms and classrooms currently in use  

2. **Grade-related**  
   - View ranking (grade, major, and class rankings)  
   - Bar chart representation of rankings  
   - Automatic calculation of GPA and arithmetic average score  
   - View GPA trend curve  
   - Filter grades by academic year and course type  
   - Sort grades by score and credits  
   - Grade distribution bar chart  
   - New grade notifications  

## Screenshots  

**Note:** To protect privacy, some information has been obscured with asterisks. This will not occur during normal app usage.  

<img src="https://github.com/founchoo/GongYun-for-Android/assets/24630338/e0385777-f842-42b4-b20b-5bfa177dd3a3">  

## Download  

- **GitHub Releases**: [Click here](https://github.com/founchoo/CampusHelper/releases/latest) to go to the download page. The `.apk` file at the bottom of the page is the installation package. Simply download and install it.  
- **Google Play Store**: [Click here to download from Google Play](https://play.google.com/store/apps/details?id=com.dart.campushelper).  

## Contributing  

Found a bug? Want a new feature? Create an issue describing the problem you encountered.  
Already written code and want to merge it into the main branch? Please create a pull request.  

We look forward to your contributions and hope to continue developing this project further!  

## References  

- https://stackoverflow.com/  
- https://m3.material.io/  
- https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary  
- https://google.github.io/accompanist/placeholder/  
- https://json2kt.com/  
- https://github.com/harmittaa/KoinExample  
- https://github.com/patrykandpatrick/vico  
- https://plugins.jetbrains.com/plugin/18619-svg-to-compose  
- https://github.com/osipxd/encrypted-datastore  

## License  

This project is licensed under the **Apache License 2.0**.  
