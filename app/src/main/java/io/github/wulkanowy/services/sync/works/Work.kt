package io.github.wulkanowy.services.sync.works

import io.reactivex.Completable

interface Work {

    fun create(): Completable
}

