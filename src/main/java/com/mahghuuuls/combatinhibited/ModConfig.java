package com.mahghuuuls.combatinhibited;

import com.mahghuuuls.combatinhibited.modules.dealingdamage.DealingDamageConfig;
import com.mahghuuuls.combatinhibited.modules.encounterclear.EncounterClearConfig;
import com.mahghuuuls.combatinhibited.modules.nearboss.NearBossConfig;
import com.mahghuuuls.combatinhibited.modules.nearenemy.NearEnemyConfig;
import com.mahghuuuls.combatinhibited.modules.takingdamage.TakingDamageConfig;
import net.minecraftforge.common.config.Config;

@Config(modid = CombatInhibited.MOD_ID)
public final class ModConfig {

    @Config.Name("dealing_damage")
    @Config.Comment("Applies Inhibited when the player deals damage.")
    public static final DealingDamageConfig dealingDamageConfig = new DealingDamageConfig();

    @Config.Name("taking_damage")
    @Config.Comment("Applies Inhibited when the player takes damage.")
    public static final TakingDamageConfig takingDamageConfig = new TakingDamageConfig();

    @Config.Name("near_boss")
    @Config.Comment("Applies Inhibited while the player is within range of a configured boss entity.")
    public static final NearBossConfig nearBossConfig = new NearBossConfig();

    @Config.Name("near_enemy")
    @Config.Comment("Applies Inhibited while the player is within range of a configured enemy entity.")
    public static final NearEnemyConfig nearEnemyConfig = new NearEnemyConfig();

    @Config.Name("encounter_clear")
    @Config.Comment("Clears Inhibited after a matching encounter entity dies and no other matching entities remain near the player.")
    public static final EncounterClearConfig encounterClearConfig = new EncounterClearConfig();

    @Config.Name("debug_mode")
    @Config.Comment("If true, reports when Inhibited is applied and explains encounter-clear decisions in chat.")
    public static boolean debugMode = false;
}
