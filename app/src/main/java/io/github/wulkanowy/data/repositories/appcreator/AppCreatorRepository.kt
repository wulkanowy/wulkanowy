package io.github.wulkanowy.data.repositories.appcreator

import android.content.res.AssetManager
import com.google.gson.Gson
import io.github.wulkanowy.data.pojos.AppCreator
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppCreatorRepository @Inject constructor(
    private val assets: AssetManager
) {
    val appCreators : List<AppCreator>
        get() = Gson().fromJson(
                assets.open("creators.json").bufferedReader().use { it.readText() },
                Array<AppCreator>::class.java
            ).toList()
}