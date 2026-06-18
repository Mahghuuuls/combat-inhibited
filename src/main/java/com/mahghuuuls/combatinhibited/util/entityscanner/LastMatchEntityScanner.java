package com.mahghuuuls.combatinhibited.util.entityscanner;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

public final class LastMatchEntityScanner implements EntityScanner {

    private final EntityScanner delegate;
    private final Map<EntityPlayer, WeakReference<EntityLivingBase>> lastMatches = new WeakHashMap<>();

    public LastMatchEntityScanner(EntityScanner delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean anyMatch(EntityPlayer player, double distanceBlocks, EntityMatch match) {
        if (player == null || match == null || distanceBlocks <= 0) return false;

        EntityLivingBase cachedEntity = getCachedEntity(player);
        if (isValidCachedEntity(player, cachedEntity, distanceBlocks)) {
            ResourceLocation key = EntityList.getKey(cachedEntity);
            if (key != null && match.matches(player, cachedEntity, key.toString())) {
                return true;
            }
        }

        lastMatches.remove(player);
        if (delegate == null) return false;

        EntityLivingBase[] foundEntity = new EntityLivingBase[1];
        boolean found = delegate.anyMatch(player, distanceBlocks, (p, entity, entityId) -> {
            if (!match.matches(p, entity, entityId)) return false;
            foundEntity[0] = entity;
            return true;
        });

        if (found && foundEntity[0] != null) {
            lastMatches.put(player, new WeakReference<>(foundEntity[0]));
        }

        return found;
    }

    private EntityLivingBase getCachedEntity(EntityPlayer player) {
        WeakReference<EntityLivingBase> reference = lastMatches.get(player);
        return reference == null ? null : reference.get();
    }

    private boolean isValidCachedEntity(EntityPlayer player,
                                        EntityLivingBase entity,
                                        double distanceBlocks) {
        if (entity == null || entity == player || entity.isDead || !entity.isEntityAlive()) return false;
        if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isSpectator()) return false;

        World world = player.world;
        if (world == null || entity.world != world) return false;
        if (world.getEntityByID(entity.getEntityId()) != entity) return false;

        return player.getEntityBoundingBox()
                .grow(distanceBlocks)
                .intersects(entity.getEntityBoundingBox());
    }
}
