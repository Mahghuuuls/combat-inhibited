# Combat Inhibited

Combat Inhibited applies the **Inhibited** effect while a player is in combat. The effect is provided by the required [Inhibited](https://www.curseforge.com/minecraft/mc-mods/inhibited) mod and prevents affected players from placing or breaking blocks.

Combat is detected and managed by independent, configurable modules. This lets modpack authors decide which entities start combat, keep players in combat, and end an encounter.

## Default Behavior

By default, fighting a hostile mob temporarily prevents you from placing or breaking blocks. The effect lasts 15 seconds, can be refreshed up to three times while a visible enemy remains within 12 blocks, and is removed early when the fight ends with no other visible hostiles nearby. Enemies behind walls do not keep the effect active, and special boss behavior remains disabled unless configured.

## Entity Matching

Modules using the shared entity filter can match living entities through any enabled include rule:

- `includeAll`: match every living entity.
- `includeIMob`: match entities implementing `IMob`, including most standard hostile mobs.
- `includeTargetingPlayers`: match entities currently targeting any player.

The include rules are combined using OR. Minecraft 1.12.2 has no universal way to classify every modded entity as hostile, so registry-name lists are also available:

- `excludeList`: entity IDs that never match that module.
- `allowList`: entity IDs that always match that module and override its include and exclude rules.
- `global_exclude_list`: entries added to the exclude list of every shared-filter module.
- `global_allow_list`: entries added to the allow list of every shared-filter module and given the same override behavior.

Global lists affect Dealing Damage, Taking Damage, Near Enemy, and Encounter Clear. Near Boss uses its own `bossList`. If an ID appears in both an allow list and an exclude list, the allow list wins.

Entity IDs use the registry format `modid:entity_name`, for example `minecraft:zombie`.

## Modules

Every module can be enabled or disabled independently in `config/combatinhibited.cfg`.

### Dealing Damage

Applies Inhibited when a player damages a living entity that matches the module's entity filter.

- Enabled by default.
- Default duration: 300 ticks (15 seconds).
- Damage types can be ignored with `damageTypeBlackList`.
- Players are excluded as targets by default.

### Taking Damage

Applies Inhibited when a player takes damage from a matching attacker.

- Enabled by default.
- Default duration: 300 ticks (15 seconds).
- Non-entity damage such as falling or lava can be enabled or disabled.
- `damageTypeBlackList` ignores configured damage types before entity matching.
- Players are excluded as attackers by default.

### Near Enemy

Scans around each player for entities matching the module's entity filter.

Modes:

- `APPLY_EFFECT`: applies Inhibited whenever a matching entity is nearby.
- `PREVENT_EXPIRING`: only refreshes an existing Inhibited effect when it is close to expiring.

Defaults:

- Enabled with mode `PREVENT_EXPIRING`.
- Scan radius: 12 blocks.
- Scan interval: 20 ticks (1 second).
- Effect duration: 300 ticks (15 seconds).
- Refresh threshold: 40 ticks (2 seconds) remaining.
- `optimizeScanner=true`: checks the last matching entity before performing a full scan.
- `requireLineOfSight=true`: entities behind solid blocks do not match.
- Maximum consecutive reapplications: 3.

If a cached entity dies, unloads, leaves the scan area, becomes hidden, or stops matching the filter, the module immediately falls back to a complete scan.

### Encounter Clear

Removes Inhibited after a matching hostile dies and no other matching hostiles remain near the player.

- Enabled by default.
- Players within 24 blocks of the dead entity are considered.
- The player credited with the kill is always considered, even when attacking from outside that radius.
- Each considered player is scanned for remaining hostiles within 16 blocks.
- `requireLineOfSight=true`: hostiles hidden behind solid blocks do not prevent clearing.
- The dying entity is explicitly ignored during the remaining-hostile scan.

Clearing is evaluated independently for each player. A player keeps Inhibited if another matching hostile remains near them.

### Near Boss

Controls Inhibited while a configured boss is near the player. Boss matching is whitelist-based through `bossList`; there is no automatic boss classification.

It supports the same two behavior modes as Near Enemy: `APPLY_EFFECT` applies Inhibited directly, while `PREVENT_EXPIRING` only refreshes an existing effect near its expiration threshold.

- Disabled by default.
- Default bosses: `minecraft:wither` and `minecraft:ender_dragon`.
- Scan radius: 24 blocks.
- Scan interval: 20 ticks (1 second).
- Default mode: `PREVENT_EXPIRING`.
- Effect duration: 300 ticks (15 seconds).
- Refresh threshold: 40 ticks (2 seconds) remaining.
- Maximum consecutive reapplications: 3.
- `optimizeScanner=true`: checks the last matching boss before performing a full scan.
- `requireLineOfSight=false`: bosses can affect players through walls unless enabled.

Near Enemy and Near Boss have independent scanner and line-of-sight settings.

## Debug Mode

Set `debug_mode=true` to display chat messages when Inhibited is applied. Encounter Clear also reports whether the effect was removed, the dead entity failed its filter, or another nearby entity prevented clearing.
