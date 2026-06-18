package com.mahghuuuls.combatinhibited.modules.encounterclear;

import net.minecraftforge.common.config.Config;

public class EncounterClearConfig {

    @Config.Comment("Enable encounter clearing. When a matching entity dies, Inhibited is removed from an affected player only if no other matching entities remain near that player.")
    public boolean isEnabled = true;

    @Config.Comment("Player search radius around the entity that died, in blocks. Players inside this radius are checked for encounter clearing. The player credited with the kill is always checked, even when outside this radius.")
    public double clearTriggerRadiusBlocks = 24.0;

    @Config.Comment("Remaining-entity scan radius around each checked player, in blocks. Inhibited is cleared only when no living entity matching the rules below is found within this radius.")
    public double scanForRemainingRadiusBlocks = 16.0;

    @Config.Comment("If true, only entities the player can currently see can prevent encounter clearing. Hidden entities behind solid blocks are ignored when scanning for remaining hostiles.")
    public boolean requireLineOfSight = true;

    @Config.Comment("Match every living entity. This makes any living entity's death eligible to trigger clearing, and any living entity near the player prevent clearing. Usually leave this false for hostile-only encounters.")
    public boolean includeAll = false;

    @Config.Comment("Match entities implementing IMob, which includes most standard hostile mobs. A matching death can trigger clearing, while a matching entity still nearby prevents it.")
    public boolean includeIMob = true;

    @Config.Comment("Match living entities whose current attack target is any player. This rule is combined with the other include rules using OR.")
    public boolean includeTargetingPlayers = true;

    @Config.Comment("Do not treat players as encounter entities. When true, a player's death cannot trigger clearing and nearby players do not prevent clearing.")
    public boolean excludePlayers = true;

    @Config.Comment("Entity IDs that never count as encounter entities: their deaths do not trigger clearing and their presence does not prevent it. Format: \"modid:entity_name\" (for example, \"minecraft:zombie\").")
    public String[] excludeList = new String[0];

    @Config.Comment("Entity IDs that always count as encounter entities. This overrides all include and exclude rules above, so a listed entity's death can trigger clearing and a listed entity nearby prevents it. Format: \"modid:entity_name\".")
    public String[] allowList = new String[0];
}
