package com.mahghuuuls.combatinhibited;

import com.mahghuuuls.combatinhibited.modules.dealingdamage.DealingDamageConfig;
import com.mahghuuuls.combatinhibited.modules.dealingdamage.DealingDamageModule;
import com.mahghuuuls.combatinhibited.modules.encounterclear.EncounterClearConfig;
import com.mahghuuuls.combatinhibited.modules.encounterclear.EncounterClearModule;
import com.mahghuuuls.combatinhibited.modules.nearboss.NearBossConfig;
import com.mahghuuuls.combatinhibited.modules.nearboss.NearBossModule;
import com.mahghuuuls.combatinhibited.modules.nearenemy.NearEnemyConfig;
import com.mahghuuuls.combatinhibited.modules.nearenemy.NearEnemyModule;
import com.mahghuuuls.combatinhibited.modules.takingdamage.TakingDamageConfig;
import com.mahghuuuls.combatinhibited.modules.takingdamage.TakingDamageModule;
import com.mahghuuuls.combatinhibited.util.reaplicationlimiter.ApplicationSource;
import com.mahghuuuls.combatinhibited.util.effectapplier.EffectApplier;
import com.mahghuuuls.combatinhibited.util.effectapplier.EffectConfig;
import com.mahghuuuls.combatinhibited.util.entityfilter.EntityFilter;
import com.mahghuuuls.combatinhibited.util.entityfilter.entityconditions.IsNotPlayerCondition;
import com.mahghuuuls.combatinhibited.util.entityfilter.entityconditions.IsNotExcludedCondition;
import com.mahghuuuls.combatinhibited.util.entityfilter.entityconditions.IsHostileCondition;
import com.mahghuuuls.combatinhibited.util.entityscanner.EntityScanner;
import com.mahghuuuls.combatinhibited.util.entityscanner.NearbyEntityScanner;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION, dependencies = CombatInhibited.DEPENDENCIES)
public class CombatInhibited {
    public static final String MOD_ID = Tags.MOD_ID;
    public static final String NAME = Tags.MOD_NAME;
    public static final String VERSION = Tags.VERSION;
    public static final String DEPENDENCIES = "required-after:inhibited";

