package com.mahghuuuls.combatinhibited.util.reapplicationlimiter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ReapplicationLimiter {

    private final int maxReapplications;
    private final Map<UUID, Integer> reapplicationCounts = new HashMap<>();
    private boolean unlimitedApplication = false;

    public ReapplicationLimiter(int maxReapplications) {
        this.maxReapplications = maxReapplications;
        if (maxReapplications < 0) {
            this.unlimitedApplication = true;
        }
    }

    public void reset(UUID playerId) {
        if (playerId == null) return;
        if (unlimitedApplication) return;
        reapplicationCounts.put(playerId, 0);
    }

    public boolean canApply(UUID playerId) {
        if (unlimitedApplication) return true;
        if (playerId == null) return false;
        return reapplicationCounts.getOrDefault(playerId, 0) < maxReapplications;
    }

    public void recordApplication(UUID playerId) {
        if (unlimitedApplication) return;
        if (playerId == null) return;
        reapplicationCounts.put(playerId, reapplicationCounts.getOrDefault(playerId, 0) + 1);
    }
}
