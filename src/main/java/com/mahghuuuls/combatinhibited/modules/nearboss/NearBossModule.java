package com.mahghuuuls.combatinhibited.modules.nearboss;

import com.mahghuuuls.combatinhibited.modules.ProximityMode;
import com.mahghuuuls.combatinhibited.util.SideUtil;
import com.mahghuuuls.combatinhibited.util.effectapplier.EffectApplyBus;
import com.mahghuuuls.combatinhibited.util.effectapplier.EffectApplier;
import com.mahghuuuls.combatinhibited.util.entityscanner.EntityScanner;
import com.mahghuuuls.combatinhibited.util.reaplicationlimiter.ApplicationSource;
import com.mahghuuuls.combatinhibited.util.reaplicationlimiter.ReapplicationLimiter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Set;
import java.util.UUID;

public final class NearBossModule {

    private final EntityScanner scanner;
    private final EffectApplier applier;
    private final Potion inhibitedPotion;
    private final Set<String> bossList;
    private final double distanceBlocks;
    private final int scanPeriodTicks;
    private final boolean requireLineOfSight;
    private final ProximityMode mode;
    private final int refreshWhenRemainingAtMostTicks;
    private final ReapplicationLimiter reapplicationLimiter;

    public NearBossModule(EntityScanner scanner,
                          EffectApplier applier,
                          Potion inhibitedPotion,
                          Set<String> bossList,
                          double distanceBlocks,
                          int scanPeriodTicks,
                          boolean requireLineOfSight,
                          ProximityMode mode,
                          int refreshWhenRemainingAtMostTicks,
                          int maxReapplications) {
        this.scanner = scanner;
        this.applier = applier;
        this.inhibitedPotion = inhibitedPotion;
        this.bossList = bossList;
        this.distanceBlocks = distanceBlocks;
        this.scanPeriodTicks = Math.max(1, scanPeriodTicks);
        this.requireLineOfSight = requireLineOfSight;
        this.mode = mode == null ? ProximityMode.APPLY_EFFECT : mode;
        this.refreshWhenRemainingAtMostTicks = Math.max(0, refreshWhenRemainingAtMostTicks);
        this.reapplicationLimiter = new ReapplicationLimiter(maxReapplications);

        EffectApplyBus.register((player, source) -> {
            if (player == null) return;
            if (source != ApplicationSource.NEAR_BOSS) {
                reapplicationLimiter.reset(player.getUniqueID());
            }
        });
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        EntityPlayer player = event.player;
        if (player == null) return;
        if (SideUtil.isRemote(player)) return;

        if (distanceBlocks <= 0) return;
        if ((player.ticksExisted % scanPeriodTicks) != 0) return;

        UUID playerId = player.getUniqueID();
        if (bossList == null || bossList.isEmpty()) {
            reapplicationLimiter.reset(playerId);
            return;
        }

        boolean foundBoss = scanner.anyMatch(player, distanceBlocks, (p, e, id) -> {
            if (!bossList.contains(id)) return false;
            return !requireLineOfSight || p.canEntityBeSeen(e);
        });
        if (!foundBoss) {
            reapplicationLimiter.reset(playerId);
            return;
        }

        if (!reapplicationLimiter.canApply(playerId)) return;

        if (mode == ProximityMode.APPLY_EFFECT) {
            applier.apply(player);
            reapplicationLimiter.recordApplication(playerId);
            return;
        }

        if (inhibitedPotion == null) return;
        PotionEffect active = player.getActivePotionEffect(inhibitedPotion);
        if (active == null) return;

        if (active.getDuration() <= refreshWhenRemainingAtMostTicks) {
            applier.apply(player);
            reapplicationLimiter.recordApplication(playerId);
        }
    }
}
