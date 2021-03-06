package com.chattriggers.ctjs.launch.plugin

import dev.falsehonesty.asmhelper.dsl.At
import dev.falsehonesty.asmhelper.dsl.InjectionPoint
import dev.falsehonesty.asmhelper.dsl.inject
import dev.falsehonesty.asmhelper.dsl.instructions.*

fun makeGuiScreenInjections() {
    injectSendChatMessage()
    injectHandleKeyboardInput()
    injectMouseClick()
    injectMouseRelease()
    injectMouseDrag()
    injectTextComponentClick()
    injectTextComponentHover()
    injectRenderTooltip()
}

fun injectSendChatMessage() = inject {
    className = "net/minecraft/client/gui/GuiScreen"
    methodName = "sendChatMessage"
    methodDesc = "(Ljava/lang/String;Z)V"
    at = At(InjectionPoint.HEAD)

    methodMaps = mapOf("func_175281_b" to "sendChatMessage")

    insnList {
        createInstance(CANCELLABLE_EVENT, "()V")
        val event = astore()

        getStatic(TRIGGER_TYPE, "MESSAGE_SENT", "L$TRIGGER_TYPE;")
        invokeVirtual(TRIGGER_TYPE, "triggerAll", "([Ljava/lang/Object;)V") {
            array(2, "java/lang/Object") {
                aadd { aload(1) }
                aadd { load(event) }
            }
        }

        load(event)
        invoke(InvokeType.VIRTUAL, CANCELLABLE_EVENT, "isCancelled", "()Z")

        ifClause(JumpCondition.FALSE) {
            methodReturn()
        }
    }
}

fun injectHandleKeyboardInput() = inject {
    className = "net/minecraft/client/gui/GuiScreen"
    methodName = "handleKeyboardInput"
    methodDesc = "()V"

    at = At(
        InjectionPoint.INVOKE(
            Descriptor(
                "net/minecraft/client/gui/GuiScreen",
                "keyTyped",
                "(CI)V"
            )
        )
    )

    methodMaps = mapOf(
        "func_146282_l" to "handleKeyboardInput",
        "func_73869_a" to "keyTyped"
    )

    insnList {
        createInstance(CANCELLABLE_EVENT, "()V")
        val event = astore()

        getStatic(TRIGGER_TYPE, "GUI_KEY", "L$TRIGGER_TYPE;")
        invokeVirtual(TRIGGER_TYPE, "triggerAll", "([Ljava/lang/Object;)V") {
            array(4, "java/lang/Object") {
                aadd {
                    invokeStatic("org/lwjgl/input/Keyboard", "getEventCharacter", "()C")
                    invokeStatic("java/lang/Character", "valueOf", "(C)Ljava/lang/Character;")
                }

                aadd {
                    invokeStatic("org/lwjgl/input/Keyboard", "getEventKey", "()I")
                    invokeStatic("java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;")
                }

                aadd {
                    aload(0)
                }

                aadd { load(event) }
            }
        }

        load(event)
        invoke(InvokeType.VIRTUAL, CANCELLABLE_EVENT, "isCancelled", "()Z")

        ifClause(JumpCondition.FALSE) {
            methodReturn()
        }
    }
}

fun injectMouseClick() = inject {
    className = "net/minecraft/client/gui/GuiScreen"
    methodName = "handleMouseInput"
    methodDesc = "()V"

    at = At(
        InjectionPoint.INVOKE(
            Descriptor(
                "net/minecraft/client/gui/GuiScreen",
                "mouseClicked",
                "(III)V"
            )
        )
    )

    methodMaps = mapOf(
        "func_146274_d" to "handleMouseInput",
        "func_73864_a" to "mouseClicked"
    )

    insnList {
        createInstance(CANCELLABLE_EVENT, "()V")
        val event = astore()

        getStatic(TRIGGER_TYPE, "GUI_MOUSE_CLICK", "L$TRIGGER_TYPE;")
        invokeVirtual(TRIGGER_TYPE, "triggerAll", "([Ljava/lang/Object;)V") {
            array(5, "java/lang/Object") {
                aadd {
                    iload(1)
                    invokeStatic("java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;")
                }

                aadd {
                    iload(2)
                    invokeStatic("java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;")
                }

                aadd {
                    iload(3)
                    invokeStatic("java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;")
                }

                aadd {
                    aload(0)
                }

                aadd { load(event) }
            }
        }

        load(event)
        invoke(InvokeType.VIRTUAL, CANCELLABLE_EVENT, "isCancelled", "()Z")

        ifClause(JumpCondition.FALSE) {
            methodReturn()
        }
    }
}

