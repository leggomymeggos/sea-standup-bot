package com.leggomymeggos.standupselector.standuppers

import com.leggomymeggos.standupselector.selector.RandomNumberProvider
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class SelectionServiceTest {
    private val randomNumberProvider = mock<RandomNumberProvider> {
        on { get() } doReturn 0.01
    }

    private val subject = SelectionService(randomNumberProvider)

    // region force omission - standuppers that are force omitted are always omitted
    @Test
    fun `pickStanduppers ignores standuppers that are force omitted`() {
        val bojack = Standupper(
            slackName = "bojack",
            email = "bojack@horseman.com",
            isForceSelected = false,
            isForceOmitted = true,
            selectionProbability = 1.0
        )
        val carolyn = Standupper(
            slackName = "carolyn",
            email = "carolyn@princess.com",
            isForceSelected = false,
            isForceOmitted = false,
            selectionProbability = 1.0
        )
        val diane = Standupper(
            slackName = "diane",
            email = "dnguyen@publishing.com",
            isForceSelected = false,
            isForceOmitted = false,
            selectionProbability = 1.0
        )

        for (i in 0..1000) {
            val selected = subject.selectStanduppersByProbability(listOf(bojack, carolyn, diane))
            assertThat(selected).hasSize(2)
            assertThat(selected).containsAnyOf(carolyn, diane)
            assertThat(selected).doesNotContain(bojack)
        }
    }

    @Test
    fun `pickStanduppers - force selection overrides force omission`() {
        val bojack = Standupper(
            slackName = "bojack",
            email = "bojack@horseman.com",
            isForceSelected = true,
            isForceOmitted = true,
            selectionProbability = 0.0
        )
        val carolyn = Standupper(
            slackName = "carolyn",
            email = "carolyn@princess.com",
            isForceSelected = true,
            isForceOmitted = true,
            selectionProbability = 0.0
        )

        for (i in 0..1000) {
            val selected = subject.selectStanduppersByProbability(listOf(bojack, carolyn))
            assertThat(selected).hasSize(2)
            assertThat(selected).containsAnyOf(carolyn, bojack)
        }
    }

    @Test
    fun `pickStanduppers returns less than 2 standuppers if most are force omitted`() {
        val bojack = Standupper(
            slackName = "bojack",
            email = "bojack@horseman.com",
            isForceSelected = false,
            isForceOmitted = true,
            selectionProbability = 1.0
        )
        val carolyn = Standupper(
            slackName = "carolyn",
            email = "carolyn@princess.com",
            isForceSelected = false,
            isForceOmitted = true,
            selectionProbability = 1.0
        )
        val diane = Standupper(
            slackName = "diane",
            email = "dnguyen@publishing.com",
            isForceSelected = false,
            isForceOmitted = false,
            selectionProbability = 1.0
        )

        for (i in 0..1000) {
            val selected = subject.selectStanduppersByProbability(listOf(bojack, carolyn, diane))
            assertThat(selected).hasSize(1)
            assertThat(selected).contains(diane)
            assertThat(selected).doesNotContain(bojack, carolyn)
        }
    }
    // endregion

    // region probability fallback
    @Test
    fun `pickStanduppers selects standuppers by probability`() {
        val bojack = Standupper(
            slackName = "bojack",
            email = "bojack@horseman.com",
            isForceSelected = false,
            isForceOmitted = false,
            selectionProbability = 60.0
        )
        val carolyn = Standupper(
            slackName = "carolyn",
            email = "carolyn@princess.com",
            isForceSelected = false,
            isForceOmitted = false,
            selectionProbability = 20.0
        )
        val diane = Standupper(
            slackName = "diane",
            email = "dnguyen@publishing.com",
            isForceSelected = false,
            isForceOmitted = false,
            selectionProbability = 10.0
        )
        val peanutButter = Standupper(
            slackName = "peanutButter",
            email = "pb@dogs.com",
            isForceSelected = false,
            isForceOmitted = false,
            selectionProbability = 5.0
        )

        // totalWeight = 95

        whenever(randomNumberProvider.get()).thenReturn(0.5)

        val selectedStanduppers = mutableListOf<Standupper>()
        for (i in 0..1000) {
            val selected = subject.selectStanduppersByProbability(listOf(bojack, carolyn, diane, peanutButter))
            assertThat(selected).hasSize(2)
            selectedStanduppers.addAll(selected)
        }

        val bojackAppearances = selectedStanduppers.filter { it.slackName == "bojack" }.count()
        val carolynAppearances = selectedStanduppers.filter { it.slackName == "carolyn" }.count()
        val dianeAppearances = selectedStanduppers.filter { it.slackName == "diane" }.count()
        val peanutButterAppearances = selectedStanduppers.filter { it.slackName == "peanutButter" }.count()

        assertThat(bojackAppearances).`as`("Bojack is selected more often than Carolyn")
            .isGreaterThanOrEqualTo(carolynAppearances)
        assertThat(bojackAppearances).`as`("Bojack is selected more often than Diane")
            .isGreaterThanOrEqualTo(dianeAppearances)
        assertThat(bojackAppearances).`as`("Bojack is selected more often than Peanut Butter")
            .isGreaterThanOrEqualTo(peanutButterAppearances)

        assertThat(carolynAppearances).`as`("Carolyn is selected more often than Diane")
            .isGreaterThanOrEqualTo(dianeAppearances)
        assertThat(carolynAppearances).`as`("Carolyn is selected more often than Peanut Butter")
            .isGreaterThanOrEqualTo(peanutButterAppearances)

        assertThat(dianeAppearances).`as`("Diane is selected more often than Peanut Butter")
            .isGreaterThanOrEqualTo(peanutButterAppearances)
    }
    // endregion
}