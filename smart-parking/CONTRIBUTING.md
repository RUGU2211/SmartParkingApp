# Contributing to Smart Parking App

First off, thank you for considering contributing to Smart Parking App! It's people like you that make Smart Parking App such a great tool.

## Code of Conduct

This project and everyone participating in it is governed by our Code of Conduct. By participating, you are expected to uphold this code.

## How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check the issue list as you might find out that you don't need to create one. When you are creating a bug report, please include as many details as possible:

* Use a clear and descriptive title
* Describe the exact steps which reproduce the problem
* Provide specific examples to demonstrate the steps
* Describe the behavior you observed after following the steps
* Explain which behavior you expected to see instead and why
* Include screenshots if possible
* Include your environment details (OS, Android version, device model)

### Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues. When creating an enhancement suggestion, please include:

* Use a clear and descriptive title
* Provide a step-by-step description of the suggested enhancement
* Provide specific examples to demonstrate the steps
* Describe the current behavior and explain which behavior you expected to see instead
* Explain why this enhancement would be useful
* List some other applications where this enhancement exists, if applicable
* Include screenshots or mockups if possible

### Pull Requests

* Fill in the required template
* Do not include issue numbers in the PR title
* Follow the Java styleguides
* Include screenshots in your pull request whenever possible
* Document new code
* End all files with a newline

## Styleguides

### Git Commit Messages

* Use the present tense ("Add feature" not "Added feature")
* Use the imperative mood ("Move cursor to..." not "Moves cursor to...")
* Limit the first line to 72 characters or less
* Reference issues and pull requests liberally after the first line
* Consider starting the commit message with an applicable emoji:
    * 🎨 `:art:` when improving the format/structure of the code
    * 🐎 `:racehorse:` when improving performance
    * 🚱 `:non-potable_water:` when plugging memory leaks
    * 📝 `:memo:` when writing docs
    * 🐛 `:bug:` when fixing a bug
    * 🔥 `:fire:` when removing code or files
    * 💚 `:green_heart:` when fixing the CI build
    * ✅ `:white_check_mark:` when adding tests
    * 🔒 `:lock:` when dealing with security
    * ⬆️ `:arrow_up:` when upgrading dependencies
    * ⬇️ `:arrow_down:` when downgrading dependencies

### Java Styleguide

* Use 4 spaces for indentation
* Class names should be in PascalCase
* Method names should be in camelCase
* Variable names should be in camelCase
* Constant names should be in SCREAMING_SNAKE_CASE
* Use meaningful names for variables, methods, and classes
* Add comments for complex logic
* Follow the Single Responsibility Principle
* Keep methods short and focused
* Use appropriate access modifiers
* Handle exceptions appropriately
* Include Javadoc for public methods

### Documentation Styleguide

* Use [Markdown](https://guides.github.com/features/mastering-markdown/)
* Reference methods and classes in backticks
* Use code blocks for code examples
* Keep documentation up to date with code changes
* Include examples where appropriate
* Document both success and error scenarios
* Include links to related documentation

## Project Structure

Please maintain the existing project structure:

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/smartparking/
│   │   │   ├── activities/       # Activity classes
│   │   │   ├── adapters/        # RecyclerView adapters
│   │   │   ├── database/        # Room database and DAOs
│   │   │   ├── fragments/       # Fragment classes
│   │   │   ├── models/          # Data models
│   │   │   └── utils/           # Utility classes
│   │   └── res/
│   │       ├── drawable/        # Images and drawable resources
│   │       ├── layout/          # XML layout files
│   │       ├── menu/           # Menu resources
│   │       └── values/         # Strings, colors, styles
│   └── androidTest/            # Instrumentation tests
└── build.gradle               # App-level Gradle build file
```

## Additional Notes

### Issue and Pull Request Labels

Labels help us track and manage issues and pull requests.

* `bug` - Issues that are bugs
* `enhancement` - Issues that are feature requests
* `documentation` - Issues that are about documentation
* `help-wanted` - Issues that need assistance
* `wontfix` - Issues that will not be worked on
* `duplicate` - Issues that are duplicates of other issues
* `good first issue` - Good issues for first-time contributors

Thank you for contributing to Smart Parking App!
