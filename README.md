# Personal Expense Tracker

A modern JavaFX desktop application for tracking personal expenses, supporting multi-user login, monthly budgets, and category-based reporting.

## Features
- Multi-user authentication (login/signup)
- Dashboard with user info, budget, spent, and warnings
- Add, update, delete expenses
- Filter expenses by month, year, and category
- Budget exceed warnings
- Edit budget and logout
- Modern UI with custom CSS

## Requirements
- Java 21 (JDK)
- Maven
- MySQL Server

## Setup Instructions

### 1. Clone the repository
```powershell
git clone https://github.com/Shihab23033/PersonalExpenseTracker.git
cd PersonalExpenseTracker
```

### 2. Set JAVA_HOME (Windows PowerShell)
```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-21'
```
Or persistently:
```powershell
setx JAVA_HOME "C:\Program Files\Java\jdk-21"
```

### 3. Create MySQL Database and Tables
- Create database:
```sql
CREATE DATABASE expense_tracker;
```
- Create tables:
```sql
CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  fullname VARCHAR(200),
  budget DOUBLE DEFAULT 0
);

CREATE TABLE Category (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL
);

CREATE TABLE Expense (
  id INT AUTO_INCREMENT PRIMARY KEY,
  category_id INT,
  description VARCHAR(255),
  amount DOUBLE,
  date DATE,
  is_recurring BOOLEAN,
  user_id INT,
  FOREIGN KEY (category_id) REFERENCES Category(id),
  FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### 4. Configure DB Credentials
- Edit `src/main/java/com/personalexpensetracker/util/DBUtil.java` if needed:
  - Default: user=`root`, password=`shihab@123`, db=`expense_tracker`

### 5. Build and Run
```powershell
./mvnw.cmd clean javafx:run
```

## Usage
- Sign up or log in as a user
- Add expenses, set categories, and track monthly spending
- Use filters to view expenses by month, year, or category
- Edit your budget and logout securely

## Security Notes
- Passwords are currently stored in plaintext. For production, implement password hashing (e.g., BCrypt).

## License
MIT

## Author
Shihab(IT23033),Sajeeb(IT23032) 
