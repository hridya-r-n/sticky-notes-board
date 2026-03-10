# 📌 Virtual Sticky Notes Board — Spring Boot

A full-featured virtual sticky notes board built with Spring Boot, JPA, and a rich drag-and-drop frontend.

---



##  Features

| Feature | Details |
|---|---|
| **Drag & Drop** | Drag notes anywhere on the board, position auto-saved |
| **Inline Editing** | Click any note title/content to edit in place |
| **Color Themes** | 10 note colors with one-click switching |
| **Pin Notes** | Pin important notes with a visual tape indicator |
| **Search** | Real-time search across title and content |
| **Filter Pinned** | Toggle to show only pinned notes |
| **Keyboard Shortcuts** | `Ctrl+N` new note, `Esc` close modal |
| **Sample Data** | 5 sample notes loaded on startup |
| **H2 Console** | Dev database UI at `/h2-console` |

---

## 🔌 REST API

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/notes` | Get all notes |
| `GET` | `/api/notes/{id}` | Get note by ID |
| `POST` | `/api/notes` | Create a new note |
| `PUT` | `/api/notes/{id}` | Update a note |
| `PATCH` | `/api/notes/{id}/position` | Update position |
| `PATCH` | `/api/notes/{id}/pin` | Toggle pin |
| `DELETE` | `/api/notes/{id}` | Delete a note |
| `DELETE` | `/api/notes` | Delete all notes |
| `GET` | `/api/notes/search?q=...` | Search notes |
| `GET` | `/api/notes/pinned` | Get pinned notes |

### Example: Create a Note
```bash
curl -X POST http://localhost:8080/api/notes \
  -H "Content-Type: application/json" \
  -d '{"title":"Hello","content":"World","color":"#fef08a","posX":100,"posY":100}'
```

---



## 🛠️ Tech Stack
- **Spring Boot 3.2** — Web, JPA, Validation
- **H2** — In-memory database (swap for MySQL/PostgreSQL easily)
- **Lombok** — Boilerplate reduction
- **Vanilla JS + CSS** — No frontend framework needed
- **Google Fonts** — Caveat + DM Sans

---

