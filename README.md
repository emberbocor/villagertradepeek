# Villager Trade Peek

See every trade a villager will ever offer before you commit to it.

Villager Trade Peek shows all trades a villager unlocks at future levels, up to Master, right in the vanilla trading screen. Locked trades are drawn greyed out below the trades that are already available, and hovering one tells you which level unlocks it.

This is the Minecraft 1.21.11 version, available for NeoForge, Forge and Fabric. The Minecraft 1.21.1 version lives on the `master` branch.

![Scrolling through locked trades in the trading screen](docs/preview.png)

## Features

- **Full preview up to Master.** Every future trade is listed below the current ones, in level order, and the list scrolls like the vanilla one.
- **What you see is what you get.** Future trades are rolled once, using the vanilla trade pool and vanilla odds, and stored on the villager. When it levels up, it receives exactly the trades you previewed.
- **Locked means locked.** Locked trades are never part of the villager's real offers, so they cannot be selected or used, not even by a modified client.
- **Unlock level tooltip.** Hover a locked trade to see the item tooltip (for example the enchantment on a book) and the level that unlocks it.
- **Your prices.** Locked trades show the price you would pay, including reputation and Hero of the Village discounts, drawn the same way vanilla shows discounts.
- **Rerolling works as expected.** Breaking and replacing a job site block rerolls all future levels along with the level 1 trades. Mods that reroll trades while the trading screen is open update the preview immediately.
- **Modded trades included.** Trades added by other mods through the standard villager trade events, and enchantments added to the tradeable enchantment tag, show up in the preview automatically. Supports the Villager Trade Rebalance experiment.
- **Cartographer friendly.** Explorer maps are only created when the villager reaches the level that unlocks them, so rerolling a cartographer stays smooth and does not use up nearby structures.
- **Survives zombification.** A villager that is turned into a zombie villager and cured keeps its future trades.
- **Existing worlds.** Villagers that existed before the mod was installed get their remaining levels generated the first time you open their trading screen.
- **Safe to remove.** Without the mod, villagers simply fall back to vanilla behavior. No trades are leaked or unlocked early.

## Requirements

| | NeoForge | Forge | Fabric |
|---|---|---|---|
| Minecraft | 1.21.11 | 1.21.11 | 1.21.11 |
| Loader | NeoForge 21.11.42 or newer | Forge 61.0.1 or newer | Fabric Loader 0.17.3 or newer |
| Other | | | Fabric API 0.139.5 or newer |
| Jar | `villagertradepeek-neoforge-1.21.11-<version>.jar` | `villagertradepeek-forge-1.21.11-<version>.jar` | `villagertradepeek-fabric-1.21.11-<version>.jar` |

NeoForge and Forge are separate loaders that do not run each other's mods, so each has its own jar. Pick the jar that matches your loader.

No configuration needed.

### Client and server

Install the mod on both the client and the server.

- **NeoForge and Forge:** the mod is required on both sides. Players without it cannot join a server that has it; the connection is refused at login.
- **Fabric:** install it on both sides to see the preview. Players without the mod can still join a server that has it and trade normally, they just do not see locked trades. A client with the mod on a server without it also works, without the preview.

## Commands

- `/villagertradepeek future <villager>` (operators only): lists the future trades stored on a villager. Useful when reporting bugs.

## Compatibility notes

Mods that add new trades or tradeable items to villagers work out of the box, because the preview is rolled from the same trade pool vanilla uses. Their trades show up in the preview and are delivered exactly as previewed when the villager levels up.

Mods that change how many trades a villager gets per level, or that replace the villager trade logic entirely, may produce trades that differ from the preview.

## Upgrading a world from Minecraft 1.21.1

Item data changed between Minecraft 1.21.1 and 1.21.11, so future trades stored by the 1.21.1 version of the mod are not reused. When a world is upgraded:

- Trades a villager has already unlocked are kept, like in vanilla.
- The stored preview is discarded and the remaining levels are rolled again the first time you open the villager's trading screen, so they may differ from what you saw on 1.21.1.
- Nothing is unlocked early and no trade is carried over with wrong prices or items.

## Building from source

```
./gradlew build
```

The jars are written to `neoforge/build/libs`, `forge/build/libs` and `fabric/build/libs`. The project follows the [MultiLoader-Template](https://github.com/jaredlll08/MultiLoader-Template) layout: shared code lives in `common`, loader-specific code in `neoforge`, `forge` and `fabric`.

## License

MIT. See [LICENSE](LICENSE).
