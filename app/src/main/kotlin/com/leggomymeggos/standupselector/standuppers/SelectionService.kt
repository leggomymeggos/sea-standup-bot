package com.leggomymeggos.standupselector.standuppers

import com.leggomymeggos.standupselector.selector.RandomNumberProvider
import org.springframework.stereotype.Service

@Service
class SelectionService(
    private val randomNumberProvider: RandomNumberProvider
) {
    fun selectStanduppersByProbability(standuppers: List<Standupper>): List<Standupper> {
        val forceSelectedStanduppers = standuppers.filter { it.isForceSelected }

        return if (forceSelectedStanduppers.size >= 2) forceSelectedStanduppers
        else {
            selectStanduppers(forceSelectedStanduppers, standuppers)
        }.let {
            if (it.size >= 2) it.subList(0, 2)
            else it
        }
    }

    private fun selectStanduppers(
        forceSelectedStanduppers: List<Standupper>,
        standuppers: List<Standupper>
    ): List<Standupper> {
        val selectedStanduppers = forceSelectedStanduppers.toMutableList()
        val nonForceSelectedStanduppers =
            standuppers.minus(forceSelectedStanduppers).filterNot { it.isForceOmitted }

        var totalWeight = nonForceSelectedStanduppers.sumByDouble { it.selectionProbability }

        while (selectedStanduppers.size < 2) {
            nonForceSelectedStanduppers.forEach { standupper ->
                if (standupper.shouldBeSelected(totalWeight)) {
                    selectedStanduppers.add(standupper)
                }
                totalWeight -= standupper.selectionProbability
            }
        }

        return selectedStanduppers.distinct()
    }

    private fun Standupper.shouldBeSelected(totalWeight: Double): Boolean {
        return randomNumberProvider.get() < (selectionProbability / totalWeight)
    }
}