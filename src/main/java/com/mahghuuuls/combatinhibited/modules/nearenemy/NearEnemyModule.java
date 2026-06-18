package com.mahghuuuls.combatinhibited.modules.nearenemy;

import com.mahghuuuls.combatinhibited.util.SideUtil;
import com.mahghuuuls.combatinhibited.util.effectapplier.ProximityEffectController;
import com.mahghuuuls.combatinhibited.util.entityfilter.EntityContext;
import com.mahghuuuls.combatinhibited.util.entityfilter.EntityFilter;
import com.mahghuuuls.combatinhibited.util.entityscanner.EntityScanner;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public final class NearEnemyModule {

    private final EntityScanner scanner;
    private final EntityFilter filter;
    private final ProximityEffectController effectController;

    private final double distanceBlocks;
    private final int scanPeriodTicks;
    private final boolean requireLineOfSight;

    public NearEnemyModule(EntityScanner scanner,
                           EntityFilter filter,
                           ProximityEffectController effectController,
                           double distanceBlocks,
                           int scanPeriodTicks,
                           boolean requireLineOfSight) {

        this.scanner = scanner;
        this.filter = filter;
        this.effectController = effectController;

        this.distanceBlocks = distanceBlocks;
        this.scanPeriodTicks = Math.max(1, scanPeriodTicks);
        this.requireLineOfSight = requireLineOfSight;
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        EntityPlayer player = event.player;
        if (player == null) return;
        if (SideUtil.isRemote(player)) return;

        if (distanceBlocks <= 0) return;
        if ((player.ticksExisted % scanPeriodTicks) != 0) return;

        boolean found = scanner.anyMatch(player, distanceBlocks, (p, e, id) -> {
            if (requireLineOfSight && !p.canEntityBeSeen(e)) return false;

            EntityContext context = new EntityContext(p, e, id);
            return filter == null || filter.passes(context);
        });

        if (!found) {
            effectController.onNoMatch(player);
            return;
        }

        effectController.onMatch(player);
    }
}
