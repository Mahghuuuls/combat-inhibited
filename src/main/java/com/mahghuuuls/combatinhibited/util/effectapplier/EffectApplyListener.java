package com.mahghuuuls.combatinhibited.util.effectapplier;

import com.mahghuuuls.combatinhibited.util.reapplicationlimiter.ApplicationSource;
import net.minecraft.entity.player.EntityPlayer;

public interface EffectApplyListener {
    void onApplied(EntityPlayer player, ApplicationSource source);
}
