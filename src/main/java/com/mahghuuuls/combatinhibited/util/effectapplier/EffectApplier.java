package com.mahghuuuls.combatinhibited.util.effectapplier;

import com.mahghuuuls.combatinhibited.util.reapplicationlimiter.ApplicationSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextComponentString;

public class EffectApplier {

    private final EffectConfig effectConfig;
    private final ApplicationSource applicationSource;
    private final boolean debugMode;

    public EffectApplier(EffectConfig effectConfig,
                         ApplicationSource applicationSource,
                         boolean debugMode) {
        this.effectConfig = effectConfig;
        this.applicationSource = applicationSource;
        this.debugMode = debugMode;
    }

    public void apply(EntityPlayer player) {

        if (player == null) return;
        if (effectConfig == null) return;
        if (effectConfig.getPotion() == null) return;

        Potion potion = effectConfig.getPotion();
        int durationTicks = effectConfig.getDurationTicks();
        int amplifier = effectConfig.getAmplifier();
        boolean showParticles = effectConfig.isShowParticles();

        if (effectConfig.getDurationTicks() <= 0) return;

        PotionEffect effect = new PotionEffect(
                potion,
                durationTicks,
                amplifier,
                false,
                showParticles
        );

        player.addPotionEffect(effect);

        // Debug message
        if (debugMode && !player.world.isRemote) {
            sendDebugMessage(player, durationTicks);
        }

        EffectApplyBus.notifyApplied(player, applicationSource);
    }

    private void sendDebugMessage(EntityPlayer player, int durationTicks){
        String source = (applicationSource == null ? "UNKNOWN" : applicationSource.name());
        int seconds = durationTicks / 20;

        player.sendMessage(new TextComponentString(
                "[CombatInhibited] Applied Inhibited for " + durationTicks + "t (" + seconds + "s), source=" + source
        ));
    }
}
