---
sidebar_position: 4
title: IMC Messages
---

Inter-Mod-Communication messages are a system provided by Forge/FML which allows communication between mods without requiring any "hard" dependency on an API/code.  
Mods can send messages to other mods in InterModEnqueueEvent containing a key and a value (String,int, CompoundNBT ...) and the receiving mod can parse them in InterModProcessEvent.  

Thereby they are a lightweight alternative to using another mods API, but far less powerful.  
They should be sent during init using `InterModComms#send`.  

## Vampirism's Messages
With Vampirism, you can send the following messages to Vampirism for better compatibility between your mod and Vampirism:
Find the parsing code [here](https://github.com/TeamLapen/Vampirism/blob/4bf2c73fb860a23de225edbae9c0b1c1ead3dd1a/src/main/java/de/teamlapen/vampirism/modcompat/IMCHandler.java)

#### Blood values
Set a blood value for your entities and thereby make them biteable. Value is a NBT compound containing your entities registry id and the designated value.  
`key`: `"blood-value"`    
`value`: `CompoundNBT`   
```
{
  "id": "yourmod:entityid",
  "value": 6
}
```

#### No-Sundamage Biome
Disable sundamge for a biome of yours.   
`key`:`"nosundamge-biome"`  
`value`:`ResourceLocation`(Your biome registry id)    
  
Configuration per dimension id is only available per API as they might not be assigned during init.