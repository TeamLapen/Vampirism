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
                newInstructions.add(ASM.buildMethodCall("de/teamlapen/vampirism/util/ASMHooks", "handlePlayerAttributes", "(Lnet/minecraft/entity/ai/attributes/AttributeModifierMap$MutableAttribute;)Lnet/minecraft/entity/ai/attributes/AttributeModifierMap$MutableAttribute;", ASM.MethodType.STATIC));

                method.instructions.insertBefore(returnInstruction, newInstructions);

                return method;
            }
        }
    }
}