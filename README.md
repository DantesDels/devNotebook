# Dev Notebook

Interactive notebook for Java development best practices with editable sections, formatting, and persistence.


## Prerequisites
- JDK 17+
- Git for version control
- VS Code recommended (with Java Extension Pack)


## Development Workflow

### Project Structure
```
DevNotebook/
├── src/main/java/com/ynov/devnotebook/
│   ├── App.java                    # Entry point + logging config
│   ├── ContentService.java         # Content management (load/save/delete)
│   ├── PathsUtil.java             # Path utilities (~/.dev-notebook)
│   └── ui/
│       ├── MainFrame.java          # Main window + tabbed interface
│       ├── SectionPanel.java       # Reusable editable panel
│       ├── IconFactory.java        # Icon generation (B/I/+)
│       └── sections/
│           ├── ReadabilitySection.java    # Code readability
│           ├── SecuritySection.java       # Information security
│           ├── CommentsSection.java       # Code comments
│           ├── RefactoringSection.java    # Refactoring practices
│           └── ScalabilitySection.java    # Scalability principles
└── src/main/resources/
    └── content/
        ├── readability.html        # Default readability content
        ├── security.html           # Default security content
        ├── comments.html           # Default comments content
        ├── refactoring.html        # Default refactoring content
        └── scalability.html        # Default scalability content
```


## Current Status
🚧 **Project under development**

Project structure initialized. Next steps:
- [ ] Core Java classes
- [ ] Swing UI implementation  
- [ ] Content persistence
- [ ] HTML formatting features


### Development Conventions
- **Language**: Java 17, no external dependencies
- **UI Framework**: Swing (EDT-safe)
- **Encoding**: UTF-8 everywhere
- **Content**: HTML sections with "Bonnes pratiques" and "À éviter"
- **Persistence**: User data in ~/.dev-notebook/ directory


## Development Setup
See [REQUIREMENTS.md](REQUIREMENTS.md) for detailed prerequisites.


## Build and Run
```bash
# Compile (Javac)
javac -encoding UTF-8 -d out -cp out src/main/java/**/*.java

# Run (Java)
java -cp "out:src/main/resources" com.ynov.devnotebook.App
```