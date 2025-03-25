# POMAnalyzer
Analyze POM dependencies and vulnerabilities

## Overview

The `App.java` class is the entry point of the POMAnalyzer application. It performs the following tasks:

1. **Argument Parsing**:
    - The application expects at least one argument: the path to the `pom.xml` file.
    - Optionally, it can take two additional boolean arguments: `verbose` and `openInBrowser`.

2. **Main Method**:
    - The `main` method initializes the application by parsing the provided arguments.
    - It then calls the `parse` method of the `ParseMavenDependencyOutput` class to analyze the POM file.

3. **Logging**:
    - The application uses the `Logger` class to log messages and errors.

### Usage

To run the application, use the following command:

```sh
java -jar pomAnalyzer.jar <pathOfPomFile> [verbose] [openInBrowser] [output type]

output type: JSON, HTML, EXCEL  
```