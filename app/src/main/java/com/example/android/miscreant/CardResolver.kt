/*
 * Copyright (c) 2019. Birgit Schoenauer
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-NonDerivatives 4.0 International License.
 * See file 'LICENSE.md' or https://creativecommons.org/licenses/by-nc-nd/4.0/ for full license details.
 */

package com.example.android.miscreant

import com.example.android.miscreant.Enums.Area
import com.example.android.miscreant.Enums.CardType
import com.example.android.miscreant.Enums.Location
import com.example.android.miscreant.models.Card
import com.example.android.miscreant.models.ImpactOutput
import com.example.android.miscreant.models.SelectedCard
import com.example.android.miscreant.viewmodels.GameViewModel
import java.security.InvalidParameterException

class CardResolver {

    // 1st monster: -> hero / -> equipped (left damage on hero)
    // 1st potion on equipped -> gain health
    // 1st weapon -> kill, overkill, equip
    // 1st shield -> only equip, discard, backpack
    // 1st backpack -> only equip
    // location backpack, discard, equip on empty
    fun showImpact(firstCard: Card, secondCard: Card, currentHealth: Int = -1, currentMaxHealth: Int = -1): ImpactOutput {
        if (firstCard.type == CardType.hero){
            return showHeroSpecial(firstCard, secondCard, true)
        }

        // first card moving somewhere -> look inactive
        firstCard.isLookActive = false

        // discarding
        if (secondCard.location == Location.discard){
            firstCard.showConsumed = true
            firstCard.showHealth = false
            return ImpactOutput(firstCard, secondCard)
        }

        // to or from backpack
        if (firstCard.location == Location.backpack || secondCard.location == Location.backpack) {
            // using potion
            if (firstCard.type == CardType.potion && secondCard.location != Location.backpack){
                return potionImpact(firstCard, secondCard, currentHealth, currentMaxHealth,true)
            }

            firstCard.showEquip = true
            secondCard.showEquip = true
            return ImpactOutput(firstCard, secondCard)
        }

        // equip shield
        if (firstCard.type == CardType.shield){
            firstCard.showEquip = true
            secondCard.showEquip = true
            return ImpactOutput(firstCard, secondCard)
        }

        return when (firstCard.type){
            CardType.potion -> potionImpact(firstCard, secondCard, currentHealth, currentMaxHealth, true)
            CardType.weapon -> weaponImpactVisual(firstCard, secondCard, currentMaxHealth)
            CardType.monster -> monsterImpactVisual(firstCard, secondCard, currentHealth)
            else -> throw InvalidParameterException("Type of first selected Card not valid ${firstCard.type}")
        }
    }

    fun resolveCardAction(firstSelected: SelectedCard, firstCard: Card, secondCard: Card, currentHealth: Int = -1, currentMaxHealth: Int = -1): ImpactOutput {
        if (firstCard.type == CardType.hero){
            return showHeroSpecial(firstCard, secondCard, false)
        }

        // store card in backpack
        if (secondCard.location == Location.backpack){
            val firstCardLocation = firstCard.location
            firstCard.location = Location.backpack
            return ImpactOutput(firstCard = Card(location = firstCardLocation), secondCard = firstCard)
        }

        // discarding
        if (secondCard.location == Location.discard){
            return ImpactOutput(Card(location = firstCard.location), secondCard)
        }

        // use potion
        if (firstCard.type == CardType.potion){
            return potionImpact(firstCard, secondCard, currentHealth, currentMaxHealth)
        }

        return when (firstSelected.inArea){
            Area.equipped -> useEquippedWeapon(firstCard, secondCard, currentMaxHealth) // weapon equipped (equipped shield can't be selected)
            Area.dungeon -> takeDungeonCard(firstCard, secondCard, currentHealth) // dungeon - potion, shield, weapon, monster
            Area.backpack -> {  // shield, weapon
                firstCard.location = secondCard.location
                return ImpactOutput(Card(location = Location.backpack), firstCard)
            }
            else -> throw InvalidParameterException("Firstcard in invalid area ${firstSelected.inArea}," +
                    " secondCardLocation ${secondCard.location}")
        }
    }

    private fun showHeroSpecial(firstCard: Card, secondCard: Card, visualizeOnly: Boolean): ImpactOutput {
        if (visualizeOnly){
            secondCard.showHealth = false
            secondCard.showPotentialHealth = true
            secondCard.potentialHealth = secondCard.health + 1

            return ImpactOutput(firstCard = firstCard, secondCard = secondCard, specialUsed = true, potentialSpecialUse = true)
        }
        else {
            secondCard.health = secondCard.health + 1
            return ImpactOutput(firstCard = firstCard, secondCard = secondCard, specialUsed = true)
        }
    }

