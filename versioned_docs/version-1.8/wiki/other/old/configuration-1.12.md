---
sidebar_position: 6
title: Configuration 1.12
---

Vampirism has a lot of configuration and balance options.  
You can change them by either editing the files in your .minecraft/config/vampirism folder manually  
or use the in-game menu to edit the main and the balance settings (Main Menu -> Mods -> Vampirism -> Config).  

There are two types of options: General configuration and balance values.

## General configuration - _vampirism.cfg_
This file contains the main options. You can change id values, change some gameplay options or adjust the gui position.
You can also disable some parts of the mod, but it is not guaranteed that everything works fine and is balanced then.
There is also a "vampire_realism_mode" which changes quite a lot of balance options at one to make it more "realistic" out of a "vampire book/movie" point of view. For now it e.g. gives creatures a sanguinare effect before they are converted.
#### Noticeable Examples
_Sundamage Dimensions_  
It is possible to specify if you receive sundamge in each individual dimension.  
You can set a default for unspecified dimension (sundamage_default).  
You can specify sundamage for a dimension by adding `:<1/0>` to sundamge_dims (e.g.`5:1` to enable sundamage in dim 5). Use one line/String for each dimension.  
You can find out the dimension you are in game with "/vampirism currentDimension".   
_Auto convert glas bottles_
By default Vampirism automatically converts between blood bottles and glas bottles depending on what is needed right now.  
If you do not want this or it causes problems with another mod, you can disable it.

## Balance - _balance/_
In this folder you will find quite a few files with a lot of options. They allow you to balance Vampirism after your own wishes and should be pretty self-explaining.  
In case you haven't changed anything, it is very recommend to use the `/resetBalance <category>` after updating Vampirism to a new version to apply changed default balance values.  
You can either reset all files at once (Use "all") or specific ones.
