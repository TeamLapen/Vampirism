
# Structure

## Branches

| Branch                                             | Description                                |
|----------------------------------------------------|--------------------------------------------|
| dev                                                | The main development branch.               |
| version/<mc-version>/[<sub-version>/]<mod-version> | The main branch for a specific MC version. |
| feature/*                                          | A feature branches.                        |
| pages/*                                            | The wiki pages deployed to github pages.   |

## Projects

| Project               | Description                                                                                                                                                                                                                                               |
|-----------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `faction-api`         | Pure API module defining the faction system: interfaces for faction registration, player faction handlers, events, registries, and sync abstractions. Depend on this when you only need to read/interact with faction state.                              |
| `faction`             | Implementation of `faction-api`. Contains the full faction logic.                                                                                                                                                                                         |
| `vampirism-api`       | Pure API module for the Vampirism mod, extending `faction-api`. Exposes interfaces for vampire and hunter players, creature conversion, world events, difficulty, data maps, and all Vampirism registries. This is the primary dependency for addon mods. |
| `vampirism`           | Full implementation of the Vampirism mod. Implements all vampire/hunter mechanics, world generation, entity conversions, mod integrations (JEI, GuideAPI, TerraBlender, …), and NeoForge service wiring. Depends on `vampirism-api` and `faction`;        |
| `vampirism-processor` | An annotation processor for generating code                                                                                                                                                                                                               |

#### Vampirism -> FactionApi dependency
Vampirism does not directly depend on the faction mod source code. For better handling, Vampirism depends on a published maven artifact instead of a project reference.

If you want to build Vampirism against FactionApi from source, then you need to set the `vampirism_factions_source_dependency` property to true. The pipeline will always fall back to the published artifacts.


## Code Style
The code style used in this project is the IntelliJ default one.
