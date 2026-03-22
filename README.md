# CATYA – Browser Automation DSL Framework

CATYA is a **Java + Selenium-based automation framework** that executes browser actions using a custom **DSL (Domain-Specific Language)**.

---

## Features
- DSL-driven automation (no coding required)
- Excel-based test scripts
- Selenium WebDriver integration
- Screenshot on failure
- Conditional logic (IF / LOOP)
- Data-driven testing via properties files

---

## Requirements
- Java 8+
- Maven 3+
- Chrome / Firefox
- WebDriver (ChromeDriver, GeckoDriver)

---

## Setup

```bash
git clone <repo>
cd catya
mvn clean compile
mvn mvn exec:java
```

---

## DSL Syntax

```
COMMAND param="value"
```

---

## Commands

### Browser Control
```
OPEN browser="chrome"
CLOSE browser="chrome"
QUIT
```

### Navigation
```
NAVIGATE url="https://example.com" timeout=10
REFRESH
```

### Actions
```
CLICK id="loginBtn"
DOUBLECLICK id="item1"
INPUT id="username" value="admin"
CLEAR id="username"
```

### Verification
```
VERIFY id="message" value="Success"
IS_ELEMENT_VISIBLE id="dashboard" value="true"
IS_ELEMENT_ENABLED id="submitBtn" value="true"
IS_ELEMENT_SELECTED id="rememberMe" value="true"
```

### Waits
```
WAIT_VISIBLE id="dashboard" timeout=30
WAIT_CLICKABLE id="submitBtn" timeout=20
PAUSE time=3
```

### Dropdown
```
SELECT id="country" name="Japan"
SELECT id="country" index="1,2"
DESELECT id="country" index="all"
```

### Screenshot
```
PRINTSCREEN itemNo=1
```

### Data
```
LOAD data="login"
```

### Flow Control
```
IF $result is Passed
FI

LOOP 3 times
POOL
EXIT loop
```

---

## Example: Login Test

```txt
OPEN browser="chrome"

NAVIGATE url="https://example.com/login"

INPUT id="username" value="admin"
INPUT id="password" value="password123"

CLICK id="loginBtn"

WAIT_VISIBLE id="dashboard" timeout=10

VERIFY id="welcomeMsg" value="Welcome Admin"

CLOSE browser="chrome"
```

---

## Data Driven Example

### data/login.properties
```
username=admin
password=1234
url=https://example.com/login
```

### Script
```
LOAD data="login"

NAVIGATE url=$ url
INPUT id="username" value=$ username
INPUT id="password" value=$ password
```

---

## Architecture

```mermaid
flowchart TD
    A[Excel / DSL Script] --> B[DSLInterpreter]
    B --> C[Command Parser]
    C --> D[BrowserCommandImpl]
    D --> E[BrowserDriverFactory]
    E --> F[Selenium WebDriver]
    F --> G[ChromeDriver / FirefoxDriver]
    D --> H[Screenshots]
    B --> I[Flow Control IF / LOOP]
    B --> J[Data Loader]
    J --> K[.properties files]
```

---

## Notes
- Supported locators: id, xpath, name
- VERIFY supports equality only
- Default timeout ~10 seconds
- Screenshots saved under `/screenshots/`

---

## Summary

CATYA is a lightweight **test automation platform**:
- DSL (test definition)
- Java engine (execution)
- Selenium (browser control)
