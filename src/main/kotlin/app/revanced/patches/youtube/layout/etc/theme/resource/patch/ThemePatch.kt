package app.revanced.patches.youtube.layout.etc.theme.resource.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.shared.annotation.YouTubeCompatibility
import app.revanced.patches.shared.patch.options.PatchOptions
import app.revanced.patches.youtube.layout.etc.theme.bytecode.patch.GeneralThemeBytecodePatch.Companion.isMonetPatchIncluded
import app.revanced.patches.youtube.misc.settings.resource.patch.SettingsPatch
import app.revanced.util.resources.ResourceHelper.updatePatchStatusTheme
import org.w3c.dom.Element

@Patch
@Name("theme")
@Description("Applies a custom theme (default: amoled).")
@DependsOn(
    [
        GeneralThemePatch::class,
        PatchOptions::class,
        SettingsPatch::class
    ]
)
@YouTubeCompatibility
@Version("0.0.1")
class ThemePatch : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {

        arrayOf("values", "values-v31").forEach {
            context.setTheme(it)
        }

        val currentTheme = if (isMonetPatchIncluded) "mix" else "amoled"

        context.updatePatchStatusTheme(currentTheme)

        return PatchResultSuccess()
    }
    private companion object {
        private fun ResourceContext.setTheme(valuesPath: String) {
            this.xmlEditor["res/$valuesPath/colors.xml"].use { editor ->
                val resourcesNode = editor.file.getElementsByTagName("resources").item(0) as Element

                for (i in 0 until resourcesNode.childNodes.length) {
                    val node = resourcesNode.childNodes.item(i) as? Element ?: continue

                    node.textContent = when (node.getAttribute("name")) {
                        "yt_black0", "yt_black1", "yt_black1_opacity95", "yt_black1_opacity98", "yt_black2", "yt_black3",
                        "yt_black4", "yt_status_bar_background_dark", "material_grey_850" -> PatchOptions.darkThemeBackgroundColor!!

                        else -> continue
                    }
                }
            }
        }
    }
}