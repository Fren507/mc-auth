# Web Integration für MC Auth System

Da die Datei `tokens.json` vom Mod synchronisiert wird, können Webanwendungen sie direkt auslesen (nur-Lese-Zugriff empfohlen), um Spieler zu authentifizieren.

### JSON Struktur

```json
[
  {
    "token": "AB73-K92L-XP81",
    "playerUUID": "uuid-example",
    "playerName": "Player",
    "created": "2026-07-20T12:00:00Z",
    "expires": "2026-07-20T14:00:00Z",
    "valid": true
  }
]
```

---

## Code Beispiele

### 1. Node.js + Express

Liest die JSON aus und bietet eine API-Route zur Prüfung an.

```javascript
const express = require("express");
const fs = require("fs");
const path = require("path");
const app = express();

const TOKENS_FILE = "/pfad/zu/minecraft/config/mcauth/tokens.json";

app.get("/api/verify/:token", (req, res) => {
  try {
    const rawData = fs.readFileSync(TOKENS_FILE);
    const tokens = JSON.parse(rawData);

    const tokenData = tokens.find((t) => t.token === req.params.token);

    if (!tokenData || !tokenData.valid) {
      return res
        .status(401)
        .json({ error: "Token ungültig oder nicht gefunden" });
    }

    const expires = new Date(tokenData.expires);
    if (expires < new Date()) {
      return res.status(401).json({ error: "Token abgelaufen" });
    }

    res.json({
      success: true,
      player: tokenData.playerName,
      uuid: tokenData.playerUUID,
    });
  } catch (e) {
    res.status(500).json({ error: "Serverfehler beim Lesen der Tokens" });
  }
});

app.listen(3000, () => console.log("Auth API läuft auf Port 3000"));
```

### 2. Python + Flask

```python
from flask import Flask, jsonify
import json
from datetime import datetime, timezone

app = Flask(__name__)
TOKENS_FILE = '/pfad/zu/minecraft/config/mcauth/tokens.json'

@app.route('/api/verify/<token>', methods=['GET'])
def verify_token(token):
try:
with open(TOKENS_FILE, 'r') as f:
tokens = json.load(f)

        for t in tokens:
            if t['token'] == token:
                if not t['valid']:
                    return jsonify({'error': 'Token invalid'}), 401

                expires = datetime.fromisoformat(t['expires'].replace('Z', '+00:00'))
                if expires < datetime.now(timezone.utc):
                    return jsonify({'error': 'Token abgelaufen'}), 401

                return jsonify({'success': True, 'player': t['playerName'], 'uuid': t['playerUUID']})

        return jsonify({'error': 'Token nicht gefunden'}), 404
    except Exception as e:
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
app.run(port=5000)
```

### 3. PHP

```php
<?php
header('Content-Type: application/json');
$token = $_GET['token'] ?? '';

if(empty($token)) {
    die(json_encode(['error' => 'Kein Token übergeben']));
}

$file = '/pfad/zu/minecraft/config/mcauth/tokens.json';
$data = json_decode(file_get_contents($file), true);

foreach($data as $t) {
    if($t['token'] === $token) {
        if(!$t['valid'] || strtotime($t['expires']) < time()) {
            http_response_code(401);
            die(json_encode(['error' => 'Token ungültig oder abgelaufen']));
        }
        die(json_encode([
            'success' => true,
            'player' => $t['playerName'],
            'uuid' => $t['playerUUID']
        ]));
    }
}
http_response_code(404);
echo json_encode(['error' => 'Token nicht gefunden']);
?>
```

### 4. Vanilla JavaScript (Frontend Anfrage an Node.js API)

```javascript
async function loginWithToken(token) {
  try {
    const response = await fetch(`http://deine-domain.com/api/verify/${token}`);
    const data = await response.json();

    if (response.ok) {
      console.log("Erfolgreich eingeloggt als:", data.player);
      // Weiterleitung zum Dashboard
    } else {
      alert("Fehler: " + data.error);
    }
  } catch (error) {
    console.error("Verbindungsfehler", error);
  }
}
```

### Java Beispiel (Server-seitig)

```java
import com.fren507.mcauth.api.TokenAPI;

public class ExampleUsage {

    public void authenticatePlayer(String token) {
        if (TokenAPI.isTokenValid(token)) {
            TokenData tokenData = TokenAPI.getToken(token).orElseThrow();
            System.out.println("Spieler " + tokenData.getPlayerName() + " erfolgreich authentifiziert!");
        } else {
            System.out.println("Ungültiges oder abgelaufenes Token.");
        }
    }
}
```