fun injectMouseRelease() = inject {
    className = "net/minecraft/client/gui/GuiScreen"
    methodName = "handleMouseInput"
    methodDesc = "()V"

    at = At(
        InjectionPoint.INVOKE(
            Descriptor(
                "net/minecraft/client/gui/GuiScreen",
                "mouseReleased",
                "(III)V"
            )
        )
    )

    methodMaps = mapOf(
        "func_146274_d" to "handleMouseInput",
        "func_146286_b" to "mouseReleased"
    )

    insnList {
        createInstance(CANCELLABLE_EVENT, "()V")
        val event = astore()

        getStatic(TRIGGER_TYPE, "GUI_MOUSE_RELEASE", "L$TRIGGER_TYPE;")
        invokeVirtual(TRIGGER_TYPE, "triggerAll", "([Ljava/lang/Object;)V") {
            array(5, "java/lang/Object") {
                aadd {
                    iload(1)
                    invokeStatic("java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;")
                }

                aadd {
                    iload(2)
                    invokeStatic("java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;")
                }

                aadd {
                    iload(3)
                    invokeStatic("java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;")
                }

                aadd {
                    aload(0)
                }

                aadd { load(event) }
            }
        }

        load(event)
        invoke(InvokeType.VIRTUAL, CANCELLABLE_EVENT, "isCancelled", "()Z")

        ifClause(JumpCondition.FALSE) {
            methodReturn()
        }
    }
}

fun injectMouseDrag() = inject {
    className = "net/minecraft/client/gui/GuiScreen"
    methodName = "handleMouseInput"
    methodDesc = "()V"

    at = At(
        InjectionPoint.INVOKE(
            Descriptor(
                "net/minecraft/client/gui/GuiScreen",
                "mouseClickMove",
                "(IIIJ)V"
            )
        )
    )

    methodMaps = mapOf(
        "func_146274_d" to "handleMouseInput",
        "func_146273_a" to "mouseClickMove"
    )

    insnList {
        createInstance(CANCELLABLE_EVENT, "()V")
        val event = astore()

        getStatic(TRIGGER_TYPE, "GUI_MOUSE_DRAG", "L$TRIGGER_TYPE;")
        invokeVirtual(TRIGGER_TYPE, "triggerAll", "([Ljava/lang/Object;)V") {
            array(5, "java/lang/Object") {
                aadd {
                    iload(1)
                    invokeStatic("java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;")
                }

                aadd {
                    iload(2)
                    invokeStatic("java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;")
                }

                aadd {
                    iload(3)
                    invokeStatic("java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;")
                }

                aadd {
                    aload(0)
                }

                aadd { load(event) }
            }
        }

        load(event)
        invoke(InvokeType.VIRTUAL, CANCELLABLE_EVENT, "isCancelled", "()Z")

        ifClause(JumpCondition.FALSE) {
            methodReturn()
        }
    }
}

