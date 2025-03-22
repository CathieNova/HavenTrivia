# HavenTrivia

is a server-side Forge mod that adds trivia questions with rewards and a leaderboard.
It’s fully automated, configurable, and requires no client mod.

---

## 🎮 Commands

### Public

- `/trivia leaderboard`  
  Shows the top 10 trivia scorers.

- `/trivia about`  
  Lists how many trivia questions and rewards exist for each category.

### Admin-only (`haventrivia.admin`)

- `/trivia start`  
  Starts the automatic trivia scheduler.

- `/trivia stop`  
  Stops auto-trivia.

- `/trivia ask`  
  Asks a trivia question immediately.

- `/trivia timeout`  
  Forces the current question to time out.

- `/trivia reload`  
  Reloads `questions.json` and `rewards.json`.

- `/trivia add <player|all> <amount>`  
  Adds points to a player or all online players.

- `/trivia remove <player|all> <amount>`  
  Removes points from a player or all players on the leaderboard.

- `/trivia reset <player|all>`  
  Resets score for a player or clears the entire leaderboard.

---

## ⚙ Config (`haventrivia-Server.toml`)

- `interval` – Seconds between automatic trivia questions  
  Set to `0` to disable auto mode.

- `minPlayers` – Minimum online players needed to trigger trivia

- `maxTime` – Time (in seconds) before a question times out

- `prefix` – Text prefix added to all trivia messages

---

## 🧠 How it works

- Questions are loaded from `questions.json`, organized by difficulty
- Rewards (items and commands) are defined per category in `rewards.json`
- Trivia is broadcast globally and rewards the first correct answer
- Each correct answer gives the player 1 point
- Points are saved and ranked in a persistent leaderboard
- Data is stored in the world folder:  
  `serverconfig/haventrivia/`

---

## 🛡 Permissions

Admin commands require:
- `haventrivia.admin`

If no permissions mod is present, the mod defaults to vanilla permission level 2 (OP).

---