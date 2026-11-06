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
    primary = Color.GolestanAccentBlue,
    secondary = Color.GolestanAccentPurple,
    tertiary = Color.Pink80,
    background = Color.GolestanDarkSurface,
    surface = Color.GolestanDarkCard,
    onPrimary = Color.GolestanTextPrimaryDark,
    onSecondary = Color.GolestanTextPrimaryDark,
    onBackground = Color.GolestanTextPrimaryDark,
    onSurface = Color.GolestanTextPrimaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = Color.GolestanAccentBlue,
    secondary = Color.GolestanAccentPurple,
    tertiary = Color.Pink40,
    background = Color.GolestanLightSurface,
    surface = Color.GolestanLightCard,
    onPrimary = Color.GolestanLightSurface,
    onSecondary = Color.GolestanLightSurface,
    onBackground = Color.GolestanTextPrimaryLight,
    onSurface = Color.GolestanTextPrimaryLight
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
