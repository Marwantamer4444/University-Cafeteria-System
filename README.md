# University Cafeteria System

A Java console application that models the daily workflow of a university cafeteria. The system supports student ordering, employee operations, menu management, payments, loyalty rewards, notifications, and revenue reports.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Project Structure](#project-structure)
- [Requirements](#requirements)
- [Build and Run](#build-and-run)
- [Default Credentials](#default-credentials)
- [Application Flow](#application-flow)
- [OOP Design Highlights](#oop-design-highlights)
- [Documentation](#documentation)
- [Notes](#notes)

## Overview

The project is designed as an Object-Oriented Programming case study for a university cafeteria. It separates responsibilities across domain classes, service classes, interfaces, enums, and payment strategies to make the application easier to understand, maintain, and extend.

Main users:

- Students: register, log in, browse the menu, manage cart items, place orders, pay, redeem loyalty points, and receive notifications.
- Cashiers: view pending cash orders and confirm cash payments.
- Chefs: view preparing orders and mark them as ready.
- Managers: manage menu items and view daily or weekly revenue summaries.
- Admin: register cafeteria employees.

## Features

- Student registration and login.
- Employee login by role.
- Admin-controlled employee registration.
- Menu browsing with item name, description, price, and category.
- Cart management and checkout.
- Cash and wallet payment options.
- Payment receipts and payment breakdowns.
- Loyalty points and free coffee redemption.
- Order status tracking: pending, preparing, ready, and related workflow states.
- Student and staff notifications.
- Manager reports for daily and weekly paid-order revenue.
- Clean Java source layout under `src/main/java`.

## Project Structure

```text
.
|-- src/
|   `-- main/
|       `-- java/              # Application source code
|-- docs/                      # Report and presentation files
|-- media/                     # (optional) local demo files (not pushed)
|-- build/                     # Generated compile output
|-- README.md                  # Project documentation
|-- .gitignore                 # Ignored local/generated files
`-- Project ITI OOP.iml        # IntelliJ IDEA module file


## Requirements

- Java Development Kit (JDK) 17 or newer.
- A terminal or PowerShell.
- Optional: IntelliJ IDEA for opening the project visually.

Check your Java installation:

```powershell
java -version
javac -version
```

## Build and Run

From the project root, compile the source files:

```powershell
javac -d build/classes src/main/java/*.java
```

Run the application:

```powershell
java -cp build/classes Main
```

Clean compiled output:

```powershell
Remove-Item -Recurse -Force build/classes
```

## Default Credentials

Admin login:

```text
Username: Maro
Password: 1234
```

Employees are registered from the Admin portal. After registration, employees can log in through the Employee option using their ID and PIN.

Students can create their own account from the Student portal.

## Application Flow

1. Start the application.
2. Choose one of the main portals:
   - Student
   - Employee
   - Admin
3. Students can register or log in, then browse the menu, add items to the cart, checkout, and pay.
4. Cashiers confirm cash payments for pending orders.
5. Chefs prepare paid orders and mark them as ready.
6. Managers update the menu and review revenue summaries.
7. Admin registers new cashiers, chefs, and managers.

## OOP Design Highlights

- Encapsulation: domain data is managed through classes such as `Student`, `Order`, `MenuItem`, `Money`, and `PaymentReceipt`.
- Inheritance: employee roles such as `Cashier`, `Chef`, and `Manager` extend the base `Employee` class.
- Interfaces: contracts such as `IMenuManager`, `IPaymentProcessor`, `IStudentRepository`, and `PaymentStrategy` define clear behavior boundaries.
- Polymorphism: different payment methods implement the `PaymentStrategy` interface.
- Factory pattern: `EmployeeFactory` centralizes employee creation based on role.
- Service-style classes: managers such as `MenuManager`, `OrderProcessor`, `StudentManager`, `Authentication`, `NotificationService`, and `ReportManager` handle application operations.
- Enums: `Role`, `Category`, and `OrderStatus` keep fixed values type-safe and readable.

## Documentation

Project documents are available in the `docs` folder:

- `Cafeteria_Project_Report.docx`
- `Cafeteria_Project_Report.pdf`
- `University-Cafeteria-System.pptx`

## Demo Video

The demo video is not uploaded directly to GitHub because it exceeds GitHub’s file size limit.

Watch the project demo here: 