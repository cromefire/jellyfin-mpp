package org.jellyfin.mpp.app

import org.jellyfin.mpp.common.hello
import kotlin.test.Test
import kotlin.test.assertTrue

class SampleTestsAndroid {
    @Test
    fun testHello() {
        assertTrue("Android" in hello())
    }
}