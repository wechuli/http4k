package tutorials.hexagonal._1.tests

import tutorials.hexagonal._1.calculator.CalculatorApp

object InMemoryHttpTest : CalculatorContract {
    override val calculator = HttpCalculator(CalculatorApp())
}
