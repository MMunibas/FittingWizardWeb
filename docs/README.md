# Directory structure

- `docs`contains this documentation
- `fitting` contains the java source and gradle build files for the fitting web & wizard

# Project structure

The fitting project under `fitting` contains the following modules:

- `fitting-web`: The web implementation of the fitting wizard built with Apache Wicket.
- `fitting-wizard`: The JavaFX standalone implementation of the fitting wizard.
- `fitting-shared`: All components shared between `fitting-web` and `fitting-wizard`

# Setup requirements

The fitting web & wizard software requires the following installation:

- `Java 8`
- `Gaussian`
- `CHARMM`
- `Python`
- `Custom scripts`  

# Configuration

# Development environment

We used Gradle (http://gradle.org/) as build system. This allows developers to use their IDE of choise, as long as a Gradle plugin is available. However, we recommend to use IntelliJ (https://www.jetbrains.com/idea/). There is a free community edition available.

No installation of Gradle is required. The fitting solution includes the Gradle wrapper, which bootstraps the Gradle installation.

So the only requirement t

# Development with mocks

In order to provide a smooth and reproducible development setup, we created mock implementations of all important scripts, e.g. gaussian and CHARMM.

# Script setup

Details of the 