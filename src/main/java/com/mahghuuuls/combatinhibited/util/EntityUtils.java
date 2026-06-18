package com.mahghuuuls.combatinhibited.util;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class EntityUtils {

    private static final ResourceLocation PLAYER_ID = new ResourceLocation("minecraft", "player");

    public static ResourceLocation getEntityKey(EntityLivingBase entity){
        if (entity == null) return null;

        ResourceLocation key = EntityList.getKey(entity);
        if (key == null && entity instanceof EntityPlayer) {
            return PLAYER_ID;
        }

        return key;
    }

    public static String getEntityId(EntityLivingBase entity){
        ResourceLocation entityKey = getEntityKey(entity);
        return entityKey == null ? null : entityKey.toString();
    }
}
