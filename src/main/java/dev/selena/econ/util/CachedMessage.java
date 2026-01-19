package dev.selena.econ.util;

import com.hypixel.hytale.server.core.Message;

public record CachedMessage(long expiryTimeMillis, Message value) {
    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTimeMillis;
    }
}
