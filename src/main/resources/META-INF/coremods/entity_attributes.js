function initializeCoreMod() {
    return {
        'player_attributes': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.entity.player.PlayerEntity',
                'methodName': 'func_234570_el_', //func_234570_el_
                'methodDesc': '()Lnet/minecraft/entity/ai/attributes/AttributeModifierMap$MutableAttribute;'
            },
            'transformer': function (method) {
                Java.type('net.minecraftforge.coremod.api.ASMAPI').log("INFO", "Patching PlayerEntity#func_234570_el_ to include additional attributes");

                var ASM = Java.type('net.minecraftforge.coremod.api.ASMAPI');
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
                var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');


                var returnInstruction = ASM.findFirstInstruction(method, Opcodes.ARETURN);

                var newInstructions = new InsnList();

                newInstructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "de/teamlapen/vampirism/util/ASMHooks", "attribute_sundamage", "Lnet/minecraft/entity/ai/attributes/Attribute;"));
                newInstructions.add(ASM.buildMethodCall("net/minecraft/entity/ai/attributes/AttributeModifierMap$MutableAttribute", "func_233814_a_", "(Lnet/minecraft/entity/ai/attributes/Attribute;)Lnet/minecraft/entity/ai/attributes/AttributeModifierMap$MutableAttribute;", ASM.MethodType.VIRTUAL));
                newInstructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "de/teamlapen/vampirism/util/ASMHooks", "attribute_blood_exhaustion", "Lnet/minecraft/entity/ai/attributes/Attribute;"));
                newInstructions.add(ASM.buildMethodCall("net/minecraft/entity/ai/attributes/AttributeModifierMap$MutableAttribute", "func_233814_a_", "(Lnet/minecraft/entity/ai/attributes/Attribute;)Lnet/minecraft/entity/ai/attributes/AttributeModifierMap$MutableAttribute;", ASM.MethodType.VIRTUAL));
                newInstructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "de/teamlapen/vampirism/util/ASMHooks", "attribute_bite_damage", "Lnet/minecraft/entity/ai/attributes/Attribute;"));
                newInstructions.add(ASM.buildMethodCall("net/minecraft/entity/ai/attributes/AttributeModifierMap$MutableAttribute", "func_233814_a_", "(Lnet/minecraft/entity/ai/attributes/Attribute;)Lnet/minecraft/entity/ai/attributes/AttributeModifierMap$MutableAttribute;", ASM.MethodType.VIRTUAL));

                method.instructions.insertBefore(returnInstruction, newInstructions);

                return method;
            }
        }
    }
}