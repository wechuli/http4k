package tutorials.hexagonal._1.tests

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import tutorials.hexagonal._1.calculator.Calculator

interface CalculatorContract {
    val calculator: Calculator

    @Test
    fun `can reset calculation`() {
        assertThat(calculator.reset(), equalTo(0))
    }
}
