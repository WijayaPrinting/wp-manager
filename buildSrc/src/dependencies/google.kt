const val VERSION_GSON = "2.8.5"
const val VERSION_GUAVA = "28.1"

fun Dependencies.google(repo: String? = null, module: String, version: String) =
    "com.google${repo?.let { ".$it" }.orEmpty()}:$module:$version"