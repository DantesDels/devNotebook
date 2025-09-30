# 📝 Dev Notebook (Java 17, Swing)

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
![Java](https://img.shields.io/badge/Java-17%2B-orange)
![Swing](https://img.shields.io/badge/UI-Swing-5c5c5c)
![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20Linux%20%7C%20macOS-6aa)

---

## 📝 Description

Carnet de notes interactif sur les bonnes pratiques de développement, avec sections éditables, mise en forme (Gras/Italique/H1/H2/Liste), enregistrement, réinitialisation, onglets dynamiques et persistance utilisateur.

---

## 🌟 Fonctionnalités

- 5 onglets intégrés: Lisibilité, Sécurité, Commentaires, Refactorisation, Scalabilité
- Affichage HTML avec sections « Bonnes pratiques » et « À éviter »

Puis à venir :
- Modifier / Enregistrer / Supprimer (réinitialiser vers le défaut)
- Mise en forme: Gras (Ctrl+B), Italique (Ctrl+I), H1 (Ctrl+1), H2 (Ctrl+2), Paragraphe (Ctrl+0)
- Annuler / Rétablir (Ctrl+Z / Ctrl+Y)
- Onglets dynamiques: création via l’onglet « + » et suppression via clic droit
- Persistance du contenu: `~/.dev-notebook/<section>.html`
- Persistance des onglets: `~/.dev-notebook/tabs.json`

---

## 🧭 Table des matières

- Présentation rapide
- Prérequis
- Installation & Lancement (PowerShell)
- Structure du projet
- Migration (com.example → com.ynov)
- Roadmap
- Bonnes pratiques & conventions
- Dépannage
- Contribuer
- Licence
- Contact

---

## ✅ Prérequis

- JDK 17+
- Git (recommandé)
- VS Code (recommandé) avec Java Extension Pack

---

## ⚙️ Installation & Lancement (via PowerShell)

```powershell
# 1) Compiler toutes les classes Java

$files = Get-ChildItem -Recurse -Filter *.java -Path src\main\java | Select-Object -ExpandProperty FullName
if (!(Test-Path -Path out)) { New-Item -ItemType Directory -Path out | Out-Null }
javac -encoding UTF-8 -d out -cp out $files


# 2) Exécuter (ajouter les resources au classpath)

java -cp "out;src\main\resources" com.ynov.devnotebook.App
```

Astuce
- Sous Linux/macOS, remplacez le séparateur de classpath `;` par `:`.
- Vous pouvez créer des tâches VS Code (Build/Run) pour accélérer le cycle dev.

---

## 🧱 Structure du projet

```
DevNotebook/
├── src/main/java/com/ynov/devnotebook/
│   ├── App.java                 # Point d’entrée + configuration des logs
│   ├── ContentService.java      # Chargement/enregistrement/suppression du contenu
│   ├── PathsUtil.java           # Utilitaires de chemins (~/.dev-notebook)
│   └── ui/
│       ├── MainFrame.java       # Fenêtre principale + barre de menu + onglets dynamiques
│       ├── SectionPanel.java    # Panneau éditable réutilisable (HTML + toolbar + undo/redo)
│       ├── IconFactory.java     # Icônes (ex: plus)
│       └── sections/
│           ├── ReadabilitySection.java    # Lisibilité du code
│           ├── SecuritySection.java       # Sécurité des informations
│           ├── CommentsSection.java       # Commentaires
│           ├── RefactoringSection.java    # Refactorisation
│           └── ScalabilitySection.java    # Scalabilité
└── src/main/resources/
        ├── logging.properties       # Configuration java.util.logging
        └── content/
                ├── readability.html
                ├── security.html
                ├── comments.html
                ├── refactoring.html
                └── scalability.html
```

Note: si vos ressources sont actuellement sous `src/ressources/`, déplacez-les vers `src/main/resources/` pour qu’elles soient chargées sur le classpath standard.

---

# État actuel
- ✅ App.java, ContentService.java, PathsUtil.java
- ✅ UI: MainFrame.java, SectionPanel.java, IconFactory.java (en progression)
- ✅ Sections: Readability, Security, Comments, Refactoring, Scalability .html réalisées
- 🔧 Vérifier l’arborescence des ressources (`src/main/resources`)

---

## 🗺️ Roadmap

### 💎 Milestone 1 — Début de projet
- Gestion de l'UI: MainFrame, SectionPanel, IconFactory
- Sections créées: Readability, Security, Comments, Refactoring, Scalability
- Corrections: cohérence `PathsUtil` (nom de classe/fichier), suppression des doublons (ex: `RefractoringSection`)

#### Critères d’acceptation
- L’application démarre via `com.ynov.devnotebook.App`
- Les 5 onglets intégrés affichent le HTML par défaut
- Compilation sans erreurs de package/classpath

### 💎 Milestone 2 — Fonctionnalités UI proposées
- SectionPanel: Modifier/Lecture, Enregistrer, Supprimer
- Mise en forme: B/I/H1/H2/P + raccourcis Ctrl+B/I/1/2/0
- Undo/Redo: Ctrl+Z/Ctrl+Y
- Onglets dynamiques: onglet « + » pour ajouter, clic droit pour supprimer
- Persistance: `~/.dev-notebook/<key>.html` et `tabs.json`

#### Critères d’acceptation
- Création/suppression d’onglets personnalisés OK
- Sauvegarde/restauration du HTML utilisateur OK
- First run sans erreur

### 💎 Milestone 3 — Build, Run, DX
- README mis à jour (PowerShell, classpath `;`)
- Tâches VS Code (Build/Run) optionnelles
- Optionnel: wrapper Gradle + tâches `run`/`jar` 
(besoin de se renseigner sur le fonctionnement de Gradle)

#### Critères d’acceptation
- Build/Run via tâches VS Code en un clic
- Optionnel: `gradlew run` si wrapper compris & ajouté

### 💎 Milestone 4 — Qualité et robustesse
- Tests unitaires ciblés (sans dépendances externes)
    - ContentService: load défaut, save, delete
    - tabs.json: sérialisation/désérialisation parser minimal

#### Cas limites
    - Première exécution (pas de `~/.dev-notebook`)
    - HTML invalide en override (pas de crash)
    - Dossier override en lecture seule (message d’erreur + logs)
    - Contenu UTF-8 (accents)

### 💎 Milestone 5 — Finitions UX
- Icônes: plus lisibles en Light/Dark, 
éventuellement toolbar pour B/I/H1/H2/P
- Utilisabilité: persister le dernier onglet sélectionné, confirmations, scroll en haut après chargement
- Accessibilité: navigation clavier, mnémotechniques menu

### 💎 Milestone 6 — Améliorations (optionnel)
- Autosave activée via toggler
- Drag & drop pour réordonner les onglets + persistance
- Export d’une section en HTML
- i18n (FR/EN) via ResourceBundle (internationalisation: rendre l’appli traduisible sans toucher au code.)
- Éditeur HTML plus riche (sans dépendances si possible)

---

## 📏 Bonnes pratiques & conventions

- Java 17, pas de dépendances externes
- Swing sur l’EDT (Event Dispatch Thread)
- UTF-8 partout (compilateur, I/O)
- HTML minimal et valide, deux sections: « Bonnes pratiques » et « À éviter »

---

## 🧪 Dépannage (FAQ)

- Erreur de classpath lors du run → Vérifiez le séparateur (`;` sous Windows, `:` sous Linux/macOS) et la présence de `src/main/resources`.

- Contenu par défaut non chargé → Assurez-vous que les fichiers HTML sont sous `src/main/resources/content/` et qu’ils sont sur le classpath.

- Pas de dossier `~/.dev-notebook` → Il est créé à la première sauvegarde; sinon créez-le manuellement.

---

## 🤝 Contribuer

Les contributions sont les bienvenues !

1. Forkez le dépôt

2. Créez une branche
     ```powershell
     git checkout -b feature/NouvelleFonctionnalite
     ```
3. Commitez vos changements
     ```powershell
     git commit -m "feat: ajoute NouvelleFonctionnalite"
     ```
4. Poussez et ouvrez une Pull Request

---

## 📝 Licence

Ce projet est sous licence MIT — voir [LICENSE](LICENSE).

---

## 📬 Contact

- Auteur: Sébastien DELVER
- Email: sebastien.delver@ynov.com
- GitHub: https://github.com/DantesDels

---

Fait avec le ❤️ pour apprendre, partager et améliorer nos pratiques de développement.