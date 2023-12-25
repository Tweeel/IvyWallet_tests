package com.ivy.core.domain.algorithm.calc

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.ivy.core.persistence.algorithm.calc.CalcTrn
import com.ivy.data.transaction.TransactionType
import org.junit.jupiter.api.Test
import java.time.Instant

class RawStatsKtTest {

    @Test
    fun `add correct transactions`() {
        val tenSecondsAgo = Instant.now().minusSeconds(10)
        val fiveSecondsAgo = Instant.now().minusSeconds(5)
        val threeSecondsAgo = Instant.now().minusSeconds(3)
        val trns = listOf(
            CalcTrn(10.0, "USD", TransactionType.Income, tenSecondsAgo),
            CalcTrn(10.0, "EURO", TransactionType.Expense, threeSecondsAgo),
            CalcTrn(10.0, "USD", TransactionType.Expense, fiveSecondsAgo)
        )
        val rawStats = rawStats(trns)
        assertThat(rawStats.expensesCount).isEqualTo(2)
        assertThat(rawStats.newestTrnTime).isEqualTo(threeSecondsAgo)
        assertThat(rawStats.expenses).isEqualTo(mapOf("USD" to 10.0, "EURO" to 10.0))
        assertThat(rawStats.expenses["USD"]).isEqualTo(10.0)
        assertThat(rawStats.expenses["EURO"]).isEqualTo(10.0)

        assertThat(rawStats.incomesCount).isEqualTo(1)
        assertThat(rawStats.incomes).isEqualTo(mapOf("USD" to 10.0))
        assertThat(rawStats.incomes["USD"]).isEqualTo(10.0)
    }

    @Test
    fun `add empty transactions`() {
        val trns = listOf<CalcTrn>()
        val rawStats = rawStats(trns)
        assertThat(rawStats.incomesCount).isEqualTo(0)
        assertThat(rawStats.expensesCount).isEqualTo(0)
        assertThat(rawStats.incomes.count()).isEqualTo(0)
        assertThat(rawStats.expenses.count()).isEqualTo(0)
        assertThat(rawStats.incomes["USD"]).isEqualTo(null)
        assertThat(rawStats.incomes["EURO"]).isEqualTo(null)
    }

}