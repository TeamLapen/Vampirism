---
sidebar_position: 2
title: Setup
---

## Setup Gradle
To use the Vampirism API in your dev environment, you need to add the following dependencies to your project:

```gradle
repositories {
    maven {
        name = 'Vampirism'
        url = "https://maven.maxanier.de"
    }
}
dependencies {
    //compile against the Vampirism API
    compileOnly fg.deobf("de.teamlapen.vampirism:Vampirism:${mc_version}-${vampirism_version}:api")
    //at runtime (in your development environment) use the full Vampirism jar
    runtimeOnly fg.deobf("de.teamlapen.vampirism:Vampirism:${mc_version}-${vampirism_version}")
}
```

### Choose a version

`${mc_version}` needs to be replaced by your minecraft version. e.g. `1.16.5`

`${vampirism_version}` needs to be replaced by the version of Vampirism you want to use. e.g. `1.8.6`

For a full list of available versions see the [Maven listing](https://maven.maxanier.de/releases/de/teamlapen/vampirism/Vampirism), [Curseforge](https://www.curseforge.com/minecraft/mc-mods/vampirism-become-a-vampire/files) or [Modrinth](https://modrinth.com/mod/vampirism/versions)

These properties can be set in the `gradle.properties` file in the root of your project.
```
mc_version=1.16.5
vampirism_version=1.8.6
```

## Run Vampirism in a de-obfuscated environment

Vampirism uses **Mixins**. To be able to apply them in a de-obfuscated environment using a different set of mappings ( from the one Vampirism uses) you have to enable remapping of the refmap.  

To do this you need to add following properties to your run configurations and regenerate them:

```
property 'mixin.env.remapRefMap', 'true'
property 'mixin.env.refMapRemappingFile', "${projectDir}/build/createSrgToMcp/output.srg"
```

Should this not work you can try to disable the refmap by adding `property 'mixin.env.disableRefMap', 'true'`.  
If you still run into issues with mixins you can also set `mixin.env.ignoreRequired` to `true`. However, not all of Vampirism will work correctly then.

## Examples

Check out this example project: [Vampirism API Example](https://github.com/TeamLapen/VampirismAPIExample)