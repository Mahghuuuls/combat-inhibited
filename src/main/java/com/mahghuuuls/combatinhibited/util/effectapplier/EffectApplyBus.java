package com.mahghuuuls.combatinhibited.util.effectapplier;

import com.mahghuuuls.combatinhibited.util.reapplicationlimiter.ApplicationSource;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;

public class EffectApplyBus {

    private static final List<EffectApplyListener> listeners = new ArrayList<>();

    private EffectApplyBus() {}

    public static void register(EffectApplyListener listener) {
        if (listener != null) listeners.add(listener);
    }

    public static void notifyApplied(EntityPlayer player, ApplicationSource source) {
        for (EffectApplyListener listener : listeners) {
            listener.onApplied(player, source);
        }
    }
}
