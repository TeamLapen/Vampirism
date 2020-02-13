function initializeCoreMod() {
    return {
        'playersize': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.entity.player.PlayerEntity',
                'methodName': 'func_213305_a', //getSize
                'methodDesc': '(Lnet/minecraft/entity/Pose;)Lnet/minecraft/entity/EntitySize;'
            },
            'transformer': function (method) {
                var ASM = Java.type('net.minecraftforge.coremod.api.ASMAPI');
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
                var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
                var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
                var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');

                var newInstructions = new InsnList();

                var endNode = new LabelNode();
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                newInstructions.add(ASM.buildMethodCall("de/teamlapen/vampirism/util/ASMHooks", "overwritePlayerSize", "(Lnet/minecraft/entity/player/PlayerEntity;)Z", ASM.MethodType.STATIC));
                newInstructions.add(new JumpInsnNode(Opcodes.IFEQ, endNode));

                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
                newInstructions.add(ASM.buildMethodCall("de/teamlapen/vampirism/util/ASMHooks", "getPlayerSize", "(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/Pose;)Lnet/minecraft/entity/EntitySize;", ASM.MethodType.STATIC));
                newInstructions.add(new InsnNode(Opcodes.ARETURN));


                newInstructions.add(endNode);

                method.instructions.insert(newInstructions);
                return method;
            }
        }
    }
}