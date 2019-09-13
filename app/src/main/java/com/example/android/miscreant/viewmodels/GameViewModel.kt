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
import com.example.android.miscreant.Settings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.IllegalArgumentException

class GameViewModel(context: Context?) : ViewModel() {

    // needed for loading decks
    private val context: Context = context ?: throw IllegalArgumentException("${this.javaClass.simpleName}: context is NULL" )
    private lateinit var settings: Settings

    // region deck related
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

    private var _currentDeckNumber = MutableLiveData<Int>()
    val currentDeckNumber : LiveData<Int>
        get() = _currentDeckNumber

    private var activeDeck: MutableList<Card> = mutableListOf()
    // endregion

    // region selected, highlighted cards, cardresolver
    private var firstSelected = SelectedCard()
    private var secondSelected= SelectedCard()
    private var _firstSelectedCard = MutableLiveData<Card>()
    private var _secondSelectedCard = MutableLiveData<Card>()
    private var highlightedCards = mutableListOf<MutableLiveData<Card>>()
    // endregion

    // region cards in play
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

    // region game area dungeon & hero card mapping
    private var dungeonMap: MutableMap<Location, MutableLiveData<Card>> =
        mutableMapOf(
            Location.dungeon_left_back to _cardLeftBack,
            Location.dungeon_middle_back to _cardMiddleBack,
            Location.dungeon_right_back to _cardRightBack,
            Location.dungeon_left_front to _cardLeftFront,
            Location.dungeon_middle_front to _cardMiddleFront,
            Location.dungeon_right_front to _cardRightFront
        )

    private var heroMap: MutableMap<Location, MutableLiveData<Card>> =
        mutableMapOf(
            Location.equip_left to _cardEquipLeft,
            Location.equip_right to _cardEquipRight,
            Location.backpack to _cardBackpack,
            Location.hero to _cardHero,
            Location.discard to _cardDiscard
        )
    // endregion
    // endregion

    // region displayed non-card values on screen
    private var _cardsLeftInDeck = MutableLiveData<Int>()
    val cardsLeftInDeck  : LiveData<Int>
        get() = _cardsLeftInDeck

    private var _heroMaxHealth = MutableLiveData<Int>()
    val heroMaxHealth  : LiveData<Int>
        get() = _heroMaxHealth

    private var _heroPotentialMaxHealth = MutableLiveData<Int>()
    val heroPotentialMaxHealth  : LiveData<Int>
        get() = _heroPotentialMaxHealth

    private var _showHeroPotentialMaxHealth = MutableLiveData<Boolean>()
    val showHeroPotentialMaxHealth  : LiveData<Boolean>
        get() = _showHeroPotentialMaxHealth

    private var _heroCurrentHealth = MutableLiveData<Int>()
    val heroCurrentHealth  : LiveData<Int>
        get() = _heroCurrentHealth

    private var _heroPotentialHealth = MutableLiveData<Int>()
    val heroPotentialHealth  : LiveData<Int>
        get() = _heroPotentialHealth

    private var _showHeroPotentialHealth = MutableLiveData<Boolean>()
    val showHeroPotentialHealth  : LiveData<Boolean>
        get() = _showHeroPotentialHealth

    private var _specialsUsed = MutableLiveData<Int>()
    val specialsUsed  : LiveData<Int>
        get() = _specialsUsed

    var heroSpecial  : String = ""
        private set
    // endregion

    init {
        _showHeroPotentialHealth.value = false
        _showHeroPotentialMaxHealth.value = false

        // init empty dungeon cards
        _cardLeftBack.value = Card(location = Location.dungeon_left_back)
        _cardMiddleBack.value = Card(location = Location.dungeon_middle_back)
        _cardRightBack.value = Card(location = Location.dungeon_right_back)
        _cardLeftFront.value = Card(location = Location.dungeon_left_front)
        _cardMiddleFront.value = Card(location = Location.dungeon_middle_front)
        _cardRightFront.value = Card(location = Location.dungeon_right_front)

        // init empty hero area cards
        _cardEquipLeft.value = Card(location = Location.equip_left)
        _cardHero.value = Card(location = Location.hero)
        _cardEquipRight.value = Card(location = Location.equip_right)
        _cardBackpack.value = Card(image = context?.resources?.getResourceName(R.drawable.backpack) ?: "", location = Location.backpack)
        _cardDiscard.value = Card(location = Location.discard)
    }

