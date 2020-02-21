function initializeCoreMod() {
    return {
        'get_all_scan_data': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraftforge.fml.ModList',
                'methodName': 'getAllScanData',
                'methodDesc': '()Ljava/util/List;'
            },
            'transformer': function (method) {
                Java.type('net.minecraftforge.coremod.api.ASMAPI').log("INFO", "Vampirism is hacking Forge ModList#getAllScanData to implement PR https://github.com/MinecraftForge/MinecraftForge/pull/6370");

                var ASM = Java.type('net.minecraftforge.coremod.api.ASMAPI');
                var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
                var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');


                var existingDistinct = ASM.findFirstMethodCall(method, ASM.MethodType.INTERFACE, "java/util/stream/Stream", "distinct", "()Ljava/util/stream/Stream;");

                if (existingDistinct) {
                    Java.type('net.minecraftforge.coremod.api.ASMAPI').log("INFO", "#distinct call already present in ModList#getAllScanData. Skipping coremod");
                    return;
                }

                //Find insert point -> locate second hasNext in this method
                var firstMap = ASM.findFirstMethodCall(method, ASM.MethodType.INTERFACE, "java/util/stream/Stream", "map", "(Ljava/util/function/Function;)Ljava/util/stream/Stream;");
                if (!firstMap) {
                    Java.type('net.minecraftforge.coremod.api.ASMAPI').log("ERROR", "Failed to find first #map call in ModList#getAllScanData");
                    return;
                }
                var indexOfFirstMap = method.instructions.indexOf(firstMap);
                var secondMap = ASM.findFirstMethodCallAfter(method, ASM.MethodType.INTERFACE, "java/util/stream/Stream", "map", "(Ljava/util/function/Function;)Ljava/util/stream/Stream;", indexOfFirstMap + 1); //Search for next hasNext (IMPORTANT findFirstMethodCallAfter requires +1 to look for next one
                if (!secondMap) {
                    Java.type('net.minecraftforge.coremod.api.ASMAPI').log("ERROR", "Failed to find second #map call in ModList#getAllScanData");
                    return;
                }
                var indexOfSecondMap = method.instructions.indexOf(secondMap);


                var start = method.instructions.get(indexOfSecondMap);

                var newInstructions = new InsnList();

                newInstructions.add(ASM.buildMethodCall("java/util/stream/Stream", "distinct", "()Ljava/util/stream/Stream;", ASM.MethodType.INTERFACE));
                newInstructions.add(new LabelNode());

                //Jump to iterator begin

                method.instructions.insert(start, newInstructions);


                /*
                Should look like this afterwards

                   L5
                    LINENUMBER 197 L5
                    INVOKEINTERFACE java/util/stream/Stream.map (Ljava/util/function/Function;)Ljava/util/stream/Stream; (itf)
                   L6
                    LINENUMBER 198 L6
                    INVOKEINTERFACE java/util/stream/Stream.distinct ()Ljava/util/stream/Stream; (itf)
                    INVOKEDYNAMIC apply()Ljava/util/function/Function; [
                      // handle kind 0x6 : INVOKESTATIC
                      java/lang/invoke/LambdaMetafactory.metafactory(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;
                      // arguments:
                      (Ljava/lang/Object;)Ljava/lang/Object;,
                      // handle kind 0x5 : INVOKEVIRTUAL
                      net/minecraftforge/fml/loading/moddiscovery/ModFile.getScanResult()Lnet/minecraftforge/forgespi/language/ModFileScanData;,
                      (Lnet/minecraftforge/fml/loading/moddiscovery/ModFile;)Lnet/minecraftforge/forgespi/language/ModFileScanData;
                    ]
                   L7
                    LINENUMBER 199 L7
                    INVOKEINTERFACE java/util/stream/Stream.map (Ljava/util/function/Function;)Ljava/util/stream/Stream; (itf)
                   L8
                 */

                return method;
            }
        }
    }
}