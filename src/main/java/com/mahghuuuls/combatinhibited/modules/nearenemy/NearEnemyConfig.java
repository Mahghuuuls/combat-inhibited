package com.mahghuuuls.combatinhibited.modules.nearenemy;

import net.minecraftforge.common.config.Config;

public class NearEnemyConfig {

    @Config.Comment("Enable this module.")
    public boolean isEnabled = true;

    @Config.Comment("Behavior mode: APPLY_EFFECT or PREVENT_EXPIRING.")
    public Mode mode = Mode.PREVENT_EXPIRING;

    @Config.Comment("Scan radius in blocks.")
    public double distanceBlocks = 12.0;

    @Config.Comment("How often to scan (ticks). 20 ticks = 1 second.")
    public int scanPeriodTicks = 20;

    @Config.Comment("If true, check the last matching entity for each player before running a full nearby-entity scan. Disable this to always use the standard scanner.")
    public boolean optimizeScanner = true;

    @Config.Comment("If true, only entities the player can currently see can apply or refresh Inhibited. Solid blocks between the player's eyes and the entity prevent a match.")
    public boolean requireLineOfSight = true;

    @Config.Comment("Duration in ticks applied/refreshed by this module.")
    public int durationTicks = 300;

    @Config.Comment("PREVENT_EXPIRING only: only refresh if remaining duration is <= this value (ticks).")
    public int refreshWhenRemainingAtMostTicks = 40;

    @Config.Comment("Maximum number of consecutive reapplications by this module while the player stays in range. -1 = unlimited.")
    public int maxReapplications = 2;

    @Config.Comment("Include rule: treat any nearby living entity as matching.")
    public boolean includeAll = false;

    @Config.Comment("Include rule: treat IMob entities as matching (most hostile mobs).")
    public boolean includeIMob = true;

    @Config.Comment("Include rule: treat entities targeting any player as matching (attack target is a player).")
    public boolean includeTargetingPlayers = true;

    @Config.Comment("Exclude players as nearby entities.")
    public boolean excludePlayers = true;

    @Config.Comment("Entity IDs that NEVER match, regardless of other rules. Format: \"modid:entity_name\".")
    public String[] excludeList = new String[0];

    @Config.Comment("Entity IDs that ALWAYS match, overriding include/exclude rules. Format: \"modid:entity_name\".")
    public String[] allowList = new String[0];
}
