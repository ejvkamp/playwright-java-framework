
## Playwright Automation Framework for the Much Ado To You Practice Shop

This repository contains the automated end-to-end test suite for the Much Ado To You practice shop (a demo e-commerce website), built with Playwright for Java, TestNG, and Maven.

## About This Project

This framework is designed to validate the core e-commerce functionality of the Much Ado To You practice shop. It follows the Page Object Model (POM) design pattern to ensure the test suite is readable, maintainable, and scalable.

## Getting Started

### Prerequisites

* Java Development Kit (JDK) 25 or higher
* Apache Maven
* Eclipse IDE for Java Developers

### Setup

1.  **Clone the repository:** 
`git clone https://github.com/your-username/your-repo-name.git`
2.  **Open in Eclipse:** 
Import the project as an "Existing Maven Project."
3.  **Install Playwright Browsers:** 
Open a terminal in the project root and run the command: 
`mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install"`

## Start the Practice Shop First
The tests run against the Much Ado To You practice shop on your own computer. Start it before you run any test and leave it running.

* **To get the shop:** `practice-shop.jar` is in the [much-ado-practice-shop](much-ado-practice-shop/) folder of this repository. You need Java 17 or newer.
* **To install, start and use it:** follow the [practice shop README](much-ado-practice-shop/README.md).

## Running the Tests
* **To run the full regression suite:**
    `mvn test -DsuiteXmlFile=testng.xml`
git
