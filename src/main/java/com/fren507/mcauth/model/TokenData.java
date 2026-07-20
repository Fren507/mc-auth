package com.fren507.mcauth.model;

import java.util.UUID;

public class TokenData {
    private String token;
    private UUID playerUUID;
    private String playerName;
    private String created;
    private String expires;
    private boolean valid;

    public TokenData(String token, UUID playerUUID, String playerName, String created, String expires, boolean valid) {
        this.token = token;
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.created = created;
        this.expires = expires;
        this.valid = valid;
    }

    // Getter und Setter
    public String getToken() { return token; }
    public UUID getPlayerUUID() { return playerUUID; }
    public String getPlayerName() { return playerName; }
    public String getCreated() { return created; }
    public String getExpires() { return expires; }
    public boolean isValid() { return valid; }

    public void setValid(boolean valid) { this.valid = valid; }
}
