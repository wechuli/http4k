package tutorials.hexagonal._1.tests

import tutorials.hexagonal._1.calculator.CalculatorHub

object HubCalculatorTest : CalculatorContract {
    override val calculator = CalculatorHub()
}
