# project26

## Team Members

* John Li
* Jack Zhou
* Nick Wu
* Kevin Li

## Project Overview

This project is a console-based banking application written in Java. It supports customer account management, recurring payments, fee handling, monthly statements, customer authentication, and administrator-only operations such as interest, fee collection, and account freezing.

## Implemented User Stories

1. A bank customer can deposit into an existing account.
2. A bank customer can withdraw from an account.
3. A bank customer can check an account balance.
4. A bank customer can view transaction history for an account.
5. A bank customer can create an additional account with the bank.
6. A bank customer can close an existing account.
7. A bank customer can transfer money from one account to another.
8. A bank administrator can collect fees from existing accounts.
9. A bank administrator can add an interest payment to an existing account.
10. A bank customer can set up an automatic recurring payment to another account.
11. A bank administrator can log in and perform admin-only actions.
12. A bank customer can set a maximum withdrawal amount for an account.
13. A bank administrator can see all transactions for a particular account.
14. A bank administrator can freeze or unfreeze a customer account.
15. A bank customer can set a password or PIN for account security.
16. A bank customer can receive notifications when a fee is pending before its due date.
17. A bank customer can update personal information.
18. A bank user can see all account information in an account dashboard after logging in.
19. A new user can register as a bank customer and create an initial account.
20. A bank customer can view monthly account statements.
21. A bank administrator can create a pending fee for a customer account.
22. A bank customer can pay a pending fee.
23. A bank customer can receive a warning when a transaction would cause low balance or overdraft risk.
24. A bank customer should be able to see an AI genereated summary and suggestion based on how many times they withdrawn, deposited and balance on the account.
25. A bank customer should be able to chat with an AI and ask general question in the chat.

## Run the Application

```bash
chmod +x runApp.sh
./runApp.sh
```

The application stores data in `bank-data.dat` in the project root. On a fresh run, when that file does not exist yet, the app seeds a demo customer so the main user flows can be exercised immediately. Once the file exists, the app loads the saved data instead.

If you want to reset back to the demo data, delete `bank-data.dat` and run the app again.

## Run the Tests

```bash
mkdir -p out/test-classes
javac -cp test-lib/junit-platform-console-standalone-1.13.0-M3.jar -d out/test-classes src/main/*.java src/test/*.java
java -jar test-lib/junit-platform-console-standalone-1.13.0-M3.jar --class-path out/test-classes --scan-class-path
```

## Demo Credentials

### Admin

```text
Password: admin123
```

### Sample Customer

Available automatically on a fresh run before `bank-data.dat` is created.

```text
Name: Sample User
Email: sample.user@237bank.com
Password: user123
PIN: 1234
```

The seeded customer starts with two accounts so transfer and recurring-payment flows are available immediately.
