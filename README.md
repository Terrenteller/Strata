## What is Strata?

Strata is a Forge-based, rock and ore generation mod for Minecraft with a focus on providing new content via plain-text configuration files of minimal complexity.

## What makes Strata special?

_Flexibility_ and **integration**.

- Strata is not a world generation mod. Ideally, Strata is compatible with all of them!
- Plain-text configuration files allow anyone to create their own Strata-powered content without a line of code!
- Tilesets make it easy to define entire suites of stone blocks and their derivatives. Just add textures!
- Strata's two-in-one ore-in-host-rock system welcomes mod content cross-pollination and texture packs!
- Ores take on the appearance and properties of their host and drop both when mined!
- Strata will replicate existing recipes using its own content!
- Ores support the same plants as their hosts! (technical exceptions withstanding)
- Strata's redstone ore acts like vanilla redstone ore! (yes, this is important)

## How do I use Strata?

Strata will have little to no gameplay effect outside of creative mode without a world generation mod to bring it to life (as much as rocks can have). Currently, the only option is [CustomOreGen](https://github.com/lawremi/CustomOreGen).

## How do I use Strata to create my own content?

Strata will unpack the following on start (if missing):
- `docs/Strata.txt` contains tiledata keyvalue documentation for the current version.
- `config/strata/tiledata` contains block/item configuration files. Use these as a starting point for any new blocks you wish to add. You are encouraged to put new configuration files in a subdirectory named after the mod/modpack/etc. for organization. The client can know about things the server doesn't, but **the client must know about everything the server does**.
- `config/strata/recipe/replication` contains recipe replication configuration files.

## "Why" is Strata?

Once upon a time, there was a mod called [PerFabricaAdAstra](https://github.com/lawremi/PerFabricaAdAstra) that fell by the wayside. Disappointed with the thought of what might never be, I took it upon myself to re-imagine the "Geologica" part of the PFAA triad, leaving Chemica and Fabrica to other tech mods like Mekanism and Immersive Engineering.

## Legal stuff

Strata is licenced under [LGPL 3.0](LICENCE.md) unless where otherwise stated.

Original and remixed [PerFabricaAdAstra](https://github.com/lawremi/PerFabricaAdAstra) assets were, and are still, licenced under [The Artistic License 2.0](artistic-v2.0.md).

- Changes (stated as required) include, but may not be limited to:
    - Resizing to 16x16
    - Tiling/edge resynthesis
    - Transparency adjustments
    - Recolouring of some or all of the image

A few images (ore texture cutouts, grayscale textures for procedural asset creation, etc.) constitute derivative works of Minecraft, owned by Mojang, a subsidiary of Microsoft. These derivatives are used without explicit permission or ill intent, and remain property of Mojang. Redistribution of this content is understood to be acceptable per Mojang's 'EXTENDED FUNCTIONALITY AND “MODS”' subsection of their [Commercial Usage Guidelines](https://account.mojang.com/terms?ref=ft#commercial). NOT AN OFFICIAL MINECRAFT PRODUCT. NOT APPROVED BY OR ASSOCIATED WITH MOJANG.