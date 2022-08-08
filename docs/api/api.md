---
sidebar_position: 6
title: API
---

There is a API which allow the creation of addon mods as well as compatibility integration in other mods. 
It is located in the `de.teamlapen.vampirism.api` package.  
If some API functionality is missing, create an issue.
If you should be planning to write an addon mod, consider contacting Maxanier.

### Setup
See the main [README](https://github.com/TeamLapen/Vampirism#api)
### Important classes
#### VampirismAPI
Central access point for all kind of registries and random things
#### VReference
Central location for all kind of static information/objects.  
This includes e.g. the faction objects of vampires and hunters, attributes, DamageSources, constants ...  

#### VIngameOverlays
Central access point for IIngameOverlays

### Examples
Checkout this example project: https://github.com/TeamLapen/VampirismAPIExample

If you want to create an addon which access all of Vampirism's classes, not just the API, checkout this https://github.com/TeamLapen/VampirismAddonExample and consider contacting maxanier.

A real example is the VampirismIntegrations mod which is some kind of mixtures between the two kinds above:
https://github.com/TeamLapen/VampirismIntegrations


