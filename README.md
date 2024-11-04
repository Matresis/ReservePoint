# Reservation Management System

## Not yet deployed
**IP link to the page with project:** http://130.162.229.180/ 

## Overview
This is a **DEMO** of a project. 

It is only at the beginning of development therefore there are many elements missing such as security (hashing etc.) a few soon to be tables in database and many other functions which should be included in such system

This project is web-based Reservation Management System developed using **Spring Boot**. It allows users to register, log in, make reservations, and view existing reservations. 

## Features
- **User Registration**: Users can register with a unique username and email.
- **User Login**: Users can log in with their credentials.
- **Reservation Management**: Users can create and view their reservations.
- **Dashboard/Home Page**: Includes navigation options for making reservations, viewing reservations, and logging out.
- **Automatic Timestamp**: Registration and reservation creation automatically store the current timestamp.
- **Simple Error Handling**: Redirects to an error page for issues like existing usernames or failed logins.

## Technologies Used
- **Spring Boot** for the backend framework.
- **Thymeleaf** for rendering views.
- **MySQL Database** for storing users (logins), reservations
- **Maven** for project management.
- **HTML, CSS** for frontend templates.

## Endpoints
- `/register`: Displays the registration form.
- `/login`: Displays the login form.
- `/reserveForm`: Displays the reservation creation form.
- `/reservations`: Shows a list of all reservations.
- `/home`: Shows dashboard/home page with links to other endpoints: `/logout`, `/reserveForm`, `/reservations`.

