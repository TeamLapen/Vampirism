function initializeCoreMod() {
    return {
        'checkpiece': {
            'target': {
                'type': 'METHOD',
                'class': 'net/minecraft/world/gen/feature/jigsaw/JigsawManager$Assembler',
                'methodName': 'func_236831_a_', //func_236831_a_
                'methodDesc': '(Lnet/minecraft/world/gen/feature/structure/AbstractVillagePiece;Lorg/apache/commons/lang3/mutable/MutableObject;IIZ)V'
            },
            'transformer': function (method) {
                Java.type('net.minecraftforge.coremod.api.ASMAPI').log("INFO", "Adding hook to JigsawManager$Assembler#func_236831_a_ second for loop");

                var ASM = Java.type('net.minecraftforge.coremod.api.ASMAPI');
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
                var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
                var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
                var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
                var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');


                //Find insert point -> locate second hasNext in this method
                var firstHasNext = ASM.findFirstMethodCall(method, ASM.MethodType.INTERFACE, "java/util/Iterator", "hasNext", "()Z");
                var indexOfFirstHasNext = method.instructions.indexOf(firstHasNext);
                var secondHasNext = ASM.findFirstMethodCallAfter(method, ASM.MethodType.INTERFACE, "java/util/Iterator", "hasNext", "()Z", indexOfFirstHasNext + 1); //Search for next hasNext (IMPORTANT findFirstMethodCallAfter requires +1 to look for next one
                var indexOfSecondHasNext = method.instructions.indexOf(secondHasNext);
                // print("Found "+indexOfFirstHasNext+ " "+secondHasNext);

                var loopBeginLabel = method.instructions.get(indexOfSecondHasNext - 3);
                // print("Found begin label: "+loopBeginLabel+ " "+loopBeginLabel.getType());

                var loopStart = method.instructions.get(indexOfSecondHasNext + 6);
                // print("Found loop start "+loopStart+" "+loopStart.getType());
                // for(var i=-3;i<10;i++){
                //     print("N "+i+ " "+method.instructions.get(indexOfSecondHasNext+6+i));
                // }


                //Insert                                 if(ASMHooks.checkStructures(this.structurePieces,lvt_27_1_))continue ;
                var newInstructions = new InsnList();

                newInstructions.add(new LabelNode());
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                newInstructions.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/gen/feature/jigsaw/JigsawManager$Assembler", ASM.mapField("field_214886_e"), "Ljava/util/List;")); //structurePieces
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 27));
                newInstructions.add(ASM.buildMethodCall("de/teamlapen/vampirism/util/ASMHooks", "checkStructures", "(Ljava/util/List;Lnet/minecraft/world/gen/feature/jigsaw/JigsawPiece;)Z", ASM.MethodType.STATIC));
                newInstructions.add(new JumpInsnNode(Opcodes.IFEQ, loopStart));
                newInstructions.add(new JumpInsnNode(Opcodes.GOTO, loopBeginLabel));
                //Jump to iterator begin

                method.instructions.insertBefore(loopStart, newInstructions);


                /*
                Should look like this afterwards

                L35
                LINENUMBER 139 L36
               FRAME APPEND [java/util/Iterator]
                ALOAD 27
                INVOKEINTERFACE java/util/Iterator.hasNext ()Z (itf)
                IFEQ L36
                ALOAD 27
                INVOKEINTERFACE java/util/Iterator.next ()Ljava/lang/Object; (itf)
                CHECKCAST net/minecraft/world/gen/feature/jigsaw/JigsawPiece
                ASTORE 28
               L37
                LINENUMBER 150 L37
                ALOAD 0
                GETFIELD de/teamlapen/vampirism/util/JigsawManager$Assembler.structurePieces : Ljava/util/List;
                ALOAD 28
                INVOKESTATIC de/teamlapen/vampirism/util/ASMHooks.checkStructures (Ljava/util/List;Lnet/minecraft/world/gen/feature/jigsaw/JigsawPiece;)Z
                IFEQ L40
                GOTO L35
               L40
                LINENUMBER 143 L40
               FRAME APPEND [net/minecraft/world/gen/feature/jigsaw/JigsawPiece]
                ALOAD 28
                GETSTATIC net/minecraft/world/gen/feature/jigsaw/EmptyJigsawPiece.INSTANCE : Lnet/minecraft/world/gen/feature/jigsaw/EmptyJigsawPiece;
                IF_ACMPNE L38
               L42
                 */

                // print("Completed transformation");
                return method;
            }
        }
    }
}