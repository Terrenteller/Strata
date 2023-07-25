
## Prologue

Once upon a time, there was a mod named [PerFabricaAdAstra](https://github.com/lawremi/PerFabricaAdAstra) that fell by the wayside. Disappointed with the thought of a revitalized and colourful underground that might never be, I took it upon myself to re-imagine the "Geologica" part of the PFAA triad, leaving Chemica and Fabrica to established tech mods like Mekanism and Immersive Engineering.

As you may have anticipated, the result is Strata!

## What is Strata?

Strata is a Forge-based geological block generation and ore compatibility mod for Minecraft and spiritual successor to PFAA Geologica. It provides a platform for new content through resource packs which contain non-standard plain-text configuration files of minimal complexity. Strata's primary directive is to shape a consistent gameplay experience across the blocks it creates and the blocks it does not create but may represent. This is a fancy way to say Strata makes everyone's rocks and ores play nicely together.

Want to see what Strata is in pictures? Check [the gallery](https://github.com/Terrenteller/Strata/wiki/Gallery)!

## What can Strata do?

**Strata can create almost any generic geological block and its entourage.** From stone, sand, gravel, and dirt to walls, stairs, buttons, and levers, the options are many. Clay can break into fragments like vanilla and be put right back together. Stone and cobblestone can break into fragments like TerraFirmaCraft. Stone fragments can be thrown like a snowball after being drawn like a bow and be crafted into cobblestone which can be smelted and crafted further. A Strata block that converts to another can be used as the other block in most recipes without fuss and be crafted directly into the other block for every other situation.

**Strata can create highly-adaptive ores.** If another mod's ores are insufficient or something special is required, Strata ores are overachievers. Not only do they have robust drop options, they also have the concept of a "host" (like "host rock" but may not be rock). This allows Strata ores to look and behave as if they are in another block without requiring a block to be made for every ore/host combination. The host is normally determined when the ore is placed into the world based on simple heuristics using the adjacent blocks. For example, if sand is a host and an ore is placed in a body of sand, the ore will adapt to look like it's in sand, sound like it's in sand, and break like it's in sand - no tool required even if the ore normally requires one. Non-Strata blocks can be deemed as hosts as well for maximum compatibility.

**Strata can mimic almost any other ore.** These ores are called "proxy" ores and defer as much to the original ore as possible. Light emission, tooltip localization, client-side random tick passive particle effects, and harvest behaviour (think drops in a more general sense) are several such things proxy ores figure out on the fly. Proxy ores sound redundant, but are key to blending hosts and ores from other mods.

**Strata can give you some things for free.** Most notably, Geolosys-like "samples" are created automatically where appropriate. Ore samples can only be broken and picked up to prevent re-rolling, but other samples can be placed back down for decoration using the fragment item.

## What makes Strata special?

_Flexibility_ and **compatibility**.

- Strata is not a world generation mod. Ideally, Strata is compatible with all of them!
- Plain-text configuration files allow anyone to create custom content without a line of code!
- Tile sets make it easy to define entire suites of stone blocks and their derivatives. Just add textures!
- Ores take on the appearance and properties of their host and drop both when harvested!
- Strata's two-in-one ore-in-host system welcomes mod content cross-pollination and texture packs!
- Strata's redstone ore acts like vanilla redstone ore! (yes, this is important)
- Strata will load tile data from resource packs on both client and server. One ZIP to rule them all!
- Strata will replicate standard crafting recipes using new content!
- Strata has no hard dependency on any other mod or modding library! (sans Forge, of course)

## How do I use Strata?

Given Strata's primary directive, actual content and its application are secondary, and two-part:

#### 1. Resource packs

Did you know resource packs can carry any payload? They're just a collection of files, after all. Strata utilizes this "feature" to neatly bundle tile data with its assets for easy distribution of synchronized content. Featured resource packs are managed separately from the code in the [Strata-Resource-Packs](https://github.com/Terrenteller/Strata-Resource-Packs) repository.

For step-by-step instructions on creating new content, consult the "Your First" tutorial series on [the wiki](https://github.com/Terrenteller/Strata/wiki).

#### 2. World generation

Strata will have little to no effect on gameplay outside of creative mode without a world generation mod to bring it to life (as much as rocks can have). The premier (and currently only) option is [CustomOreGen](https://github.com/lawremi/CustomOreGen). Options provided by compatible resource packs are found in the "Custom Ore Generation" GUI when creating a new world.

_NOTE: COG has a long-standing bug of replacing `emerald_ore` with `monster_egg` [here](https://github.com/lawremi/CustomOreGen/blob/db939431ccab85707754c69c6d54858d1fcdf9ff/src/main/resources/config/modules/Vanilla.xml#L2059). You will likely want to turn off COG's handling of vanilla ore generation in the "Vanilla" tab if you do not fix this yourself._

## How do I build Strata?

If configuration files are not enough, please refer to Forge documentation for the version in `build.gradle` to prepare a development environment.

**Arch Linux**

1. Install `jdk8-openjdk`
2. Adjust the symlinks in `/usr/lib/jvm/` or edit the run configuration

## Legal stuff

Strata is licenced under [LGPL 3.0](LICENCE.md) unless where otherwise stated. Markdown-formatted licences are provided by [IQAndreas/markdown-licenses](https://github.com/IQAndreas/markdown-licenses).

Original and remixed [Geolosys](https://github.com/oitsjustjose/Geolosys) assets were, and still are, licensed under [GPL 3.0](gnu-gpl-v3.0.md).

- Changes include:
    - Organized, simplified, and duplicated ore sample model geometry
    - Synthesized a new ore cluster base image by removing differences from originals

Original and remixed [TerraFirmaCraft](https://github.com/TerraFirmaCraft/TerraFirmaCraft) assets were, and still are, licensed under [EUPL 1.2](EUPL-1.2 EN.txt).

- Changes include:
    - Reverse-engineered rock fragment texture layers

A few images (ore texture cutouts, grayscale textures for procedural asset creation, etc.) constitute derivative works of Minecraft, owned by Mojang, a subsidiary of Microsoft. These derivatives are used without explicit permission, without ill intent, and remain property of Mojang. Redistribution of this content is understood to be acceptable per Mojang's 'EXTENDED FUNCTIONALITY AND “MODS”' subsection of their [Commercial Usage Guidelines](https://www.minecraft.net/en-us/terms). NOT AN OFFICIAL MINECRAFT PRODUCT. NOT APPROVED BY OR ASSOCIATED WITH MOJANG.