fun injectTextComponentClick() = inject {
    className = "net/minecraft/client/gui/GuiScreen"
    methodName = "handleComponentClick"
    methodDesc = "(L$ICHAT_COMPONENT;)Z"
    at = At(InjectionPoint.HEAD)

    methodMaps = mapOf("func_175276_a" to "handleComponentClick")

    insnList {
        createInstance(CANCELLABLE_EVENT, "()V")
        val event = astore()

        getStatic(TRIGGER_TYPE, "CHAT_COMPONENT_CLICKED", "L$TRIGGER_TYPE;")
        invokeVirtual(TRIGGER_TYPE, "trigger", "([Ljava/lang/Object;)V") {
            array(2, "java/lang/Object") {
                aadd {
                    aload(1)

                    ifElseClause(JumpCondition.NULL) {
                        ifCode {
                            aconst_null()
                        }
                        elseCode {
                            createInstance("com/chattriggers/ctjs/minecraft/objects/message/TextComponent", "(L$ICHAT_COMPONENT;)V") {
                                aload(1)
                            }
                        }
                    }
                }

                aadd { load(event) }
            }
        }

        load(event)
        invoke(InvokeType.VIRTUAL, CANCELLABLE_EVENT, "isCancelled", "()Z")

        ifClause(JumpCondition.FALSE) {
            int(0)
            ireturn()
        }
    }
}

fun injectTextComponentHover() = inject {
    className = "net/minecraft/client/gui/GuiScreen"
    methodName = "handleComponentHover"
    methodDesc = "(L$ICHAT_COMPONENT;II)V"
    at = At(InjectionPoint.HEAD)

    methodMaps = mapOf("func_175272_a" to "handleComponentHover")

    insnList {
        createInstance(CANCELLABLE_EVENT, "()V")
        val event = astore()

        getStatic(TRIGGER_TYPE, "CHAT_COMPONENT_HOVERED", "L$TRIGGER_TYPE;")
        invokeVirtual(TRIGGER_TYPE, "trigger", "([Ljava/lang/Object;)V") {
            array(4, "java/lang/Object") {
                aadd {
                    aload(1)

                    ifElseClause(JumpCondition.NULL) {
                        ifCode {
                            aconst_null()
                        }
                        elseCode {
                            createInstance("com/chattriggers/ctjs/minecraft/objects/message/TextComponent", "(L$ICHAT_COMPONENT;)V") {
                                aload(1)
                            }
                        }
                    }
                }

                aadd {
                    iload(2)
                    invokeStatic("java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;")
                }

                aadd {
                    iload(3)
                    invokeStatic("java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;")
                }

                aadd { load(event) }
            }
        }

        load(event)
        invoke(InvokeType.VIRTUAL, CANCELLABLE_EVENT, "isCancelled", "()Z")

        ifClause(JumpCondition.FALSE) {
            methodReturn()
        }
    }
}

fun injectRenderTooltip() = inject {
    className = "net/minecraft/client/gui/GuiScreen"
    methodName = "renderToolTip"
    methodDesc = "(L$ITEM_STACK;II)V"

    at = At(
        InjectionPoint.INVOKE(
            Descriptor(
                ITEM_STACK,
                "getTooltip",
                "(L$ENTITY_PLAYER;Z)Ljava/util/List;"
            )
        ),
        before = false,
        shift = 1
    )

    methodMaps = mapOf(
        "func_146285_a" to "renderToolTip",
        "func_82840_a" to "getTooltip"
    )

    insnList {
        createInstance(CANCELLABLE_EVENT, "()V")
        val event = astore()

        getStatic(TRIGGER_TYPE, "TOOLTIP", "L$TRIGGER_TYPE;")
        invokeVirtual(TRIGGER_TYPE, "triggerAll", "([Ljava/lang/Object;)V") {
            array(3, "java/lang/Object") {
                aadd {
                    aload(4)
                }

                aadd {
                    createInstance("com/chattriggers/ctjs/minecraft/wrappers/objects/inventory/Item", "(L$ITEM_STACK;)V") {
                        aload(1)
                    }
                }

                aadd { load(event) }
            }
        }

        load(event)
        invoke(InvokeType.VIRTUAL, CANCELLABLE_EVENT, "isCancelled", "()Z")

        ifClause(JumpCondition.FALSE) {
            methodReturn()
        }
    }
}
