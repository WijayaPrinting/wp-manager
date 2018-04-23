package com.hendraanggrian.openpss.controls

import com.hendraanggrian.openpss.scene.NodeTest
import com.hendraanggrian.openpss.scene.controls.IntField
import org.junit.Test
import kotlin.test.assertEquals

class IntFieldTest : NodeTest<IntField>() {

    override fun newInstance() = IntField()

    @Test fun setText() {
        node.text = "12"
        assertEquals(node.value, 12)
    }

    @Test fun setValue() {
        node.value = 21
        assertEquals(node.text, "21")
    }
}