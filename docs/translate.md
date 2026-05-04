# Translate the mod

If you want to translate the mod, go to [Crowdin](https://crowdin.com/project/vampirism)

**Do not translate them in this repository!**
Translations may be ignored

## Translation files

There are 3 source files for translations:
- [FactionApi](../projects/faction/src/main/resources/assets/factionapi/lang/en_us.json)
- [Vampirism](../projects/vampirism/src/main/resources/assets/vampirism/lang/en_us.json)
- [Vampirism GuideBook](../projects/vampirism/src/main/resources/assets/vampirismguide/lang/en_us.json)


## Translation workflow

1. Add a new key to en_us.json
2. push to the main branch
3. Crowdin will automatically pull the new key
4. Translate the key in Crowdin
5. Crowdin will automatically push to the translation branch
6. Merge the translation branch into the main branch