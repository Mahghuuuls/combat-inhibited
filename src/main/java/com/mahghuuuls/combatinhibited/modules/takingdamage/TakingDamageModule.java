package com.mahghuuuls.combatinhibited.modules.takingdamage;

import com.mahghuuuls.combatinhibited.util.EntityUtils;
import com.mahghuuuls.combatinhibited.util.SideUtil;
import com.mahghuuuls.combatinhibited.util.effectapplier.EffectApplier;
import com.mahghuuuls.combatinhibited.util.entityfilter.EntityContext;
import com.mahghuuuls.combatinhibited.util.entityfilter.EntityFilter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Set;

public final class TakingDamageModule {

    private final EffectApplier applier;
    private final Set<String> blacklistDamageTypes;
    private final EntityFilter entityFilter;
    private final boolean includeNonEntityDamageSources;

    public TakingDamageModule(EffectApplier applier,
                              Set<String> blacklistDamageTypes,
                              EntityFilter entityFilter,
                              boolean includeNonEntityDamageSources) {
        this.applier = applier;
        this.blacklistDamageTypes = blacklistDamageTypes;
        this.entityFilter = entityFilter;
        this.includeNonEntityDamageSources = includeNonEntityDamageSources;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingHurt(LivingHurtEvent event) {
        if (SideUtil.isRemote(event.getEntity())) return;
        if (event.getAmount() <= 0) return;

        Entity victim = event.getEntityLiving();
        if (!(victim instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) victim;

        DamageSource src = event.getSource();
        if (src == null) return;

        String damageType = src.getDamageType();
        if (damageType != null && blacklistDamageTypes != null && blacklistDamageTypes.contains(damageType)) return;

        Entity trueSource = src.getTrueSource();

        // Living attacker
        if (trueSource instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase) trueSource;
            String attackerEntityId = EntityUtils.getEntityId(attacker);

            EntityContext ctx = new EntityContext(player, attacker, attackerEntityId);
            if (entityFilter == null || entityFilter.passes(ctx)) {
                applier.apply(player);
            }
            return;
        }

        // No living attacker
        if (this.includeNonEntityDamageSources) {
            applier.apply(player);
        }
    }
}
