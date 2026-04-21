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
5. A bank customer can create an additional account.
6. A bank customer can close an existing account.
7. A bank customer can transfer money between accounts.
8. A bank administrator can collect fees from existing accounts.
9. A bank administrator can add interest to an existing account.
10. A bank administrator can log in to perform admin-only actions.
11. A bank customer can receive pending-fee notifications while using the account menus.
12. A bank administrator can view transaction history for a specific customer account.
13. A bank customer can set up recurring payments between accounts.
14. A bank administrator can freeze or unfreeze an account.
15. A bank customer can set a maximum withdrawal amount.
16. A bank customer can set a password or PIN for account security.
17. A bank customer can update personal information.

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
