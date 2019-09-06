/*
 * Copyright (c) 2019. Birgit Schoenauer
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-NonDerivatives 4.0 International License.
 * See file 'LICENSE.md' or https://creativecommons.org/licenses/by-nc-nd/4.0/ for full license details.
 */

package com.example.android.miscreant.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.miscreant.models.Card
import com.example.android.miscreant.models.GameData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.IllegalArgumentException

class GameViewModel(context: Context?) : ViewModel() {

    private val context: Context = context ?: throw IllegalArgumentException("${this.javaClass.simpleName}: context is NULL" )
    // todo combine with map -> difficulty keys
    private val deckPaths: List<String> = listOf("testdeck.json")

    private var gameData = GameData()
    private var currentDeck: MutableList<Card> = mutableListOf()

    // region name dungeon area cards
    private var _cardLeftBack = MutableLiveData<Card>()
    val cardLeftBack : LiveData<Card>
        get() = _cardLeftBack

    private var _cardMiddleBack = MutableLiveData<Card>()
    val cardMiddleBack : LiveData<Card>
        get() = _cardMiddleBack

    private var _cardRightBack = MutableLiveData<Card>()

    val cardRightBack : LiveData<Card>
        get() = _cardRightBack

    private var _cardLeftFront = MutableLiveData<Card>()
    val cardLeftFront : LiveData<Card>
        get() = _cardLeftFront

    private var _cardMiddleFront = MutableLiveData<Card>()
    val cardMiddleFront : LiveData<Card>
        get() = _cardMiddleFront

    private var _cardRightFront = MutableLiveData<Card>()
    val cardRightFront : LiveData<Card>
        get() = _cardRightFront

    // endregion

    // region name hero area cards
    private var _cardEquipLeft = MutableLiveData<Card>()
    val cardEquipLeft : LiveData<Card>
        get() = _cardEquipLeft

    private var _cardHero = MutableLiveData<Card>()
    val cardHero : LiveData<Card>
        get() = _cardHero

    private var _cardEquipRight = MutableLiveData<Card>()
    val cardEquipRight : LiveData<Card>
        get() = _cardEquipRight

    private var _cardDiscard = MutableLiveData<Card>()
    val cardDiscard : LiveData<Card>
        get() = _cardDiscard

    private var _cardBackpack = MutableLiveData<Card>()
    val cardBackpack: LiveData<Card>
        get() = _cardBackpack
    // endregion

    // region name displayed values on game screen
    private var _cardsLeftInDeck = MutableLiveData<Int>()
    val cardsLeftInDeck  : LiveData<Int>
        get() = _cardsLeftInDeck

    private var _heroMaxHealth = MutableLiveData<Int>()
    val heroMaxHealth  : LiveData<Int>
        get() = _heroMaxHealth

    private var _heroCurrentHealth = MutableLiveData<Int>()
    val heroCurrentHealth  : LiveData<Int>
        get() = _heroCurrentHealth
    // endregion

    // todo no hardcoded strings
    // region name dungeonCardsMap
    private var dungeonMap: MutableMap<String, MutableLiveData<Card>> =
    mutableMapOf(
        "card_dungeon_left_back" to _cardLeftBack,
        "card_dungeon_middle_back" to _cardMiddleBack,
        "card_dungeon_right_back" to _cardRightBack,
        "card_dungeon_left_front" to _cardLeftFront,
        "card_dungeon_middle_front" to _cardMiddleFront,
        "card_dungeon_right_front" to _cardRightFront
    )
    // endregion

    // region name heroCardsMap
    var heroMap: MutableMap<String, MutableLiveData<Card>> =
        mutableMapOf(
            "card_equip_left" to _cardEquipLeft,
            "card_equip_right" to _cardEquipRight,
            "card_backpack" to _cardBackpack,
            "card_hero" to _cardHero,
            "card_discard" to _cardDiscard
        )
    // endregion

    init {
        // todo maybe load all decks at start

        // initialize start values
        _heroCurrentHealth.value = gameData.heroStartHealth
        _heroMaxHealth.value = gameData.heroStartHealth
        _cardsLeftInDeck.value = currentDeck.size

        // initialize empty dungeon cards
        _cardLeftBack.value = Card()
        _cardMiddleBack.value = Card()
        _cardRightBack.value = Card()
        _cardLeftFront.value = Card()
        _cardMiddleFront.value = Card()
        _cardRightFront.value = Card()

    }

    fun startGame(){
        // todo: check, if still running
        // todo: if running, check if monster attack
        if (currentDeck.isEmpty()){
            val deckPath = deckPaths[gameData.currentDeckNumber]
            currentDeck = getDeck(deckPath)
            // todo check decksize
        }

        // todo: ensure after game end - in scoring all left dungeon cards removed -> points
        // todo: ensure progress currentDeckNumber in GameData @gameEnd
        dealCards()
        // update nr cardsLeft/in Deck
    }

    // todo later 3 decks per playthrough
    // better throw exception -> no game possible w/o deck
    private fun getDeck(deckPath : String) : MutableList<Card> {

        val bufferedReader = context.assets.open(deckPath).bufferedReader()
        val jsonString = bufferedReader.use{ it.readText() } //read and store in string

        val type = object : TypeToken<MutableList<Card>>(){}.type
        return Gson().fromJson(jsonString, type)
    }

    private fun dealCards(){
        // todo include check if deck empty: scoring etc.

        // fill dungeon
        dungeonMap.forEach { (key, card)->
            if (card.value?.isEmpty() ?: return@forEach){
                if (currentDeck.isNotEmpty()){
                    card.value = currentDeck[0]
                    currentDeck.removeAt(0)
                }
                else moveBackrowCardsToDungeonFront()
            }
        }
    }

    private fun moveBackrowCardsToDungeonFront(){
        _cardLeftFront.value?.let {
            if (!it.isEmpty()) {
                moveFromToCard(_cardLeftBack, _cardLeftFront)
            }
        }
        
        _cardMiddleFront.value?.let {
            if (!it.isEmpty()) {
                moveFromToCard(_cardMiddleBack, _cardMiddleFront)
            }
        }

        _cardRightFront.value?.let {
            if (!it.isEmpty()) {
                moveFromToCard(_cardRightBack, _cardRightFront)
            }
        }

        // todo monster counter attack
    }

    private fun moveFromToCard(provideCard: MutableLiveData<Card>, receiveCard: MutableLiveData<Card>){
        receiveCard.value = provideCard.value
        provideCard.value = Card()
    }
}