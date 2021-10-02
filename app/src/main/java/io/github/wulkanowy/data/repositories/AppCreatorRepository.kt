package io.github.wulkanowy.data.repositories

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.data.pojos.Contributor
import io.github.wulkanowy.utils.DispatchersProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppCreatorRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatchers: DispatchersProvider,
    private val moshi: Moshi
) {

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun getAppCreators() = withContext(dispatchers.backgroundThread) {
        val type = Types.newParameterizedType(List::class.java, Contributor::class.java)
        val adapter = moshi.adapter<List<Contributor>>(type)
        val json = context.assets.open("contributors.json").bufferedReader().use { it.readText() }

        return@withContext adapter.fromJson(json)
    }
}