    fun initializeGameSettings(difficulty: Difficulty, heroName: String, hero: Hero){
        if (!::settings.isInitialized){
            settings = Settings(
                difficulty = difficulty,
                heroName = heroName,
                hero = hero
            )
        }

        // initialize start values
        _heroCurrentHealth.value = settings.currentHealth
        _heroMaxHealth.value = settings.currentMaxHealth
        _cardsLeftInDeck.value = if (activeDeck.isEmpty()) 0 else activeDeck.size

        // set hero stuff
        _cardHero.value = settings.getHeroCard()
        _cardHero.value?.location = Location.hero
        _specialsUsed.value = settings.usedSpecials
        heroSpecial = if (hero == Hero.archer) context.getString(R.string.archer_power)
                      else context.getString(R.string.viking_power)

        // todo - consider: load all decks for difficulty @app start
    }

    fun startGame(){
        // todo: check, if still running
        // todo: if running, check if monster attack
        if (activeDeck.isEmpty()){
            if (deckPaths.containsKey(settings.difficulty)){
                val deckNumber = _currentDeckNumber.value ?: 0

                val deckPath = deckPaths[settings.difficulty]?.get(deckNumber).orEmpty()
                activeDeck = getDeck(deckPath)
                activeDeck.shuffle()
            }

            // todo check decksize
        }

        // todo: ensure after game end - in scoring all left dungeon cards removed -> points
        // todo: ensure progress currentDeckNumber in Settings @gameEnd
        dealCards()
        // update nr cardsLeft/in Deck
    }

    // 1st tap: select a 1st card and show potential drop locations
    // 2nd tap - on 1st card: deselect / on diff card: select a 2nd card and show potential impact
    // 3rd tap: on diff card - deselect / on 2nd card: move and resolve, resetHighlights
    fun singleTap(view: View){
        val cardType = view.getCardTypeFromTag()
        val location = view.getCardLocationByName()

        if (firstSelected.isEmpty()) {
            if (isValidFirstSelectedCard(cardType, location)) {
                firstSelected = SelectedCard(location, cardType)
                showDropLocations()
            }
            return
        }

        // no 2nd card selected yet
        if (secondSelected.isEmpty()){
            secondSelected = SelectedCard(location, cardType)

            // if valid card drop target
            if (firstSelected.location != secondSelected.location){

                if (highlightedCards.any{ it.value?.location == secondSelected.location}){
                    _firstSelectedCard = getCard(firstSelected.inArea, firstSelected.location)
                    _secondSelectedCard = getCard(secondSelected.inArea, secondSelected.location)

                    // todo visualize outcome
                    return
                }
            }
            resetSelectedCards()
        } // if tap on 2nd card: resolve card move action
        else {
            if (location == secondSelected.location){
                // todo resolve outcome
            }
            resetSelectedCards()
            moveBackrowCardsToDungeonFront()
        }
    }

