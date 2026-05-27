# Playwright Automation Framework for the LambdaTest Playground Website

This repository contains the automated end-to-end test suite for the TestMu AI Playground e-commerce website, built with Playwright for Java, TestNG, and Maven.

## About This Project

This framework is designed to validate the core e-commerce functionality of the TestMu AI Playground website. It follows the Page Object Model (POM) design pattern to ensure the test suite is readable, maintainable, and scalable.

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
`mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install --with-deps"`

## Running the Tests
* **To run the full regression suite:**
    `mvn test -DsuiteXmlFile=testng.xml`

