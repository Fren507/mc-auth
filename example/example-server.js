const express = require("express");
const fs = require("fs/promises");
const path = require("path");

const app = express();

app.use(express.urlencoded({ extended: true }));

const TOKENS_FILE = path.resolve(
    __dirname,
    "../run/config/mcauth/tokens.json"
);

async function verifyToken(token) {
    try {
        const data = await fs.readFile(TOKENS_FILE, "utf8");
        const tokens = JSON.parse(data);

        const tokenData = tokens.find(
            t => t.token === token
        );

        if (!tokenData) {
            return null;
        }

        if (!tokenData.valid) {
            return null;
        }

        if (new Date(tokenData.expires) < new Date()) {
            return null;
        }

        return tokenData;

    } catch (error) {
        console.error(error);
        return null;
    }
}


app.get("/", (req, res) => {
    res.send(`
<!DOCTYPE html>
<html>
<head>
<title>MCAuth Login</title>
<style>
body {
    background: #111;
    color: white;
    font-family: Arial;
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
}

.box {
    background: #222;
    padding: 30px;
    border-radius: 15px;
    width: 350px;
}

input, button {
    width: 100%;
    padding: 12px;
    margin-top: 10px;
}

button {
    background: #55ff55;
    border: none;
    cursor: pointer;
}
</style>
</head>

<body>

<div class="box">
<h2>🎮 Minecraft Login</h2>

<form method="POST" action="/verify">

<input
name="token"
placeholder="Verifizierungstoken"
required
/>

<button>
Anmelden
</button>

</form>
</div>

</body>
</html>
`);
});


app.post("/verify", async (req, res) => {

    const token = req.body.token;

    const player = await verifyToken(token);

    if (!player) {
        return res.status(401).send(`
            <h1>❌ Token ungültig</h1>
            <a href="/">Zurück</a>
        `);
    }


    res.send(`
<!DOCTYPE html>
<html>
<head>
<title>Erfolgreich</title>
</head>

<body>

<h1>✅ Willkommen ${player.playerName}</h1>

<p>
UUID:
${player.playerUUID}
</p>

<p>
Token gültig bis:
${player.expires}
</p>

</body>
</html>
`);
});


app.listen(3000, () => {
    console.log("MCAuth Web läuft auf http://localhost:3000");
});