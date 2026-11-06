package ir.kerman.kaveh.golestan.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = GolestanAccentBlue,
    secondary = GolestanAccentPurple,
    tertiary = Pink80,
    background = GolestanDarkSurface,
    surface = GolestanDarkCard,
    onPrimary = GolestanTextPrimaryDark,
    onSecondary = GolestanTextPrimaryDark,
    onBackground = GolestanTextPrimaryDark,
    onSurface = GolestanTextPrimaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = GolestanAccentBlue,
    secondary = GolestanAccentPurple,
    tertiary = Pink40,
    background = GolestanLightSurface,
    surface = GolestanLightCard,
    onPrimary = GolestanLightSurface,
    onSecondary = GolestanLightSurface,
    onBackground = GolestanTextPrimaryLight,
    onSurface = GolestanTextPrimaryLight
)

@Composable
fun GolestanLauncherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
