const val VERSION_KOTLIN: String = "1.3.50"
const val VERSION_COROUTINES: String = "1.3.2"
const val VERSION_NOSQL = "0.1-SNAPSHOT"

fun Dependencies.kotlinx(module: String, version: String? = null) =
    "org.jetbrains.kotlinx:kotlinx-$module${version?.let { ":$it" }.orEmpty()}"