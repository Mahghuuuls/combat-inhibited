package com.mahghuuuls.combatinhibited.util.effectapplier;

import com.mahghuuuls.combatinhibited.modules.ProximityMode;
import com.mahghuuuls.combatinhibited.util.reapplicationlimiter.ApplicationSource;
import com.mahghuuuls.combatinhibited.util.reapplicationlimiter.ReapplicationLimiter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.UUID;

public final class ProximityEffectController {

    private final EffectApplier applier;
    private final Potion inhibitedPotion;
    private final ProximityMode mode;
    private final int refreshWhenRemainingAtMostTicks;
    private final ReapplicationLimiter reapplicationLimiter;

    public ProximityEffectController(EffectApplier applier,
                                     Potion inhibitedPotion,
                                     ProximityMode mode,
                                     int refreshWhenRemainingAtMostTicks,
                                     int maxReapplications,
                                     ApplicationSource applicationSource) {
        this.applier = applier;
        this.inhibitedPotion = inhibitedPotion;
        this.mode = mode == null ? ProximityMode.PREVENT_EXPIRING : mode;
        this.refreshWhenRemainingAtMostTicks = Math.max(0, refreshWhenRemainingAtMostTicks);
        this.reapplicationLimiter = new ReapplicationLimiter(maxReapplications);

        EffectApplyBus.register((player, source) -> {
            if (player == null) return;
            if (source != applicationSource) {
                reapplicationLimiter.reset(player.getUniqueID());
            }
        });
    }

    public void onMatch(EntityPlayer player) {
        if (player == null || applier == null) return;

        UUID playerId = player.getUniqueID();
        if (!reapplicationLimiter.canApply(playerId)) return;

        if (mode == ProximityMode.APPLY_EFFECT) {
            applyAndRecord(player, playerId);
            return;
        }

        if (inhibitedPotion == null) return;
        PotionEffect active = player.getActivePotionEffect(inhibitedPotion);
        if (active == null) return;

        if (active.getDuration() <= refreshWhenRemainingAtMostTicks) {
            applyAndRecord(player, playerId);
        }
    }

    public void onNoMatch(EntityPlayer player) {
        if (player == null) return;
        reapplicationLimiter.reset(player.getUniqueID());
    }

    private void applyAndRecord(EntityPlayer player, UUID playerId) {
        applier.apply(player);
        reapplicationLimiter.recordApplication(playerId);
    }
}
