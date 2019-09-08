/*
 * Copyright (c) 2019. Birgit Schoenauer
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-NonDerivatives 4.0 International License.
 * See file 'LICENSE.md' or https://creativecommons.org/licenses/by-nc-nd/4.0/ for full license details.
 */

package com.example.android.miscreant.viewmodels

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.miscreant.*
import com.example.android.miscreant.Enums.*
import com.example.android.miscreant.models.Card
import com.example.android.miscreant.models.SelectedCard
import com.example.android.miscreant.models.Settings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.IllegalArgumentException

class GameViewModel(context: Context?) : ViewModel() {

    // needed for loading decks
    private val context: Context = context ?: throw IllegalArgumentException("${this.javaClass.simpleName}: context is NULL" )

    // todo each difficulty consists of 3 different decks
    private val deckPaths : Map<Difficulty, List<String>> = mapOf(
        Difficulty.easy to listOf(
            context?.getString(R.string.easy_deck_path).orEmpty(),
            context?.getString(R.string.easy_deck_path).orEmpty(),
            context?.getString(R.string.easy_deck_path).orEmpty()),
        Difficulty.normal to listOf(
            context?.getString(R.string.easy_deck_path).orEmpty(),
            context?.getString(R.string.easy_deck_path).orEmpty(),
            context?.getString(R.string.easy_deck_path).orEmpty()),
        Difficulty.hard to listOf(
            context?.getString(R.string.easy_deck_path).orEmpty(),
            context?.getString(R.string.easy_deck_path).orEmpty(),
            context?.getString(R.string.easy_deck_path).orEmpty())
    )

    private lateinit var gameSettings: Settings

    private var _currentDeckNumber = MutableLiveData<Int>()
    val currentDeckNumber : LiveData<Int>
        get() = _currentDeckNumber

    private var activeDeck: MutableList<Card> = mutableListOf()
    private var firstSelectedCard = SelectedCard()
    private var secondSelectedCard= SelectedCard()

    // region dungeon area cards
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

    // region hero area cards
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

    // region highlighted cards
    private var highlightedCards = mutableListOf<MutableLiveData<Card>>()
    // end region

    // region displayed values on game screen
    private var _cardsLeftInDeck = MutableLiveData<Int>()
    val cardsLeftInDeck  : LiveData<Int>
        get() = _cardsLeftInDeck

    private var _heroMaxHealth = MutableLiveData<Int>()
    val heroMaxHealth  : LiveData<Int>
        get() = _heroMaxHealth

    private var _heroCurrentHealth = MutableLiveData<Int>()
    val heroCurrentHealth  : LiveData<Int>
        get() = _heroCurrentHealth

    private var _specialsUsed = MutableLiveData<Int>()
    val specialsUsed  : LiveData<Int>
        get() = _specialsUsed

    var heroSpecial  : String = ""
        private set
    // endregion

    // region dungeonCardsMap
    private var dungeonMap: MutableMap<Location, MutableLiveData<Card>> =
    mutableMapOf(
        Location.dungeon_left_back to _cardLeftBack,
        Location.dungeon_middle_back to _cardMiddleBack,
        Location.dungeon_right_back to _cardRightBack,
        Location.dungeon_left_front to _cardLeftFront,
        Location.dungeon_middle_front to _cardMiddleFront,
        Location.dungeon_right_front to _cardRightFront
    )
    // endregion

    // region heroCardsMap
    var heroMap: MutableMap<Location, MutableLiveData<Card>> =
        mutableMapOf(
            Location.equip_left to _cardEquipLeft,
            Location.equip_right to _cardEquipRight,
            Location.backpack to _cardBackpack,
            Location.hero to _cardHero,
            Location.discard to _cardDiscard
        )
    // endregion

    init {
        // init empty dungeon cards
        _cardLeftBack.value = Card()
        _cardMiddleBack.value = Card()
        _cardRightBack.value = Card()
        _cardLeftFront.value = Card()
        _cardMiddleFront.value = Card()
        _cardRightFront.value = Card()

        // init empty hero area cards
        _cardEquipLeft.value = Card()
        _cardHero.value = Card()
        _cardEquipRight.value = Card()
        _cardBackpack.value = Card()
        _cardDiscard.value = Card()
    }

    fun initializeGameSettings(difficulty: Difficulty, heroName: String, hero: Hero){
        if (!::gameSettings.isInitialized){
            gameSettings = Settings(difficulty, heroName, hero)
        }

        // initialize start values
        _heroCurrentHealth.value = gameSettings.currentHealth
        _heroMaxHealth.value = gameSettings.currentMaxHealth
        _cardsLeftInDeck.value = if (activeDeck.isEmpty()) 0 else activeDeck.size

        // set hero stuff
        _cardHero.value = gameSettings.getHeroCard()
        _cardHero.value?.location = Location.hero
        _specialsUsed.value = gameSettings.usedSpecials
        heroSpecial = if (hero == Hero.archer) context.getString(R.string.archer_power)
                      else context.getString(R.string.viking_power)

        // todo - consider: load all decks for difficulty @app start
    }

    fun startGame(){
        // todo: check, if still running
        // todo: if running, check if monster attack
        if (activeDeck.isEmpty()){
            if (deckPaths.containsKey(gameSettings.difficulty)){
                val deckNumber = _currentDeckNumber.value ?: 0

                val deckPath = deckPaths[gameSettings.difficulty]?.get(deckNumber).orEmpty()
                activeDeck = getDeck(deckPath)
            }

            // todo check decksize
        }

        // todo: ensure after game end - in scoring all left dungeon cards removed -> points
        // todo: ensure progress currentDeckNumber in Settings @gameEnd
        dealCards()
        // update nr cardsLeft/in Deck
    }

