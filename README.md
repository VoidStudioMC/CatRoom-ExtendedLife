<img src="./images/catroom.png" height="160" alt="CatRoom" align="right">

# CatRoom
CatRoom is a Cleanroom+Bukkit+Spigot server software forked from [CatServer](https://github.com/Luohuayu/CatServer).

This fork is supporting by VoidStudio. We are on [Discord](https://discord.gg/RXUY7nkEKY)!

Cleanroom version: 0.6.10-alpha

## Features
### Implemented

- 1.12.2 on Java 25+
- A working *mod development template/kit*
- Patches for loading incompatible mods
- Built-in Mixin w/ handy bootstrapping
- Develop using Scala 3 + Kotlin 2
- Compatibility to 99% of Forge mods
- Built-in Forge-Bukkit permission bridge
- Optimized plugin performance

### Planning

- CleanroomGradle to replace ForgeGradle
- Actually useful APIs (See [here](https://github.com/orgs/CleanroomMC/projects/4/))
- Optimized PLEASE FUNKYRA DON'T FORGET ABOUT THIS

## Components:

- Minecraft Coder Pack
- CleanroomLoader (Continuation + Revamp of ForgeModLoader)
- Cleanroom Minecraft (Continuation + Revamp of MinecraftForge)
- Customized Mixin with improved bootstraps
- [Foundation](https://github.com/kappa-maintainer/Foundation/), a LaunchWrapper replacement with better debug logging.
- Bytecode Patcher (Coming Soon) \[Inspired by [Bansoukou](https://github.com/LoliKingdom/Bansoukou) and [Bytecode Patcher](https://github.com/jbredwards/Bytecode-Patcher)]
- [Fugue](https://www.curseforge.com/minecraft/mc-mods/fugue), a mod patches many incompatible mods.
- [Scalar](https://www.curseforge.com/minecraft/mc-mods/scalar-legacy), a Scala provider. We made Scala libraries become a standalone mod so it can be updated.
- [Forgelin-Continuous](https://www.curseforge.com/minecraft/mc-mods/forgelin-continuous) and [LibrarianLib-Continuous](https://www.curseforge.com/minecraft/mc-mods/librarianlib-continuous)
- Some fixes includes in [fixed upstream issues](FIXED_UPSTREAM_ISSUES.md)

### About Cross-compat Between Forge and Cleanroom

- Cleanroom mods (Fugue, Scalar) will be ignored by Forge, so then won't crash Forge
- Jar of Cleanroom integrated mods (MixinBooter, ConfigAnyTime) will be ignored by Cleanroom, then won't crash under Cleanroom
- The version of built-in MixinBooter is configurable in forge_early.cfg

### List of Obsoleted/Incompatible Mods ?? is it actually ??

- SpongeForge: Use [SpongeForge LTS](https://www.curseforge.com/minecraft/mc-mods/spongeforge)
- Phosphor: Use [Hesperus](https://www.curseforge.com/minecraft/mc-mods/hesperus) or [Alfheim Lighting Engine](https://www.curseforge.com/minecraft/mc-mods/alfheim-lighting-engine)
- Forgelin: Use [Forgelin-Continuous](https://www.curseforge.com/minecraft/mc-mods/forgelin-continuous)
- LibrarianLib: Use [LibrarianLib-Continuous](https://www.curseforge.com/minecraft/mc-mods/librarianlib-continuous)
- JustEnoughIds: Use [RoughlyEnoughIDs](https://www.curseforge.com/minecraft/mc-mods/reid)
- AdvancedShader: Binary patching, incompatible
- Polyfrost series: Waiting for official fix
- Essential.gg: Patched but still buggy

## Build Instructions:

1. Clone this repository
2. `git submodule init` then `git submodule update` ???
3. Import the `build.gradle` into your IDE (most preferably IntelliJ IDEA)
4. Once the import has finished, run `gradlew setup`
5. Run `gradlew --stop` to stop the daemon and prevent ForgeGradle gone wrong
6. Build with `gradlew build`

## Development Tips:

- Only modify `projects/catroom/src/` directory if you want to change vanilla
- Run `gradlew genPatches` before commit, or the changes won't exist
- Modifications on `src/` doesn't need generating patches
- [Tips from Forge](https://github.com/MinecraftForge/MinecraftForge/wiki/If-you-want-to-contribute-to-Forge) are still apply, keep the patches clean!
- The current patches is full of useless hunks after we switched to VineFlower, we encourage contributors to clean up these patches manually.

## Mod Development:

Official template is here: [template](https://github.com/CleanroomMC/CleanroomModTemplate)

A porting guide is available in [Cleanroom wiki](https://cleanroommc.com/wiki/cleanroom-mod-development/introduction) (WIP).
