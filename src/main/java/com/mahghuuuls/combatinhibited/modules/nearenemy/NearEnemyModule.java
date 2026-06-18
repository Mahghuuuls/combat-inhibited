package com.mahghuuuls.combatinhibited.modules.nearenemy;

import com.mahghuuuls.combatinhibited.util.SideUtil;
import com.mahghuuuls.combatinhibited.util.reaplicationlimiter.ApplicationSource;
import com.mahghuuuls.combatinhibited.util.effectapplier.EffectApplier;
import com.mahghuuuls.combatinhibited.util.effectapplier.EffectApplyBus;
import com.mahghuuuls.combatinhibited.util.reaplicationlimiter.ReapplicationLimiter;
import com.mahghuuuls.combatinhibited.util.entityfilter.EntityContext;
import com.mahghuuuls.combatinhibited.util.entityfilter.EntityFilter;
import com.mahghuuuls.combatinhibited.util.entityscanner.EntityScanner;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.UUID;

public final class NearEnemyModule {

    private final EntityScanner scanner;
    private final EntityFilter filter;
    private final EffectApplier applier;
    private final Potion inhibitedPotion;

    private final double distanceBlocks;
    private final int scanPeriodTicks;
    private final boolean requireLineOfSight;
    private final Mode mode;
    private final int refreshWhenRemainingAtMostTicks;

    private final ReapplicationLimiter reapplicationLimiter;

    public NearEnemyModule(EntityScanner scanner,
                           EntityFilter filter,
                           EffectApplier applier,
                           Potion inhibitedPotion,
                           double distanceBlocks,
                           int scanPeriodTicks,
                           boolean requireLineOfSight,
                           Mode mode,
                           int refreshWhenRemainingAtMostTicks,
                           int maxReapplications) {

        this.scanner = scanner;
        this.filter = filter;
        this.applier = applier;
        this.inhibitedPotion = inhibitedPotion;

        this.distanceBlocks = distanceBlocks;
        this.scanPeriodTicks = Math.max(1, scanPeriodTicks);
        this.requireLineOfSight = requireLineOfSight;
        this.mode = (mode == null ? Mode.PREVENT_EXPIRING : mode);
        this.refreshWhenRemainingAtMostTicks = Math.max(0, refreshWhenRemainingAtMostTicks);

        this.reapplicationLimiter = new ReapplicationLimiter(maxReapplications);

        EffectApplyBus.register((player, source) -> {
            if (player == null) return;
            if (source != ApplicationSource.NEAR_ENEMY) {
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

        boolean found = scanner.anyMatch(player, distanceBlocks, (p, e, id) -> {
            if (requireLineOfSight && !p.canEntityBeSeen(e)) return false;

            EntityContext context = new EntityContext(p, e, id);
            return filter == null || filter.passes(context);
        });

        if (!found) {
            reapplicationLimiter.reset(playerId);
            return;
        }

        if (!reapplicationLimiter.canApply(playerId)) {
            return;
        }

        // APPLY_EFFECT
        if (mode == Mode.APPLY_EFFECT) {
            applier.apply(player);
            reapplicationLimiter.recordApplication(playerId);
            return;
        }

        // PREVENT_EXPIRING
        if (inhibitedPotion == null) return;
        PotionEffect active = player.getActivePotionEffect(inhibitedPotion);
        if (active == null) return;

        if (active.getDuration() <= refreshWhenRemainingAtMostTicks) {
            applier.apply(player);
            reapplicationLimiter.recordApplication(playerId);
        }
    }
}
