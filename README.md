## What is Strata?

Strata is a Forge-based, rock and ore generation mod for Minecraft with a focus on providing a platform for new content via plain-text configuration files of minimal complexity.

Want to see what Strata is in pictures? Check [the gallery](https://github.com/Terrenteller/Strata/wiki/Gallery)!

## What makes Strata special?

_Flexibility_ and **integration**.

- Strata is not a world generation mod. Ideally, Strata is compatible with all of them!
- Plain-text configuration files allow anyone to create custom content without a line of code!
- Tilesets make it easy to define entire suites of stone blocks and their derivatives. Just add textures!
- Strata will load tile data from resource packs on both client and server. One ZIP to rule them all!
- Strata's two-in-one ore-in-host-rock system welcomes mod content cross-pollination and resource packs!
- Ores take on the appearance and properties of their host and drop both when mined!
- Strata will replicate existing recipes using its own content!
- Ores support the same plants as their hosts! (technical exceptions withstanding)
- Strata's redstone ore acts like vanilla redstone ore! (yes, this is important)
- Strata has no hard dependency on any other mod or modding library! (sans Forge, of course)

## How do I use Strata?

Strata will have little to no effect on gameplay outside of creative mode without a world generation mod to bring it to life (as much as rocks can have). Currently, the only option is [CustomOreGen](https://github.com/lawremi/CustomOreGen).

Strata's COG XML is 100% vanilla-compatible out-of-the-box, but is disabled as a whole by default. Given that changes to world generation is ironically destructive, it is easier to turn Strata on than it is to turn off. You may do so in the "Strata" tab of "Custom Ore Generation" options when creating a new world.

_NOTE: COG has a long-standing bug of replacing `emerald_ore` with `monster_egg` [here](https://github.com/lawremi/CustomOreGen/blob/db939431ccab85707754c69c6d54858d1fcdf9ff/src/main/resources/config/modules/Vanilla.xml#L2059). You will likely want to turn off COG's handling of vanilla ore generation in the "Vanilla" tab if you do not fix this yourself._

## How do I use Strata to create my own content?

- `docs/Strata.txt` describes tiledata keyvalues for the current version
- `config/strata/tiledata/<modid>` directories contain block/item configuration files
    - **The client must know about all blocks the server does else the client will hang on connect**
        - This applies to blocks defined by active resource packs as well
        - Unfortunately, the hang occurs outside of Strata's influence
- `config/strata/recipe/<modid>/blacklist.txt` restricts recipe replication

`<modid>` directories are only processed if a mod with that ID is present. Strata will extract its own configuration into corresponding directories on start but will not overwrite them.

For step-by-step instructions, consult the "Your First" series of tutorials on [the wiki](https://github.com/Terrenteller/Strata/wiki).

## How do I build Strata?

Refer to Forge documentation instead to prepare a development environment for use with an IDE.

**Arch Linux**

```
# pacman -Sy jdk8-openjdk
$ ./gradlew setupCIWorkspace
$ ./gradlew build
```

## "Why" is Strata?

Once upon a time, there was a mod called [PerFabricaAdAstra](https://github.com/lawremi/PerFabricaAdAstra) that fell by the wayside. Disappointed with the thought of what might never be, I took it upon myself to re-imagine the "Geologica" part of the PFAA triad, leaving Chemica and Fabrica to established tech mods like Mekanism and Immersive Engineering.

## Legal stuff

Strata is licenced under [LGPL 3.0](LICENCE.md) unless where otherwise stated. Markdown-formatted licences are provided by [IQAndreas/markdown-licenses](https://github.com/IQAndreas/markdown-licenses).

Original and remixed [PerFabricaAdAstra](https://github.com/lawremi/PerFabricaAdAstra) assets were, and are still, licensed under [The Artistic License 2.0](artistic-v2.0.md).

- Changes (where applicable) include, but may not be limited to:
    - Image resizing, tiling and edge resynthesis, and colour and transparency adjustments

Original and remixed [Geolosys](https://github.com/oitsjustjose/Geolosys) assets were, and still are, licensed under [GPL 3.0](gnu-gpl-v3.0.md).

- Changes include:
    - Organized, simplified, and duplicated ore sample model geometry
    - Synthesized a new ore cluster base image by removing differences from originals

Original and remixed [TerraFirmaCraft](https://github.com/TerraFirmaCraft/TerraFirmaCraft) assets were, and still are, licensed under [EUPL 1.2](EUPL-1.2 EN.txt).

- Changes include:
    - Reverse-engineered rock fragment texture layers

A few images (ore texture cutouts, grayscale textures for procedural asset creation, etc.) constitute derivative works of Minecraft, owned by Mojang, a subsidiary of Microsoft. These derivatives are used without explicit permission, without ill intent, and remain property of Mojang. Redistribution of this content is understood to be acceptable per Mojang's 'EXTENDED FUNCTIONALITY AND “MODS”' subsection of their [Commercial Usage Guidelines](https://account.mojang.com/terms?ref=ft#commercial). NOT AN OFFICIAL MINECRAFT PRODUCT. NOT APPROVED BY OR ASSOCIATED WITH MOJANG.
