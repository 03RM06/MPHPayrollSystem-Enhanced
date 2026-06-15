MPHPayrollSystem

A Java-based Payroll Management System developed using NetBeans IDE and Maven.
This system manages employee records, attendance, leave requests, and payroll computation with role-based access control.

Features
Employee Management (Create, View, Update)
Attendance Tracking
Leave Management System
Payroll Computation
Payslip Generation
Role-Based Access Control (Admin / Employee)
Secure password handling (BCrypt)
Salary deductions and tax computation
Project Structure
src/main/java/
│
├── DAO/              # Database Access Layer
├── Model/            # Data Models (Employee, Attendance, etc.)
├── Services/         # Business Logic Layer
├── UI/               # Java Swing Interfaces
├── Utility/          # Helper Classes
└── com/mycompany/    # Main Application Entry Point
🛠️ Technologies Used
Java (JDK 8+)
NetBeans IDE
Maven
MySQL (assumed backend database)
Swing UI
BCrypt (security hashing)
How to Run the Project

Clone the repository:

git clone https://github.com/03RM06/MPHPayrollSystem.git
Open the project in NetBeans IDE

Build the project using Maven:

mvn clean install

Run the main class:

MPHPayrollSystem.java

Login Page
Admin Dashboard
Payslip Form
Employee Management Screen
👨‍💻 Developer
Developed by: 03RM06
Project: Payroll Management System
📄 License

This project is for educational purposes only.
