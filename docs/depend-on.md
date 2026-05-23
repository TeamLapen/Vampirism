# Depending on FactionApi/Vampirism in gradle

To use Vampirism or FactionAPI as a compile-time dependency in your own mod, add the maven repository and the relevant artifact to your build configuration.

Addon mods typically only need `FactionApi-api` or `Vampirism-api`. Use the full `Vampirism` artifact only if you need access to implementation internals.

Both mods need a maven repository:  
_The snapshot repository is only required for snapshot builds (dev,alpha,beta,test)_

```groovy
repositories {
    maven {
        name = "Maxanier Releases"
        url = "https://maven.maxanier.de/releases"
        content {
            includeGroupAndSubgroups "de.teamlapen"
        }
    }
    maven {
        name = "Maxanier Snapshots"
        url = "https://maven.maxanier.de/snapshots"
        content {
            includeGroupAndSubgroups "de.teamlapen"
        }
    }
}
```

### FactionApi

[![](https://img.shields.io/badge/dynamic/xml?url=https%3A%2F%2Fmaven.maxanier.de%2Freleases%2Fde%2Fteamlapen%2Ffaction%2FFactionApi%2Fmaven-metadata.xml&query=%2Fmetadata%2Fversioning%2Flatest&logo=apachemaven&logoColor=%23C71A36&label=releases&color=%2303C75A)](https://maven.maxanier.de/#/releases/de/teamlapen/faction/FactionApi)
[![](https://img.shields.io/badge/dynamic/xml?url=https%3A%2F%2Fmaven.maxanier.de%2Fsnapshots%2Fde%2Fteamlapen%2Ffaction%2FFactionApi%2Fmaven-metadata.xml&query=%2Fmetadata%2Fversioning%2Flatest&logo=apachemaven&logoColor=%23C71A36&label=snapshots&color=%23008CFF)](https://maven.maxanier.de/#/snapshots/de/teamlapen/faction/FactionApi)


You can choose between the api and the full implementation.

_Depending only on the api is recommended_

#### Api Only
```groovy
dependencies {
    compileOnly "de.teamlapen.faction:FactionApi-api:<version>"
    runtimeOnly "de.teamlapen.faction:FactionApi:<version>"
}
```

#### Full implementation
```groovy
dependencies {
    implementation "de.teamlapen.faction:FactionApi:<version>"
}
```

#### With Extras

This allows access to the interface injections and access transformer of vampirism on compile time.
```groovy
dependencies {
    implementation(accessTransformers(interfaceInjectionData("de.teamlapen.faction:FactionApi:<version>")))
}
```


### Vampirism

[![](https://img.shields.io/badge/dynamic/xml?url=https%3A%2F%2Fmaven.maxanier.de%2Freleases%2Fde%2Fteamlapen%2Fvampirism%2FVampirism%2Fmaven-metadata.xml&query=%2Fmetadata%2Fversioning%2Flatest&logo=apachemaven&logoColor=%23C71A36&label=releases&color=%2303C75A)](https://maven.maxanier.de/#/releases/de/teamlapen/vampirism/Vampirism)
[![](https://img.shields.io/badge/dynamic/xml?url=https%3A%2F%2Fmaven.maxanier.de%2Fsnapshots%2Fde%2Fteamlapen%2Fvampirism%2FVampirism%2Fmaven-metadata.xml&query=%2Fmetadata%2Fversioning%2Flatest&logo=apachemaven&logoColor=%23C71A36&label=snapshots&color=%23008CFF)](https://maven.maxanier.de/#/snapshots/de/teamlapen/vampirism/Vampirism)

If you just want to depend on vampirism you only need to add vampirism as dependency (the faction api will be automatically resolved)

_Depending only on the api is recommended_

#### Api Only
```groovy
dependencies {
    compileOnly "de.teamlapen.vampirism:Vampirism-api:<version>"
    runtimeOnly "de.teamlapen.vampirism:Vampirism:<version>"
}
```

#### Full implementation
```groovy
dependencies {
    implementation "de.teamlapen.vampirism:Vampirism:<version>"
}
```

#### With Extras

This allows access to the interface injections and access transformer of vampirism on compile time.
```groovy
dependencies {
    implementation(accessTransformers(interfaceInjectionData("de.teamlapen.vampirism:Vampirism:<version>")))
}
```
