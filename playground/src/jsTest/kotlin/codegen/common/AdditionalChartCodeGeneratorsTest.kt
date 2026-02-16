package codegen.common

import kotlin.test.Test
import kotlin.test.assertEquals

class AdditionalChartCodeGeneratorsTest {
    @Test
    fun kotlin_string_escaping_escapes_dollar_sign() {
        assertEquals("\\$", escapeKotlinString("$"))
        assertEquals("\\$" + "{value}", escapeKotlinString("$" + "{value}"))
    }
}