    fun singleTap(view: View){
        // testing
        val cardType = view.getCardTypeFromTag()
        val location = view.getCardLocationByName()

        if (firstSelectedCard.isEmtpy()){
            firstSelectedCard = SelectedCard(location, cardType)
            showDropLocations()
        } else {
            secondSelectedCard = SelectedCard(location, cardType)

            if (firstSelectedCard.location == secondSelectedCard.location){
                removeHighlights()
                resetSelectedCards()
            } else {
                // check if 2nd clicked card valid target
                if (highlightedCards.any{ it.value?.location == secondSelectedCard.location}){
                    showImpact()
                } else {
                    removeHighlights()
                    resetSelectedCards()
                }
            }
        }
    }

    fun doubleTap(view: View){
        if (firstSelectedCard.isEmtpy()){
            Log.i("DOUBLE TAP", "No card has been selected yet. Returning")
            return
        }

        // first card is selected - execute move/game action by double tap
        // todo deactivate highlighting
        // todo update card values
        // todo move cards
    }

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
            card.value?.let {
                if (it.isEmpty()){
                    if (activeDeck.isNotEmpty()){
                        card.value = activeDeck[0]
                        card.value?.location = key
                        card.postValue(card.value)
                        activeDeck.removeAt(0)
                    } else {
                        moveBackrowCardsToDungeonFront()
                    }
                } else {
                    return@forEach
                }
            }
        }
    }

    private fun moveBackrowCardsToDungeonFront(){
        _cardLeftFront.value?.let {
            if (!it.isEmpty()) {
                moveFromToCard(_cardLeftBack, _cardLeftFront)
                _cardLeftFront.value?.location = Location.dungeon_left_front
            }
        }

        _cardMiddleFront.value?.let {
            if (!it.isEmpty()) {
                moveFromToCard(_cardMiddleBack, _cardMiddleFront)
                _cardMiddleFront.value?.location = Location.dungeon_middle_front
            }
        }

        _cardRightFront.value?.let {
            if (!it.isEmpty()) {
                moveFromToCard(_cardRightBack, _cardRightFront)
                _cardRightFront.value?.location = Location.dungeon_right_front
            }
        }

        // todo monster counter attack
    }

    private fun moveFromToCard(from: MutableLiveData<Card>, to: MutableLiveData<Card>){
        to.value = from.value
        from.value = Card()
    }

    // region show drop locations
    // is called when a first card is selected
    private fun showDropLocations(){
        when(firstSelectedCard.area){
            Area.dungeon -> showDropLocationsForDungeonCard()
            Area.equipped -> showDropLocationsForEquippedCard()
            Area.backpack -> showDropLocationsForBackpackedCard()
            else -> return
        }
    }

    // discard, equip left, equip right
    private fun showDropLocationsForBackpackedCard() {
        heroMap.forEach { (location, card) ->
            card.value?.let {
                if (!it.isEmpty()) {
                    if (location != Location.backpack){
                        it.isHighlightOn = true
                        card.postValue(card.value) // not updating w/o postValue
                        highlightedCards.add(card)
                    }
                } else {
                    return@forEach
                }
            }
        }
    }

    // non-monster: if free: all on hero side (if empty)
    // monster: hero; equipped, if cards equipped
    private fun showDropLocationsForDungeonCard(){
        val isMonster = firstSelectedCard.type == CardType.monster

        heroMap.forEach{ (_, card) ->
            card.value?.let {
                if (isMonster){
                    if (it.type == CardType.backpack || it.type == CardType.discard || it.isEmpty()){
                        return@forEach
                    } else {
                        it.isHighlightOn = true
                        card.postValue(card.value)
                        highlightedCards.add(card)
                    }
                } else {
                    if (it.isEmpty()){
                        it.isHighlightOn = true
                        card.postValue(card.value)
                        highlightedCards.add(card)

                    } else {
                        return@forEach
                    }
                }
            }
        }
    }

    // dungeon front (unless archer), if monster card
    private fun showDropLocationsForEquippedCard(){
        val isArcher = gameSettings.hero == Hero.archer

        dungeonMap.forEach{ (location, card)->
            card.value?.let {
                if (it.type == CardType.monster){
                    if (location == Location.dungeon_left_front ||
                        location == Location.dungeon_middle_front ||
                        location == Location.dungeon_right_front) {
                        it.isHighlightOn = true
                        card.postValue(card.value)
                        highlightedCards.add(card)
                    } else {
                        if (isArcher) {
                            it.isHighlightOn = true
                            card.postValue(card.value)
                            highlightedCards.add(card)
                        }
                    }
                }
            }
        }
    }
    // endregion

    private fun removeHighlights(){
        highlightedCards.forEach{ card ->
            card.value?.let {
                it.isHighlightOn = false
                card.postValue(card.value)
            }
        }

        highlightedCards.clear()
    }

    private fun resetSelectedCards(){
        firstSelectedCard = SelectedCard()
        secondSelectedCard = SelectedCard()
    }

    // region show potential impact

    // if valid target visualise impact
    // otherwise deselect
    private fun showImpact(){


    }
    // endregion
}