    // case potion to backpack handled before this call
    private fun potionImpact(firstCard: Card, secondCard: Card, currentHealth: Int, currentMaxHealth: Int, visualizeOnly: Boolean = false): ImpactOutput {
        val newHealth = if (currentHealth + firstCard.health > currentMaxHealth) currentMaxHealth
                            else currentHealth + firstCard.health

        firstCard.showEquip = true
        secondCard.showEquip = true

        val output = ImpactOutput(firstCard = firstCard, secondCard = secondCard, currentHealth = newHealth)

        if (visualizeOnly){
            // case from backpack or used from dungeon
            output.showPotentialHealth = true
            output.currentHealth = newHealth
            output.firstCard.showHealth = false
            return output
        }
        else {
            output.firstCard = Card(location = firstCard.location)
            output.currentHealth = newHealth
        }
        return output
    }

    private fun weaponImpactVisual(firstCard: Card, secondCard: Card, currentMaxHealth: Int): ImpactOutput {
        // weapon disabled in any case
        firstCard.showHealth = false
        secondCard.showHealth = false

        val output = ImpactOutput(firstCard = firstCard, secondCard = secondCard)

        // simply equipping
        if (secondCard.type != CardType.monster){
            output.firstCard.showEquip = true
            output.secondCard.showEquip = true
            return output
        }

        val weaponValue = firstCard.health
        val monsterHealth = secondCard.health
        val monsterSurvives = monsterHealth - weaponValue > 0

        // weapon is consumed
        output.firstCard.showConsumed = true

        if (monsterSurvives){
            output.secondCard.potentialHealth = monsterHealth - weaponValue
            output.secondCard.showPotentialHealth = true
        }
        else {
            output.secondCard.isLookActive = false
            output.secondCard.showRIP = true

            // overkill
            if (monsterHealth - weaponValue < 0){
                output.maxHealth = currentMaxHealth + 1
                output.showPotentialMaxHealth = true
            }
        }
        return output
    }

    private fun monsterImpactVisual(firstCard: Card, secondCard: Card, currentHealth: Int): ImpactOutput {
        // monster dies
        firstCard.showHealth = false
        firstCard.showRIP = true

        val attackOutcome = if (secondCard.type == CardType.hero) currentHealth - firstCard.health
                                else secondCard.health - firstCard.health
        val output = ImpactOutput(firstCard = firstCard, secondCard = secondCard)

        // second card can be hero, weapon or shield
        if (secondCard.type == CardType.hero){
            if (attackOutcome > 0){
                output.currentHealth = attackOutcome
                output.showPotentialHealth = true
            }
            else{
                output.secondCard.isLookActive = false
                output.secondCard.showRIP = true
            }
            return output
        }

        if (attackOutcome > 0){
            output.secondCard.potentialHealth = attackOutcome
            output.secondCard.showPotentialHealth = true
            output.secondCard.showHealth = false
        }
        else { // rest damage hits hero
            output.secondCard.isLookActive = false
            output.secondCard.showHealth = false
            output.secondCard.showConsumed = true

            if (attackOutcome < 0){
                output.currentHealth = currentHealth + attackOutcome
                output.showPotentialHealth = true
            }
        }
        return output
    }

    private fun useEquippedWeapon(firstCard: Card, secondCard: Card, currentMaxHealth: Int): ImpactOutput {
        val output = ImpactOutput(firstCard = Card(location = firstCard.location), secondCard = secondCard)

        val weaponValue = firstCard.health
        val monsterHealth = secondCard.health
        val monsterSurvives = monsterHealth - weaponValue > 0

        if (monsterSurvives){
            output.secondCard.health = monsterHealth - weaponValue
        }
        else {
            output.secondCard = Card(location = secondCard.location)

            // overkill
            if (monsterHealth - weaponValue < 0) {
                output.maxHealth = currentMaxHealth + 1
            }
        }
        return output
    }

    // either card being equipped or monster (other cases already handled)
    private fun takeDungeonCard(firstCard: Card, secondCard: Card, currentHealth: Int): ImpactOutput {
        if (firstCard.type != CardType.monster){
            val firstLocation = firstCard.location
            firstCard.location = secondCard.location
            return ImpactOutput(firstCard = Card(location = firstLocation), secondCard = firstCard)
        }

        // monster
        val monsterValue = firstCard.health
        val attackResult = if (secondCard.type == CardType.hero) currentHealth - monsterValue
                                else secondCard.health - monsterValue

        val output = ImpactOutput(firstCard = Card(location = firstCard.location), secondCard = secondCard)

        if (secondCard.type == CardType.hero){
            output.currentHealth = attackResult

            if (attackResult < 0){
                output.secondCard.showRIP = true
            }
            return output
        }

        // monster on shield or weapon
        if (attackResult > 0){
            output.secondCard.health = attackResult
        }
        else {
            output.secondCard = Card(location = secondCard.location)

            // left over damage dealt to hero
            if (attackResult < 0){
                output.currentHealth = currentHealth + attackResult
            }
        }
        return output
    }
}