package com.chattriggers.ctjs.launch.plugin

import dev.falsehonesty.asmhelper.dsl.At
import dev.falsehonesty.asmhelper.dsl.InjectionPoint
import dev.falsehonesty.asmhelper.dsl.inject
import dev.falsehonesty.asmhelper.dsl.instructions.*

fun injectScreenshotHelper() = inject {
    className = "net/minecraft/util/ScreenShotHelper"
    methodName = "saveScreenshot"
    methodDesc = "(L$FILE;IILnet/minecraft/client/shader/Framebuffer;)L$ICHAT_COMPONENT;"
    at = At(InjectionPoint.HEAD)

    methodMaps = mapOf(
        "getTimestampedPNGFileForDirectory" to "func_74290_a",
        "func_148260_a" to "saveScreenshot"
    )

    insnList {
        createInstance(CANCELLABLE_EVENT, "()V")
        val event = astore()

        getStatic(TRIGGER_TYPE, "SCREENSHOT_TAKEN", "L$TRIGGER_TYPE;")

        invokeVirtual(TRIGGER_TYPE, "triggerAll", "([Ljava/lang/Object;)V") {
            array(2, "java/lang/Object") {
                aadd {
                    invokeStatic(className, "getTimestampedPNGFileForDirectory", "(L$FILE;)L$FILE;") {
                        createInstance(FILE, "(L$FILE;Ljava/lang/String;)V") {
                            aload(0)
                            ldc("screenshots")
                        }
                    }
                    invokeVirtual(FILE, "getName", "()Ljava/lang/String;")
                }

                aadd {
                    load(event)
                }
            }
        }

        load(event)
        invoke(InvokeType.VIRTUAL, CANCELLABLE_EVENT, "isCancelled", "()Z")

        ifClause(JumpCondition.FALSE) {
            aconst_null()
            areturn()
        }
    }
}