    // shortcut to make move, if 1st card selected and doubleTap on valid dropTarget
    fun doubleTap(view: View){
        if (firstSelected.isEmpty()){
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
                        var dealtCard = activeDeck[0]
                        dealtCard.location = key
                        card.value = dealtCard
                        activeDeck.removeAt(0)
                    }
                    else moveBackrowCardsToDungeonFront()
                }
                else return@forEach
            }
        }

        // todo monster retaliate
    }

    private fun isValidFirstSelectedCard(cardType: CardType, location: Location): Boolean {

        val isEmptyBackpack = location == Location.backpack && cardBackpack.value?.isEmpty() ?: false

        val validCard = cardType != CardType.none && cardType != CardType.hero &&
                                location != Location.dungeon_left_back &&
                                location != Location.dungeon_middle_back &&
                                location != Location.dungeon_right_back &&
                                location != Location.none &&
                                location != Location.discard

        return validCard && !isEmptyBackpack
    }

    // region show drop locations
    // is called when a first card is selected
    private fun showDropLocations(){
        when(firstSelected.inArea){
            Area.dungeon -> showDropLocationsForDungeonCard()
            Area.equipped -> showDropLocationsForEquippedCard()
            Area.backpack -> showDropLocationsForBackpackedCard()
            else -> return
        }
    }

    // equip left, equip right
    private fun showDropLocationsForBackpackedCard() {
        heroMap.forEach { (location, card) ->
            card.value?.let {
                if (it.isEmpty() && location != Location.backpack && location != Location.discard) {
                    it.isHighlightOn = true
                    card.postValue(it)
                    highlightedCards.add(card)
                }
                else return@forEach
            }
        }
    }

    // non-monster: if free: all on hero side (if empty)
    // monster: hero; equipped, if cards equipped
    private fun showDropLocationsForDungeonCard(){
        val isMonster = firstSelected.type == CardType.monster

        heroMap.forEach{ (_, card) ->
            card.value?.let {
                if (isMonster){
                    if (it.type == CardType.backpack || it.type == CardType.discard || it.isEmpty()){
                        return@forEach
                    }

                    it.isHighlightOn = true
                    card.postValue(it)
                    highlightedCards.add(card)
                }
                else {
                    if (it.isEmpty()){
                        it.isHighlightOn = true
                        card.postValue(it)
                        highlightedCards.add(card)
                    }
                    else return@forEach
                }
            }
        }
    }

    // equipped = weapon or shield (potion is consumed immediately)
    // once equipped no chance for backpack, discard
    // cant attack monster with shield
    // dungeon front (unless archer), if monster card
    private fun showDropLocationsForEquippedCard(){
        val isArcher = settings.hero == Hero.archer

        if (firstSelected.type == CardType.shield){
            return
        }

        dungeonMap.forEach{ (location, card)->
            card.value?.let {
                if (it.type == CardType.monster){
                    if (location == Location.dungeon_left_front ||
                        location == Location.dungeon_middle_front ||
                        location == Location.dungeon_right_front) {
                        it.isHighlightOn = true
                        card.postValue(card.value)
                        highlightedCards.add(card)
                    }
                    else {
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

    private fun moveBackrowCardsToDungeonFront(){
        _cardLeftFront.value?.let {
            if (it.isEmpty()) {
                moveFromToCard(_cardLeftBack, _cardLeftFront)
            }
        }

        _cardMiddleFront.value?.let {
            if (it.isEmpty()) {
                moveFromToCard(_cardMiddleBack, _cardMiddleFront)
            }
        }

        _cardRightFront.value?.let {
            if (it.isEmpty()) {
                moveFromToCard(_cardRightBack, _cardRightFront)
            }
        }
    }

    private fun moveFromToCard(from: MutableLiveData<Card>, to: MutableLiveData<Card>){
        val toLocation = to.value?.location ?: Location.none
        var fromCard = from.value ?: Card()

        from.value = Card(location = fromCard.location)
        from.postValue(from.value)

        fromCard.location = toLocation
        to.postValue(fromCard)
    }

    private fun getCard(area: Area, location: Location): MutableLiveData<Card> {
        return when (area){
            Area.hero -> _cardHero
            Area.dungeon -> dungeonMap[location] ?: MutableLiveData()
            Area.equipped -> heroMap[location] ?: MutableLiveData()
            Area.backpack -> _cardBackpack
            Area.discard -> _cardDiscard
            else -> MutableLiveData()
        }
    }

    private fun resetSelectedCards(){
        // reset highlighted cards
        highlightedCards.forEach{ card ->
            card.value?.let {
                it.isHighlightOn = false
                it.showRIP = false
                it.showEquip = false
                it.showHealth = it.type != CardType.hero && it.type != CardType.none
                it.showConsumed = false
                it.showPotentialHealth = false
                it.isLookActive = true
                card.postValue(card.value)
            }
        }

        highlightedCards.clear()

        // reset potential visualisations on first selected card
        _firstSelectedCard.value?.let{
            if (!it.isEmpty()){
                it.showRIP = false
                it.showEquip = false
                it.showHealth = true
                it.showConsumed = false
                it.showPotentialHealth = false
                it.isLookActive = true
                _firstSelectedCard.postValue(_firstSelectedCard.value)
            }
        }

        // disable potential impact visualization for hero values
        _showHeroPotentialHealth.value = false
        _showHeroPotentialHealth.postValue(_showHeroPotentialHealth.value)
        _showHeroPotentialMaxHealth.value = false
        _showHeroPotentialMaxHealth.postValue(_showHeroPotentialMaxHealth.value)

        firstSelected = SelectedCard()
        secondSelected = SelectedCard()
    }
}