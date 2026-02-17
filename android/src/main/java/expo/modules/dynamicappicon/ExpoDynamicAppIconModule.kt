package expo.modules.dynamicappicon

import android.app.Activity;
import android.app.Application;
import android.content.Context
import android.content.pm.PackageManager;
import android.content.Intent;
import android.content.ComponentName;

import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition

class ExpoDynamicAppIconModule : Module() {
  override fun definition() = ModuleDefinition {
    Name("ExpoDynamicAppIcon")

    Function("setAppIcon") { name: String ->
        try {
            val packageName = context.packageName
            
            val suffixes = listOf(
                "two_dark", "two_light", "six_dark", "six_light", 
                "seven_dark", "seven_light", "eight_dark", "eight_light", 
                "ten_dark", "ten_light", "fifteen_dark", "fifteen_light", 
                "seventeen_dark", "seventeen_light", "nineteen_dark", "nineteen_light", 
                "twenty_dark", "twenty_light"
            )

            val targetClassName = if (name.isEmpty()) "$packageName.MainActivity" else "$packageName.MainActivity$name"

            val mainActivityName = "$packageName.MainActivity"
            pm.setComponentEnabledSetting(
                ComponentName(packageName, mainActivityName),
                if (targetClassName == mainActivityName) PackageManager.COMPONENT_ENABLED_STATE_ENABLED 
                else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )

            for (suffix in suffixes) {
                val aliasName = "$packageName.MainActivity$suffix"
                
                val newState = if (targetClassName == aliasName) {
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                } else {
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                }
                
                pm.setComponentEnabledSetting(
                    ComponentName(packageName, aliasName),
                    newState,
                    PackageManager.DONT_KILL_APP
                )
            }

            SharedObject.icon = targetClassName
            return@Function name

        } catch (e: Exception) {
            return@Function false
        }
    }

    Function("getAppIcon") {
      var componentClass:String = currentActivity.getComponentName().getClassName()

      var currentIcon:String = if(!SharedObject.icon.isEmpty()) SharedObject.icon else componentClass
      
      var currentIconName:String = currentIcon.split("MainActivity")[1]

      return@Function if(currentIconName.isEmpty()) "DEFAULT" else currentIconName
    }
  }

  private val context: Context
    get() = requireNotNull(appContext.reactContext) { "React Application Context is null" }
  
  private val currentActivity
    get() = requireNotNull(appContext.activityProvider?.currentActivity)

  private val pm
    get() = requireNotNull(currentActivity.packageManager)

}
