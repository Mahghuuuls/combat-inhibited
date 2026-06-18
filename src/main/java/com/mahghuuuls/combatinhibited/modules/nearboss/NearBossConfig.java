package com.mahghuuuls.combatinhibited.modules.nearboss;

import com.mahghuuuls.combatinhibited.modules.ProximityMode;
import net.minecraftforge.common.config.Config;

public class NearBossConfig {

    @Config.Comment("Enable this module.")
    public boolean isEnabled = false;

    @Config.Comment("Scan radius in blocks.")
    public double distanceBlocks = 24.0;

    @Config.Comment("How often to scan (ticks). 20 ticks = 1 second.")
    public int scanPeriodTicks = 20;

    @Config.Comment("If true, check the last matching boss for each player before running a full nearby-entity scan. Disable this to always use the standard scanner.")
    public boolean optimizeScanner = true;

    @Config.Comment("If true, only bosses the player can currently see can apply or refresh Inhibited. Solid blocks between the player's eyes and the boss prevent a match.")
    public boolean requireLineOfSight = false;

    @Config.Comment("Behavior mode: APPLY_EFFECT or PREVENT_EXPIRING.")
    public ProximityMode mode = ProximityMode.PREVENT_EXPIRING;

    @Config.Comment("Duration in ticks applied/refreshed by this module.")
    public int durationTicks = 300;

    @Config.Comment("PREVENT_EXPIRING only: only refresh if remaining duration is <= this value (ticks).")
    public int refreshWhenRemainingAtMostTicks = 40;

    @Config.Comment("Maximum number of consecutive applications by this module while the player stays in range. -1 = unlimited.")
    public int maxReapplications = 3;

    @Config.Comment("Boss entity IDs (whitelist). Only these entities are treated as bosses. Format: \"modid:entity_name\".")
    public String[] bossList = new String[] {
            "minecraft:wither",
            "minecraft:ender_dragon"
    };
}
