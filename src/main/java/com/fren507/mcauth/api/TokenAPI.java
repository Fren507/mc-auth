package com.fren507.mcauth.api;

import com.fren507.mcauth.MCAuthMod;
import com.fren507.mcauth.manager.TokenManager;
import com.fren507.mcauth.model.TokenData;

import java.util.Optional;
import java.util.UUID;

/**
 * Öffentliche Schnittstelle für andere Mods,
 * die das MCAuth-System verwenden möchten.
 */
public final class TokenAPI {

    private TokenAPI() {
        // Keine Instanzen
    }

    /**
     * Prüft, ob ein Token gültig ist.
     *
     * @param token Token-String
     * @return true, wenn das Token existiert und gültig ist
     */
    public static boolean isTokenValid(String token) {
        return getToken(token)
                .map(TokenData::isValid)
                .orElse(false);
    }

    /**
     * Gibt die Daten eines Tokens zurück.
     *
     * @param token Token-String
     * @return TokenData oder leer
     */
    public static Optional<TokenData> getToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        TokenManager manager = getManager();

        if (manager == null) {
            return Optional.empty();
        }

        return manager.getToken(token);
    }

    /**
     * Findet das aktuelle gültige Token eines Spielers.
     *
     * @param playerUUID Spieler UUID
     * @return TokenData oder leer
     */
    public static Optional<TokenData> findPlayerToken(UUID playerUUID) {
        if (playerUUID == null) {
            return Optional.empty();
        }

        TokenManager manager = getManager();

        if (manager == null) {
            return Optional.empty();
        }

        return manager.findPlayerToken(playerUUID);
    }

    /**
     * Erstellt ein neues Token für einen Spieler.
     *
     * @param playerUUID Spieler UUID
     * @param playerName Spielername
     * @return Token-String oder leer
     */
    public static Optional<String> generateToken(UUID playerUUID, String playerName) {
        if (playerUUID == null || playerName == null) {
            return Optional.empty();
        }

        TokenManager manager = getManager();

        if (manager == null) {
            return Optional.empty();
        }

        return Optional.of(manager.generateToken(playerUUID, playerName));
    }

    private static TokenManager getManager() {
        return MCAuthMod.getTokenManager();
    }
}