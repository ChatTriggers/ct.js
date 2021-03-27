package com.chattriggers.ctjs.launch

import com.chattriggers.ctjs.engine.module.ModuleManager
import net.minecraft.crash.CrashReportCategory

object AsmUtils {
    fun addCrashSectionCallable(crashReport: CrashReportCategory) {
        //#if MC==10809
        crashReport.addCrashSectionCallable("ct.js modules") {
        //#else
        //$$ crashReport.addDetail("ct.js modules") {
        //#endif
            ModuleManager.cachedModules.toString()
        }
    }
}
