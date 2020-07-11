package io.github.wulkanowy.data.repositories.appcreator

import android.content.res.AssetManager
import com.google.gson.Gson
import io.github.wulkanowy.data.pojos.Contributor
import io.github.wulkanowy.utils.DispatchersProvider
import io.github.wulkanowy.utils.flowWithResource
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppCreatorRepository @Inject constructor(
    private val assets: AssetManager,
    private val dispatchers: DispatchersProvider
) {

    fun getAppCreators() = flowWithResource {
        withContext(dispatchers.backgroundThread) {
            Gson().fromJson(
                assets.open("contributors.json").bufferedReader().use { it.readText() },
                Array<Contributor>::class.java
            ).toList()
        }
    }
}
