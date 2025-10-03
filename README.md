<img src="./images/catroom.png" height="160" alt="CatRoom" align="right">

# CatRoom
CatRoom is a Cleanroom+Bukkit+Spigot server software forked from [CatServer](https://github.com/Luohuayu/CatServer).

## Features:

- Replaced Forge with Cleanroom
- Enhanced mod compatibility
- Built-in Forge-Bukkit permission bridge
- Make use of modern Java features
- Optimized plugin performance

For all fixes, see [fixed upstream issues](FIXED_UPSTREAM_ISSUES.md)

## Components:

- Minecraft Coder Pack
- CleanroomLoader (Continuation + Revamp of ForgeModLoader)
- Cleanroom Minecraft (Continuation + Revamp of MinecraftForge)
- Customized Mixin
- Bytecode Patcher (Coming Soon) \[Inspired by [Bansoukou](https://github.com/LoliKingdom/Bansoukou) and [Bytecode Patcher](https://github.com/jbredwards/Bytecode-Patcher)]
- [Fugue](https://www.curseforge.com/minecraft/mc-mods/fugue), a mod patches many incompatibilities.
- Javassist
- [Scalar](https://www.curseforge.com/minecraft/mc-mods/scalar-legacy), a Scala provider. We made Scala libraries become a standalone mod so it can be updated.
- [Forgelin-Continuous](https://www.curseforge.com/minecraft/mc-mods/forgelin-continuous) and [LibrarianLib-Continuous](https://www.curseforge.com/minecraft/mc-mods/librarianlib-continuous)

### About Cross-compat Between Forge and Cleanroom

- Cleanroom mods (Fugue, Scalar) will be ignored by Forge, so then won't crash existed players
- Cleanroom integrated mods (MixinBooter, ConfigAnyTime) will be ignored by Cleanroom, then won't crash new players
- The version of built-in MixinBooter is configurable in forge_early.cfg

### List of Obsoleted/Incompatible Mods

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
2. Import the `build.gradle` into your IDE (most preferably IntelliJ IDEA)
3. Once the import has finished, run `gradlew setup`
4. Run `gradlew --stop` to stop the daemon and prevent ForgeGradle gone wrong 
5. Build with `gradlew build`

## Development Tips:

- Only modify `projects/cleanroom/src/` directory if you want to change vanilla
- Run `gradlew genPatches` before commit, or the changes won't exist
- Modifications on `src/` doesn't need generating patches
- [Tips from Forge](https://github.com/MinecraftForge/MinecraftForge/wiki/If-you-want-to-contribute-to-Forge) are still apply, keep the patches clean!
- The current patches is full of useless hunks after we switched to VineFlower, we encourage contributors to clean up these patches manually.

## Mod Development:

Official template is here: [template](https://github.com/CleanroomMC/CleanroomModTemplate)

A porting guide is available in [Cleanroom wiki](https://cleanroommc.com/wiki/cleanroom-mod-development/introduction) (WIP).
