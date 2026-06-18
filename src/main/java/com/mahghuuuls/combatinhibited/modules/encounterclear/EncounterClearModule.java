package com.mahghuuuls.combatinhibited.modules.encounterclear;

import com.mahghuuuls.combatinhibited.util.EntityUtils;
import com.mahghuuuls.combatinhibited.util.SideUtil;
import com.mahghuuuls.combatinhibited.util.entityfilter.EntityContext;
import com.mahghuuuls.combatinhibited.util.entityfilter.EntityFilter;
import com.mahghuuuls.combatinhibited.util.entityscanner.EntityScanner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class EncounterClearModule {

    private final EntityScanner scanner;
    private final EntityFilter entityFilter;
    private final Potion inhibitedPotion;
    private final double clearTriggerRadiusBlocks;
    private final double scanForRemainingRadiusBlocks;
    private final boolean requireLineOfSight;
    private final boolean debugMode;

    public EncounterClearModule(EntityScanner scanner,
                                EntityFilter entityFilter,
                                Potion inhibitedPotion,
                                double clearTriggerRadiusBlocks,
                                double scanForRemainingRadiusBlocks,
                                boolean requireLineOfSight,
                                boolean debugMode) {
        this.scanner = scanner;
        this.entityFilter = entityFilter;
        this.inhibitedPotion = inhibitedPotion;
        this.clearTriggerRadiusBlocks = clearTriggerRadiusBlocks;
        this.scanForRemainingRadiusBlocks = scanForRemainingRadiusBlocks;
        this.requireLineOfSight = requireLineOfSight;
        this.debugMode = debugMode;
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (event == null) return;
        if (!SideUtil.isServer(event.getEntity())) return;

        EntityLivingBase deadEntity = event.getEntityLiving();
        if (deadEntity == null) return;
        if (inhibitedPotion == null) return;
        if (scanner == null) return;
        if (clearTriggerRadiusBlocks <= 0 || scanForRemainingRadiusBlocks <= 0) return;

        World world = deadEntity.world;
        if (world == null) return;

        String deadEntityId = EntityUtils.getEntityId(deadEntity);

        List<EntityPlayer> nearbyPlayers = world.getEntitiesWithinAABB(
                EntityPlayer.class,
                deadEntity.getEntityBoundingBox().grow(clearTriggerRadiusBlocks)
        );

        Set<EntityPlayer> eligiblePlayers = new HashSet<>();
        eligiblePlayers.addAll(nearbyPlayers);

        DamageSource source = event.getSource();
        if (source != null) {
            Entity trueSource = source.getTrueSource();
            if (trueSource instanceof EntityPlayer) {
                eligiblePlayers.add((EntityPlayer) trueSource);
            }
        }

        for (EntityPlayer player : eligiblePlayers) {
            if (player == null) continue;
            if (player.getActivePotionEffect(inhibitedPotion) == null) continue;

            EntityContext deadEntityContext = new EntityContext(player, deadEntity, deadEntityId);
            if (entityFilter != null && !entityFilter.passes(deadEntityContext)) {
                sendDebugMessage(player, "Ignored death of " + deadEntityId + ": it does not match the encounter filter.");
                continue;
            }

            EntityLivingBase[] blockingEntity = new EntityLivingBase[1];
            String[] blockingEntityId = new String[1];
            boolean otherHostilesRemain = scanner.anyMatch(player, scanForRemainingRadiusBlocks, (p, e, id) -> {
                if (e == deadEntity) return false;
                if (requireLineOfSight && !p.canEntityBeSeen(e)) return false;

                EntityContext nearbyEntityContext = new EntityContext(p, e, id);
                boolean matches = entityFilter == null || entityFilter.passes(nearbyEntityContext);
                if (matches) {
                    blockingEntity[0] = e;
                    blockingEntityId[0] = id;
                }
                return matches;
            });

            if (otherHostilesRemain) {
                EntityLivingBase blocker = blockingEntity[0];
                String location = blocker == null
                        ? ""
                        : " at " + blocker.getPosition().getX() + ", "
                        + blocker.getPosition().getY() + ", " + blocker.getPosition().getZ();
                sendDebugMessage(player, "Kept Inhibited: " + blockingEntityId[0] + location + " still matches the encounter filter.");
                continue;
            }

            player.removePotionEffect(inhibitedPotion);
            sendDebugMessage(player, "Cleared Inhibited after the death of " + deadEntityId + ".");
        }
    }

    private void sendDebugMessage(EntityPlayer player, String message) {
        if (!debugMode || player == null || message == null) return;
        player.sendMessage(new TextComponentString("[CombatInhibited] Encounter Clear: " + message));
    }
}