    @EventHandler
    public void init(FMLInitializationEvent event) {

        Potion inhibitedPotion = ForgeRegistries.POTIONS.getValue(new ResourceLocation("inhibited", "inhibited"));
        if (inhibitedPotion == null) {
            throw new RuntimeException("Inhibited potion reference is null.");
        }

        int amplifier = 0;
        boolean showParticles = false;

        // Dealing Damage (DD) Module
        DealingDamageConfig DDConfig = ModConfig.dealingDamageConfig;
        if (DDConfig.isEnabled) {
            EffectConfig DDEffectCfg = new EffectConfig(inhibitedPotion, DDConfig.durationTicks, amplifier, showParticles);
            EffectApplier DDApplier = new EffectApplier(DDEffectCfg, ApplicationSource.DEALING_DAMAGE, ModConfig.debugMode);

            Set<String> blackListDamageTypes = new HashSet<>(Arrays.asList(DDConfig.damageTypeBlackList));

            EntityFilter DDEntityFilter = buildFilter(
                    DDConfig.includeAll,
                    DDConfig.includeIMob,
                    DDConfig.includeTargetingPlayers,
                    DDConfig.excludePlayers,
                    DDConfig.excludeList,
                    DDConfig.allowList
            );

            DealingDamageModule DDModule = new DealingDamageModule(DDApplier, blackListDamageTypes, DDEntityFilter);
            MinecraftForge.EVENT_BUS.register(DDModule);
        }

        // Taking Damage (TD) Module
        TakingDamageConfig TDConfig = ModConfig.takingDamageConfig;
        if (TDConfig.isEnabled) {
            EffectConfig TDEffectCfg = new EffectConfig(inhibitedPotion, TDConfig.durationTicks, amplifier, showParticles);
            EffectApplier TDApplier = new EffectApplier(TDEffectCfg, ApplicationSource.TAKING_DAMAGE, ModConfig.debugMode);

            Set<String> blackListDamageTypes = new HashSet<>(Arrays.asList(TDConfig.damageTypeBlackList));

            EntityFilter TDEntityFilter = buildFilter(
                    TDConfig.includeAll,
                    TDConfig.includeIMob,
                    TDConfig.includeTargetingPlayers,
                    TDConfig.excludePlayers,
                    TDConfig.excludeList,
                    TDConfig.allowList
            );

            TakingDamageModule TDModule = new TakingDamageModule(
                    TDApplier,
                    blackListDamageTypes,
                    TDEntityFilter,
                    TDConfig.includeNonEntityDamageSources
            );
            MinecraftForge.EVENT_BUS.register(TDModule);
        }

        // Near Enemy (NE) Module
        NearEnemyConfig NEConfig = ModConfig.nearEnemyConfig;
        if (NEConfig.isEnabled) {

            EffectConfig NEEffectCfg = new EffectConfig(inhibitedPotion, NEConfig.durationTicks, amplifier, showParticles);
            EffectApplier NEApplier = new EffectApplier(NEEffectCfg, ApplicationSource.NEAR_ENEMY, ModConfig.debugMode);

            EntityFilter NEEntityFilter = buildFilter(
                    NEConfig.includeAll,
                    NEConfig.includeIMob,
                    NEConfig.includeTargetingPlayers,
                    NEConfig.excludePlayers,
                    NEConfig.excludeList,
                    NEConfig.allowList
            );

            EntityScanner scanner = new NearbyEntityScanner();

            NearEnemyModule NEModule = new NearEnemyModule(
                    scanner,
                    NEEntityFilter,
                    NEApplier,
                    inhibitedPotion,
                    NEConfig.distanceBlocks,
                    Math.max(1, NEConfig.scanPeriodTicks),
                    NEConfig.mode,
                    NEConfig.refreshWhenRemainingAtMostTicks,
                    NEConfig.maxReapplications
            );

            MinecraftForge.EVENT_BUS.register(NEModule);
        }

        // Encounter Clear (EC) Module
        EncounterClearConfig ECConfig = ModConfig.encounterClearConfig;
        if (ECConfig.isEnabled) {

            EntityFilter ECEntityFilter = buildFilter(
                    ECConfig.includeAll,
                    ECConfig.includeIMob,
                    ECConfig.includeTargetingPlayers,
                    ECConfig.excludePlayers,
                    ECConfig.excludeList,
                    ECConfig.allowList
            );

            EntityScanner scanner = new NearbyEntityScanner();

            EncounterClearModule ECModule = new EncounterClearModule(
                    scanner,
                    ECEntityFilter,
                    inhibitedPotion,
                    ECConfig.clearTriggerRadiusBlocks,
                    ECConfig.scanForRemainingRadiusBlocks,
                    ModConfig.debugMode
            );

            MinecraftForge.EVENT_BUS.register(ECModule);
        }

        // Near Boss (NB) Module
        NearBossConfig NBConfig = ModConfig.nearBossConfig;
        if (NBConfig.isEnabled) {

            EffectConfig NBEffectCfg = new EffectConfig(inhibitedPotion, NBConfig.durationTicks, amplifier, showParticles);
            EffectApplier NBApplier = new EffectApplier(NBEffectCfg, ApplicationSource.NEAR_BOSS, ModConfig.debugMode);

            Set<String> bossList = new HashSet<>(Arrays.asList(NBConfig.bossList));

            EntityScanner scanner = new NearbyEntityScanner();

            NearBossModule NBModule = new NearBossModule(
                    scanner,
                    NBApplier,
                    bossList,
                    NBConfig.distanceBlocks,
                    Math.max(1, NBConfig.scanPeriodTicks)
            );

            MinecraftForge.EVENT_BUS.register(NBModule);
        }
    }

    private static EntityFilter buildFilter(boolean includeAll,
                                            boolean includeIMob,
                                            boolean includeTargetingPlayers,
                                            boolean excludePlayers,
                                            String[] excludeList,
                                            String[] allowList) {

        EntityFilter filter = new EntityFilter();

        if (includeAll || includeIMob || includeTargetingPlayers) {
            filter.addCondition(new IsHostileCondition(includeAll, includeIMob, includeTargetingPlayers));
        }

        if (excludePlayers) {
            filter.addCondition(new IsNotPlayerCondition());
        }

        if (excludeList != null && excludeList.length > 0) {
            Set<String> exclude = new HashSet<>(Arrays.asList(excludeList));
            filter.addCondition(new IsNotExcludedCondition(exclude));
        }

        if (allowList != null && allowList.length > 0) {
            filter.setAllowListOverride(new HashSet<>(Arrays.asList(allowList)));
        }

        return filter;
    }
}
