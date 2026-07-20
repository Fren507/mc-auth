package com.fren507.mcauth.api;

import com.fren507.mcauth.MCAuthMod;
import com.fren507.mcauth.model.TokenData;
import java.util.Optional;
import java.util.UUID;

public class TokenAPI {

    /**
     * Prüft, ob ein Token existiert und gültig (nicht abgelaufen) ist.
     */
    public static boolean isTokenValid(String token) {
        return MCAuthMod.getTokenManager().getToken(token)
                .map(TokenData::isValid)
                .orElse(false);
    }

    /**
     * Holt die Token-Daten anhand des Token-Strings.
     */
    public static Optional<TokenData> getToken(String token) {
        return MCAuthMod.getTokenManager().getToken(token);
    }

    /**
     * Sucht das aktuell gültige Token eines Spielers.
     */
    public static Optional<TokenData> findPlayerToken(UUID playerUUID) {
        return MCAuthMod.getTokenManager().findPlayerToken(playerUUID);
    }
}