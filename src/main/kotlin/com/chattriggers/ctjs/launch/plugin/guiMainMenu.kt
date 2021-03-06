package com.chattriggers.ctjs.launch.plugin

import dev.falsehonesty.asmhelper.dsl.At
import dev.falsehonesty.asmhelper.dsl.InjectionPoint
import dev.falsehonesty.asmhelper.dsl.inject
import dev.falsehonesty.asmhelper.dsl.instructions.Descriptor

fun injectGuiMainMenu() = inject {
    className = "net/minecraft/client/gui/GuiMainMenu"
    methodName = "drawScreen"
    methodDesc = "(IIF)V"

    at = At(
        InjectionPoint.INVOKE(
            Descriptor(
                "net/minecraftforge/client/ForgeHooksClient",
                "renderMainMenu",
                "(Lnet/minecraft/client/gui/GuiMainMenu;Lnet/minecraft/client/gui/FontRenderer;II)V"
            )
        )
    )

    methodMaps = mapOf("func_73863_a" to "drawScreen")

    insnList {
        invokeKObjectFunction("com/chattriggers/ctjs/utils/UpdateChecker", "drawUpdateMessage", "()V")
    }
}
