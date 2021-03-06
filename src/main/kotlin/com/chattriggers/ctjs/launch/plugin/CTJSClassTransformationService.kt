package com.chattriggers.ctjs.launch.plugin

import dev.falsehonesty.asmhelper.ClassTransformationService

class CTJSClassTransformationService : ClassTransformationService {
    override fun transformerClasses() = listOf("com.chattriggers.ctjs.launch.plugin.CTJSTransformer")
}
