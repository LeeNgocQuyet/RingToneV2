package com.example.ringtonev2.util

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.savedstate.compose.LocalSavedStateRegistryOwner
import java.util.Locale


// Todo
val LocalAppLanguage = staticCompositionLocalOf<String?> { null }
object LanguageUtils {

    fun createLocalizedContext(context: Context, languageCode: String): Context {
        val locale = Locale.forLanguageTag(languageCode.ifBlank { Locale.getDefault().language })
        Locale.setDefault(locale)

        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        return context.createConfigurationContext(configuration)
    }
}
@Composable
fun AppLocaleProvider(
    currentLang: String?,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val savedStateRegistryOwner = LocalSavedStateRegistryOwner.current
    val activityResultRegistryOwner = LocalActivityResultRegistryOwner.current
    val currentActivity = LocalActivity.current

    if (currentLang != null) {
        val localeContext = remember(currentLang, context) {
            val baseLocaleContext = context.createLocaleContext(currentLang)

            object : ContextWrapper(baseLocaleContext) {
                override fun getBaseContext(): Context {
                    return context
                }
            }
        }

        val providers = buildList {
            add(LocalContext provides localeContext)
            add(LocalAppLanguage provides currentLang)
            add(LocalLifecycleOwner provides lifecycleOwner)
            add(LocalSavedStateRegistryOwner provides savedStateRegistryOwner)
            if (currentActivity != null) add(LocalActivity provides currentActivity)
            if (activityResultRegistryOwner != null) {
                add(LocalActivityResultRegistryOwner provides activityResultRegistryOwner)
            }
        }.toTypedArray()

        CompositionLocalProvider(*providers) { content() }
    } else {
        content()
    }
}
@Composable
fun DialogLocaleProvider(content: @Composable () -> Unit) {
    val lang = LocalAppLanguage.current
    val context = LocalContext.current
    val currentActivity = LocalActivity.current
    if (lang != null) {
        val localeContext = remember(lang, context) {
            context.createLocaleContext(lang)
        }
        CompositionLocalProvider(
            LocalContext provides localeContext,
            LocalActivity provides currentActivity
        ) {
            content()
        }
    } else {
        content()
    }
}

fun Context.createLocaleContext(languageCode: String): Context {
    // "in" là code cũ của Indonesia trong Java; resource folder dùng "in".
    val locale = Locale(languageCode)
    Locale.setDefault(locale)

    val config = Configuration(resources.configuration)
    config.setLocale(locale)
    config.setLayoutDirection(locale)

    return createConfigurationContext(config)
}
