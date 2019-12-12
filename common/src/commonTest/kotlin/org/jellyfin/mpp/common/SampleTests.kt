package org.jellyfin.mpp.common

import org.jellyfin.mpp.common.Sample
import kotlin.test.Test
import kotlin.test.assertTrue

class SampleTests {
    @Test
    fun testMe() {
        assertTrue(Sample().checkMe() > 0)
    }
}