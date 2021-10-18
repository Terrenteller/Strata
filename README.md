## What is Strata?

Strata is a Forge-based, rock and ore generation mod for Minecraft with a focus on providing new content via plain-text configuration files of minimal complexity.

## What makes Strata special?

_Flexibility_ and **correctness**.

- Strata is not a world generation mod. Ideally, with no conflict of interest, Strata is compatible with all of them!
- Plain-text configuration files allow anyone can create their own Strata-powered content without a line of code!
- Tilesets make it easy to define entire suites of stone blocks and their derivatives. Just add textures!
- Strata's two-in-one ore and host rock system facilitates mod content cross-pollination!
- Ores take on the appearance and properties of their host and drop both when mined!

## How do I use Strata?

Strata will have little to no gameplay effect outside of creative mode without a world generation mod to bring it to life (as much as rocks can have). Currently, the only option is [CustomOreGen](https://github.com/lawremi/CustomOreGen).

## How do I use Strata to create my own content?

Strata will unpack the following on start (if missing):
- `docs/Strata.txt` contains tiledata keyvalue documentation for the current version.
- `config/strata/tiledata` contains block/item configuration files. Use these as a starting point for any new blocks you wish to add. You are encouraged to put new configuration files in a subdirectory named after the mod/modpack/etc. for organization. The client can know about things the server doesn't, but **the client must know about everything the server does**.
- `config/strata/recipe/replication` contains recipe replication configuration files.

## "Why" is Strata?

Once upon a time, there was a mod called [PerFabricaAdAstra](https://github.com/lawremi/PerFabricaAdAstra) that fell by the wayside. Disappointed with the thought of what might never be, I took it upon myself to recreate the "Geologica" part of the PFAA triad, leaving Chemica and Fabrica to other tech mods like Mekanism and Immersive Engineering.

## Legal stuff

[cc-by-nc-sa]: http://creativecommons.org/licenses/by-nc-sa/4.0/
[cc-by-nc-sa-image]: https://licensebuttons.net/l/by-nc-sa/4.0/88x31.png
[cc-by-nc-sa-shield]: https://img.shields.io/badge/License-CC%20BY--NC--SA%204.0-lightgrey.svg

This work is licensed under a
[Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License][cc-by-nc-sa].
