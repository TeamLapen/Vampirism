---
sidebar_position: 2
title: Portal Activation
---

The way to [Velmorra](./velmorra) is a **Velmorra Portal**. It is closed by default and has to be opened with a ritual at the **Velmorra Altar** next to it.

:::info Who can do this
Only players that are **lords of the highest lord level** of their faction (vampire lord level 5 or hunter lord level 5) can use the altar. Everybody else is told that their blood is not strong enough. Once the portal is open, anybody can walk through it.
:::

## Finding a portal

The portal is a structure that generates in the overworld. It consists of an arched frame made from Dark Stone Bricks and *Velmorra Portal Arch* blocks, with a Velmorra Altar next to it. It is rare: portals are at least 100 and on average about 125 chunks apart.

A lord that drinks **Pure Blood** receives the effect **Whispers of The Veil** for a few seconds (higher purity lasts longer). It spawns particles around you that drift towards the nearest Velmorra Portal.

The altar is only found in the portal structure. If you break it, it drops as an item, and the portal it opened closes.

## Ritual Knives

The ritual needs three different knives, one for each vanilla dimension. All three are a *Ritual Knife* item with a different type.

First craft the base **Ritual Knife** (iron ingot above a stick). Then combine it with the ingredients below to give it a type:

| Knife | Ingredients (besides the Ritual Knife) | Meaning |
|-------|------|---------|
| **Knife of Seeking** | Recovery Compass, Sculk Shrieker, 4 Amethyst Shards | Finds the way to Velmorra |
| **Knife of Passage** | Dragon's Breath, End Stone, 4 Ender Pearls | Tears a passage through the void |
| **Knife of Anchoring** | Wither Rose, Lodestone, 4 Ghast Tears | Anchors the portal in place |

A Sculk Shrieker can only be obtained with Silk Touch, a Lodestone needs a Netherite Ingot and a Wither Rose requires a Wither kill, so the knives take a trip through all three dimensions. All recipes are data-driven and can be changed with a data pack.

## Performing the ritual

Stand at the altar, which has to be within 10 blocks of the portal frame.

1. **Insert the knives.** Right-click the altar with a knife. Every knife costs you blood: you take damage with each insertion (6 points, i.e. 3 hearts, by default; configurable as `velmorraAltarKnifeDamage` in the balance config). It never kills you and always leaves you at least one heart, and it is skipped in creative mode. If you are too weak you are told to come back with more health. A knife type can only be inserted once.
2. **Activate.** Right-click the altar with an **empty hand**. If a knife is missing, the message lists which. If there is no complete portal frame nearby, you are told so.
3. **Wait.** The knives glow and particles rise from the altar for 3 seconds. Then the portal blocks fill the frame.

The altar shows how many knives it holds, and after activation the knives sit fully inserted.

### While the portal is open

- The knives are locked and cannot be removed.
- The portal leads to Velmorra. The game remembers which portal you entered from.
- The portal closes when Velmorra is destroyed (see [Velmorra](./velmorra#collapse)). The altar then raises the knives again.

### Reopening

The knives are **not consumed**. After the portal closed, right-click the altar with an empty hand again to open it again, without any new items. Opening it creates a brand new Velmorra: the castle is intact again and Dracula is alive.

### Taking the knives back

While the portal is closed, sneak and right-click with an empty hand to take back the last inserted knife (one per click). Breaking the altar drops all knives.
