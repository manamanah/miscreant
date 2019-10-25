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
import androidx.cardview.widget.CardView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.miscreant.*
import com.example.android.miscreant.Enums.*
import com.example.android.miscreant.database.Highscore
import com.example.android.miscreant.models.Card
import com.example.android.miscreant.models.Dungeonstatus
import com.example.android.miscreant.models.ImpactOutput
import com.example.android.miscreant.models.SelectedCard
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GameViewModel(private val context: Context) : ViewModel() {

    var heroSpecial: String = ""
        private set

    var leftItemsValue = 0
        private set

    var gameOver = false
        private set

    val maxDecks: Int = if (::settings.isInitialized) settings.maxDeckNumber else 0

    var counterAttackRunning: Boolean = false
        private set

    private val _navigateToLoseFragment = MutableLiveData<Boolean>()
    val navigateToLoseFragment: LiveData<Boolean>
        get() = _navigateToLoseFragment

    private val _navigateToWinFragment = MutableLiveData<Boolean>()
    val navigateToWinFragment: LiveData<Boolean>
        get() = _navigateToWinFragment

    // region dungeon area cards
    private val _cardLeftBack = MutableLiveData<Card>()
    val cardLeftBack: LiveData<Card>
        get() = _cardLeftBack

    private val _cardMiddleBack = MutableLiveData<Card>()
    val cardMiddleBack: LiveData<Card>
        get() = _cardMiddleBack

    private val _cardRightBack = MutableLiveData<Card>()

    val cardRightBack: LiveData<Card>
        get() = _cardRightBack

    private val _cardLeftFront = MutableLiveData<Card>()
    val cardLeftFront: LiveData<Card>
        get() = _cardLeftFront

    private val _cardMiddleFront = MutableLiveData<Card>()
    val cardMiddleFront: LiveData<Card>
        get() = _cardMiddleFront

    private val _cardRightFront = MutableLiveData<Card>()
    val cardRightFront: LiveData<Card>
        get() = _cardRightFront

    // endregion

    // region hero area cards
    private val _cardEquipLeft = MutableLiveData<Card>()
    val cardEquipLeft: LiveData<Card>
        get() = _cardEquipLeft

    private val _cardHero = MutableLiveData<Card>()
    val cardHero: LiveData<Card>
        get() = _cardHero

    private val _cardEquipRight = MutableLiveData<Card>()
    val cardEquipRight: LiveData<Card>
        get() = _cardEquipRight

    private val _cardDiscard = MutableLiveData<Card>()
    val cardDiscard: LiveData<Card>
        get() = _cardDiscard

    private val _cardBackpack = MutableLiveData<Card>()
    val cardBackpack: LiveData<Card>
        get() = _cardBackpack
    // endregion

    // region displayed non-card values on screen
    private val _cardsLeftInDeck = MutableLiveData<Int>()
    val cardsLeftInDeck: LiveData<Int>
        get() = _cardsLeftInDeck

    private val _cardsInDeck = MutableLiveData<Int>()
    val cardsInDeck: LiveData<Int>
        get() = _cardsInDeck

    private val _heroMaxHealth = MutableLiveData<Int>()
    val heroMaxHealth: LiveData<Int>
        get() = _heroMaxHealth

    private val _heroPotentialMaxHealth = MutableLiveData<Int>()
    val heroPotentialMaxHealth: LiveData<Int>
        get() = _heroPotentialMaxHealth

    private val _showHeroPotentialMaxHealth = MutableLiveData<Boolean>()
    val showHeroPotentialMaxHealth: LiveData<Boolean>
        get() = _showHeroPotentialMaxHealth

    private val _heroCurrentHealth = MutableLiveData<Int>()
    val heroCurrentHealth: LiveData<Int>
        get() = _heroCurrentHealth

    private val _heroPotentialHealth = MutableLiveData<Int>()
    val heroPotentialHealth: LiveData<Int>
        get() = _heroPotentialHealth

    private val _showHeroPotentialHealth = MutableLiveData<Boolean>()
    val showHeroPotentialHealth: LiveData<Boolean>
        get() = _showHeroPotentialHealth

    private val _specialsUsed = MutableLiveData<Int>()
    val specialsUsed: LiveData<Int>
        get() = _specialsUsed

    private val _dealNewPenalty = MutableLiveData<Int>()
    val dealNewPenalty: LiveData<Int>
        get() = _dealNewPenalty
    // endregion

    // region deck related
    private val _currentDeckNumber = MutableLiveData<Int>()
    val currentDeckNumber: LiveData<Int>
        get() = _currentDeckNumber

    // todo each difficulty consists of 3 different decks
    private val deckPaths: Map<Difficulty, List<String>> = mapOf(
        Difficulty.easy to listOf(
            context.getString(R.string.easy_deck_one).orEmpty(),
            context.getString(R.string.easy_deck_two).orEmpty(),
            context.getString(R.string.easy_deck_three).orEmpty()
        ),
        Difficulty.normal to listOf(
            context.getString(R.string.easy_deck_path).orEmpty(),
            context.getString(R.string.easy_deck_path).orEmpty(),
            context.getString(R.string.easy_deck_path).orEmpty()
        ),
        Difficulty.hard to listOf(
            context.getString(R.string.easy_deck_path).orEmpty(),
            context.getString(R.string.easy_deck_path).orEmpty(),
            context.getString(R.string.easy_deck_path).orEmpty()
        )
    )

    private val activeDeck: MutableList<Card> = mutableListOf()
    // endregion

    // needed for loading decks
    private var repository = HighscoreRepository(this.context)

    private val gameBackground = context.getString(R.string.game_background)
    private lateinit var settings: Settings

    private val dungeonRowSize = context.resources?.getInteger(R.integer.dungeon_row_size) ?: 3
    private var counterAttackImpact = ImpactOutput()

    // region selected & highlighted cards, cardresolver
    private var cardResolver = CardResolver()
    private var firstSelected = SelectedCard()
    private var secondSelected = SelectedCard()
    private val highlightedCards = mutableListOf<MutableLiveData<Card>>()
    // endregion

    // region game area dungeon & hero card mapping
    private val dungeonMap: MutableMap<Location, MutableLiveData<Card>> =
        mutableMapOf(
            Location.dungeon_left_back to _cardLeftBack,
            Location.dungeon_middle_back to _cardMiddleBack,
            Location.dungeon_right_back to _cardRightBack,
            Location.dungeon_left_front to _cardLeftFront,
            Location.dungeon_middle_front to _cardMiddleFront,
            Location.dungeon_right_front to _cardRightFront
        )

    private val heroMap: MutableMap<Location, MutableLiveData<Card>> =
        mutableMapOf(
            Location.equip_left to _cardEquipLeft,
            Location.equip_right to _cardEquipRight,
            Location.backpack to _cardBackpack,
            Location.hero to _cardHero,
            Location.discard to _cardDiscard
        )
    // endregion

    private val _triggerHealing = MutableLiveData<Boolean>()
    val triggerHealing: LiveData<Boolean>
        get() = _triggerHealing

    init {
        // navigation
        _navigateToLoseFragment.value = false
        _navigateToWinFragment.value = false

        _showHeroPotentialHealth.value = false
        _showHeroPotentialMaxHealth.value = false

        // init empty dungeon cards
        _cardLeftBack.value = Card(location = Location.dungeon_left_back)
        _cardMiddleBack.value = Card(location = Location.dungeon_middle_back)
        _cardRightBack.value = Card(location = Location.dungeon_right_back)
        _cardLeftFront.value = Card(location = Location.dungeon_left_front)
        _cardMiddleFront.value = Card(location = Location.dungeon_middle_front)
        _cardRightFront.value = Card(location = Location.dungeon_right_front)

        _triggerHealing.value = false
        // init empty hero area cards
        resetHeroAreaMap()
    }

    fun initializeGameSettings(difficulty: Difficulty, heroName: String, hero: Hero) {
        if (!::settings.isInitialized || gameOver) {
            settings = Settings(
                difficulty = difficulty,
                heroName = heroName,
                hero = hero
            )
            leftItemsValue = 0
            gameOver = false
        }

        // initialize start values
        _dealNewPenalty.value = settings.dealCardsPenalty
        _heroCurrentHealth.value = settings.currentHealth
        _heroMaxHealth.value = settings.currentMaxHealth
        _cardsLeftInDeck.value = if (activeDeck.isEmpty()) 0 else activeDeck.size
        _cardsInDeck.value = activeDeck.size
        _currentDeckNumber.value = 0

        // set hero stuff
        _cardHero.value = settings.getHeroCard()
        _cardHero.value?.location = Location.hero
        _specialsUsed.value = settings.usedSpecials
        heroSpecial = if (hero == Hero.archer) context.getString(R.string.archer_power)
        else context.getString(R.string.viking_power)

        // todo - consider: load all decks for difficulty @app start
    }

    fun startGame() {
        if (activeDeck.isEmpty()) {
            if (deckPaths.containsKey(settings.difficulty)) {
                val deckPath =
                    deckPaths[settings.difficulty]?.get(_currentDeckNumber.value ?: 0).orEmpty()
                activeDeck.addAll(getDeck(deckPath))
                activeDeck.shuffle()
            }
            _cardsInDeck.postValue(activeDeck.size)
        }
        dealCards()
    }

    // 1st tap: select a 1st card and show potential drop locations
    // 2nd tap - on 1st card: deselect / on diff card: select a 2nd card and show potential impact
    // 3rd tap: on diff card - deselect / on 2nd card: move and resolve, resetHighlights
    fun singleTap(view: View) {
        // if clicked somewhere else on game screen reset
        if (view.tag == gameBackground) {
            resetSelectedCards()
            return
        }

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
        if (secondSelected.isEmpty()) {
            secondSelected = SelectedCard(location, cardType)

            // if valid card drop target
            if (firstSelected.location != secondSelected.location) {

                if (highlightedCards.any { it.value?.location == secondSelected.location }) {
                    val firstCard = getCard(firstSelected.inArea, firstSelected.location)
                    val secondCard = getCard(secondSelected.inArea, secondSelected.location)

                    val output = cardResolver.showImpact(
                        firstCard,
                        secondCard,
                        settings.currentHealth,
                        settings.currentMaxHealth
                    )
                    applyImpact(output)
                    return
                }
            }
            resetSelectedCards()
        } // if tap on 2nd card: resolve card move action
        else {
            if (location == secondSelected.location) {
                val firstCard = getCard(firstSelected.inArea, firstSelected.location)
                val secondCard = getCard(secondSelected.inArea, secondSelected.location)

                val output = cardResolver.resolveCardAction(
                    firstSelected,
                    firstCard,
                    secondCard,
                    settings.currentHealth,
                    settings.currentMaxHealth
                )
                applyImpact(output)
            }
            resetSelectedCards()
            moveBackrowCardsToDungeonFront()
        }
    }

    // shortcut to make move, if 1st card selected and doubleTap on valid dropTarget
    fun doubleTap(view: View) {
        if (firstSelected.isEmpty()) {
            return
        }

        // if clicked somewhere else on game screen reset
        if (view.tag == gameBackground) {
            resetSelectedCards()
            return
        }

        val cardType = view.getCardTypeFromTag()
        val location = view.getCardLocationByName()

        if (secondSelected.isEmpty()) {
            secondSelected = SelectedCard(location, cardType)
        }

        // if valid card drop target
        if (firstSelected.location != secondSelected.location) {

            if (highlightedCards.any { it.value?.location == secondSelected.location }) {
                val firstCard = getCard(firstSelected.inArea, firstSelected.location)
                val secondCard = getCard(secondSelected.inArea, secondSelected.location)

                val output = cardResolver.resolveCardAction(
                    firstSelected,
                    firstCard,
                    secondCard,
                    settings.currentHealth,
                    settings.currentMaxHealth
                )
                applyImpact(output)
            }
        }

        resetSelectedCards()
        moveBackrowCardsToDungeonFront()
    }

    fun dealNewCards() {
        // apply penalty
        val newHealth = settings.currentHealth - settings.dealCardsPenalty
        if (newHealth <= 0) {
            gameLost()
            return
        }
        settings.updateHeroHealth(newHealth)
        _heroCurrentHealth.postValue(settings.currentHealth)

        // existing cards back into deck
        dungeonMap.forEach { (_, card) ->
            card.value?.let {
                if (!it.isEmpty()) {
                    it.triggerCounterAttackAnimation = false
                    activeDeck.add(it)
                    card.value = Card(location = it.location)
                }
            }
        }
        activeDeck.shuffle()
        dealCards()
    }

    fun navigatedToLoseFragment() {
        _navigateToLoseFragment.postValue(false)
    }

    fun navigatedToWinFragment() {
        _navigateToWinFragment.postValue(false)
    }

    fun onClawEnd(view: CardView) {
        Log.i(this.javaClass.simpleName, "Claw animation ENDED")

        val location = view.getCardLocationByName()
        if (heroMap.containsKey(location)) {
            val card = heroMap[location]?.value ?: Card()
            card.triggerClawAnimation = false
            card.triggerCounterHitAnimation = false
            heroMap[location]?.postValue(card)
        }

        // if damage through shield/weapon on hero
        resetHeroAnimationTriggers()
    }

    fun onHitEnd(view: CardView) {
        Log.i(this.javaClass.simpleName, "Hit animation ENDED")

        val location = view.getCardLocationByName()
        if (dungeonMap.containsKey(location)) {
            val card = dungeonMap[location]?.value ?: Card()
            card.triggerHitAnimation = false
            dungeonMap[location]?.postValue(card)
        }

        resetHeroAnimationTriggers()
    }

    fun healingDone() {
        _triggerHealing.postValue(false)
    }

    private fun resetHeroAnimationTriggers() {
        val heroCard = _cardHero.value ?: Card()
        heroCard.triggerClawAnimation = false
        heroCard.triggerCounterHitAnimation = false
        _cardHero.postValue(heroCard)
    }

    fun counterAttackAnimationEnded(view: CardView) {
        val location = view.getCardLocationByName()
        if (dungeonMap.containsKey(location)) {
            dungeonMap[location]?.value?.let {
                it.triggerCounterAttackAnimation = false
                it.showCounterAttackIntent = false
                dungeonMap[location]?.postValue(it)
            }

            val dungeonStatus = getDungeonStatus()
            if (dungeonStatus.counterAttackOpen == 0) {
                // update card values
                applyCounterAttackImpact(counterAttackImpact)

                counterAttackRunning = false
                dealCards(dungeonStatus)
            }
        }
    }

    // better throw exception -> no game possible w/o deck
    private fun getDeck(deckPath: String): MutableList<Card> {

        val bufferedReader = context.assets.open(deckPath).bufferedReader()
        val jsonString = bufferedReader.use { it.readText() } //read and store in string

        val type = object : TypeToken<MutableList<Card>>() {}.type
        return Gson().fromJson(jsonString, type)
    }

    private fun dealCards(
        dungeonStatus: Dungeonstatus = Dungeonstatus(
            emptySpots = dungeonMap.keys.toMutableList(),
            monstersInDungeon = 0
        )
    ) {
        // deck is empty -> if no monsters & dungeon clearDifficulty -> scoring & new deck
        if (activeDeck.isEmpty()) {
            // if any monster left -> not over yet
            if (dungeonStatus.monstersInDungeon > 0) {
                return
            }

            // if dungeon cleared -> continue with new deck
            if (dungeonStatus.emptySpots.size == dungeonMap.size && !gameOver) {
                val deckNr = _currentDeckNumber.value ?: 0
                _currentDeckNumber.value = deckNr + 1

                return when {
                    deckNr + 1 > settings.maxDeckNumber -> gameWon()
                    else -> startGame()
                }
            }
            return
        }

        // fill dungeon
        dungeonStatus.emptySpots.forEach { location ->
            if (activeDeck.isNotEmpty()) {
                val dealtCard = activeDeck[0]
                dealtCard.location = location
                dungeonMap[location]?.value = dealtCard
                activeDeck.removeAt(0)
            } else moveBackrowCardsToDungeonFront()
        }

        // update values
        _cardsLeftInDeck.postValue(activeDeck.size)
    }

    private fun gameWon() {
        Log.i(this.javaClass.simpleName, "GameWON - Entered")

        // values of equipped, stored cards
        heroMap.forEach { (_, card) ->
            card.value?.let {
                if (!it.isEmpty() && it.type != CardType.hero) {
                    leftItemsValue += it.health
                }
            }
        }

        val deckpoints = (currentDeckNumber.value
            ?: 0).times(context.resources.getInteger(R.integer.deckMultiplier))
        val maxHealthPoints = (heroMaxHealth.value
            ?: 0).times(context.resources.getInteger(R.integer.maxHealthMultiplier))

        val highscore = Highscore(
            difficulty = settings.difficulty.title,
            heroName = settings.heroName,
            heroType = settings.hero.imageName,
            points = deckpoints + maxHealthPoints + leftItemsValue
        )
        repository.insert(highscore)

        _navigateToWinFragment.postValue(true)
        resetGame()
    }

    private fun gameLost() {
        Log.i(this.javaClass.simpleName, "GameLOST Entered")

        val deckpoints = (currentDeckNumber.value
            ?: 0).times(context.resources.getInteger(R.integer.deckMultiplier))
        val maxHealthPoints = (heroMaxHealth.value
            ?: 0).times(context.resources.getInteger(R.integer.maxHealthMultiplier))

        val highscore = Highscore(
            difficulty = settings.difficulty.title,
            heroName = settings.heroName,
            heroType = settings.hero.imageName,
            points = deckpoints + maxHealthPoints
        )
        repository.insert(highscore)

        _navigateToLoseFragment.postValue(true)
        resetGame()
    }

    private fun resetGame() {
        activeDeck.clear()
        resetHeroAreaMap()
        gameOver = true
    }

    private fun applyImpact(impactOutput: ImpactOutput) {
        updateCardValues(firstSelected.inArea, impactOutput.firstCard)
        updateCardValues(secondSelected.inArea, impactOutput.secondCard)

        updateHeroValues(impactOutput)

        // maybe monster was killed in front row
        moveBackrowCardsToDungeonFront()
    }

    private fun applyCounterAttackImpact(impactOutput: ImpactOutput) {
        Log.i(this.javaClass.simpleName, "counter attack impact APPLIED")

        if (impactOutput.secondCard.type == CardType.shield ||
            impactOutput.secondCard.type == CardType.none
        ) {
            heroMap[impactOutput.secondCard.location]?.postValue(impactOutput.secondCard)
        }

        updateHeroValues(impactOutput)
    }

    private fun updateHeroValues(impactOutput: ImpactOutput) {
        // hero life values
        if (impactOutput.currentHealth != -1) {
            if (impactOutput.showPotentialHealth) {
                _heroPotentialHealth.postValue(impactOutput.currentHealth)
                _showHeroPotentialHealth.postValue(true)
            } else {
                // hero dead
                if (impactOutput.currentHealth <= 0) {
                    gameLost()
                    return
                }

                if (impactOutput.currentHealth >= settings.currentHealth) {
                    _triggerHealing.postValue(true)
                } else { // if damage goes through shield/weapon visualize claw on hero
                    if (impactOutput.secondCard.type != CardType.hero && _cardHero.value?.triggerClawAnimation != true) {
                        val card = _cardHero.value ?: Card()
                        card.triggerClawAnimation = true
                        _cardHero.postValue(card)
                    }
                }

                _heroCurrentHealth.postValue(impactOutput.currentHealth)
                settings.updateHeroHealth(impactOutput.currentHealth)
            }
        }

        if (impactOutput.maxHealth != -1) {
            if (impactOutput.showPotentialMaxHealth) {
                _heroPotentialMaxHealth.postValue(impactOutput.maxHealth)
                _showHeroPotentialMaxHealth.postValue(true)
            } else {
                _heroMaxHealth.postValue(impactOutput.maxHealth)
                settings.updateHeroMaxHealth(impactOutput.maxHealth)
            }
        }

        if (impactOutput.specialUsed) {
            val tempUsedSpecials = settings.usedSpecials + 1

            if (!impactOutput.potentialSpecialUse) {
                settings.updateUsedSpecials()
            }
            _specialsUsed.postValue(tempUsedSpecials)
        }
    }

    private fun isValidFirstSelectedCard(cardType: CardType, location: Location): Boolean {
        val isEmptyBackpack =
            location == Location.backpack && cardBackpack.value?.isEmpty() ?: false

        val isHeroSelectionChecked = if (settings.hero == Hero.viking) {
            if (cardType == CardType.hero) settings.usedSpecials < settings.maxSpecials
            else true
        } else cardType != CardType.hero

        val validCard = cardType != CardType.none &&
                location != Location.dungeon_left_back &&
                location != Location.dungeon_middle_back &&
                location != Location.dungeon_right_back &&
                location != Location.none &&
                location != Location.discard

        return validCard && !isEmptyBackpack && isHeroSelectionChecked
    }

    // region show drop locations
    // is called when a first card is selected
    private fun showDropLocations() {
        when (firstSelected.inArea) {
            Area.hero -> showDropLocationsForHero()
            Area.dungeon -> showDropLocationsForDungeonCard()
            Area.equipped -> showDropLocationsForEquippedCard()
            Area.backpack -> showDropLocationsForBackpackedCard()
            else -> return
        }
    }

    // only viking -> to apply special on equipped weapon
    private fun showDropLocationsForHero() {
        heroMap.forEach { (location, card) ->
            card.value?.let {
                if (!it.isEmpty() && it.type == CardType.weapon &&
                    (location == Location.equip_left || location == Location.equip_right)
                ) {
                    it.isHighlightOn = true
                    card.postValue(it)
                    highlightedCards.add(card)
                }
            }
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
            }
        }
    }

    // non-monster: if free: all on hero side (if empty)
    // monster: hero; equipped, if cards equipped
    private fun showDropLocationsForDungeonCard() {
        val isMonster = firstSelected.type == CardType.monster

        heroMap.forEach { (_, card) ->
            card.value?.let {
                if (isMonster) {
                    if (it.type == CardType.backpack || it.type == CardType.discard || it.isEmpty()) {
                        return@forEach
                    }

                    it.isHighlightOn = true
                    card.postValue(it)
                    highlightedCards.add(card)
                } else {
                    if (it.isEmpty()) {
                        it.isHighlightOn = true
                        card.postValue(it)
                        highlightedCards.add(card)
                    } else return@forEach
                }
            }
        }
    }

    /*
    equipped = weapon or shield (potion is consumed immediately)
    once equipped no chance for backpack, discard
    cant attack monster with shield
    dungeon front (unless archer), if monster card
     */
    private fun showDropLocationsForEquippedCard() {
        val isArcher = settings.hero == Hero.archer

        if (firstSelected.type == CardType.shield) {
            return
        }

        dungeonMap.forEach { (location, card) ->
            card.value?.let {
                if (it.type == CardType.monster) {
                    if (location == Location.dungeon_left_front ||
                        location == Location.dungeon_middle_front ||
                        location == Location.dungeon_right_front
                    ) {
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

    private fun moveBackrowCardsToDungeonFront() {
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

        val dungeonStatus = getDungeonStatus()
        if (dungeonStatus.emptySpots.size >= dungeonRowSize && !counterAttackRunning) {
            monsterCounterAttack(dungeonStatus)
        }
    }

    private fun monsterCounterAttack(dungeonStatus: Dungeonstatus) {
        // check, if any monsters at front
        val frontCards = listOf(_cardLeftFront, _cardMiddleFront, _cardRightFront)
        val monstersAttacking = frontCards.any { it.value?.type == CardType.monster }
        Log.i(this.javaClass.simpleName, "monster counterAttack: $monstersAttacking")

        // reset any selected cards
        resetSelectedCards()

        if (!monstersAttacking) {
            dealCards(dungeonStatus)
            return
        }

        // otherwise monster counterAttack
        counterAttackRunning = true
        var attackValue = 0

        // display attack text and trigger animation
        frontCards.forEach { card: MutableLiveData<Card> ->
            card.value?.let {
                if (it.type == CardType.monster) {
                    attackValue += it.counterAttackValue
                    it.showCounterAttackIntent = true
                    it.triggerCounterAttackAnimation = true
                    card.postValue(it)
                }
            }
        }

        // create fictive monsterCard with combined attackValue
        val monsterCard = Card(type = CardType.monster, health = attackValue)
        // if shield equipped, target it - otherwise hero
        val targetCard = getCounterAttackTarget()

        counterAttackImpact =
            cardResolver.resolveCounterAttack(monsterCard, targetCard, settings.currentHealth)
        triggerCounterAttackClawAnimation()
    }

    // returns shield, if equipped or hero
    // if 2 shields are equipped, higher shield is returned unless Difficulty.hard
    private fun getCounterAttackTarget(): Card {
        val leftCard = _cardEquipLeft.value?.copy() ?: Card()
        val rightCard = _cardEquipRight.value?.copy() ?: Card()

        if (leftCard.type == CardType.shield) {
            // 2 shields equipped
            return if (rightCard.type == CardType.shield) {
                if (leftCard.health >= rightCard.health) {
                    // return lower shield if Difficulty.hard
                    if (settings.difficulty == Difficulty.hard) rightCard else leftCard
                } else if (settings.difficulty == Difficulty.hard) leftCard else rightCard
            } else return leftCard
        }

        return if (rightCard.type == CardType.shield) rightCard
        else _cardHero.value
            ?: throw IllegalArgumentException("${this.javaClass.simpleName} HERO-CARD is null")
    }

    private fun triggerResolveAnimations(output: ImpactOutput) {
        Log.i(this.javaClass.simpleName, "Resolve animations TRIGGERED")

        if (output.secondCard.location != Location.hero) {
            val card = heroMap[output.secondCard.location]?.value ?: Card()
            if (card.triggerClawAnimation != output.secondCard.triggerClawAnimation) {
                card.triggerClawAnimation = output.secondCard.triggerClawAnimation
                heroMap[output.secondCard.location]?.postValue(card)
            }
        }

        if (output.currentHealth != -1 && _cardHero.value?.triggerClawAnimation == false) {
            _cardHero.value?.let {
                it.triggerClawAnimation = true
                _cardHero.postValue(it)
            }
        }
    }

    private fun triggerCounterAttackClawAnimation() {
        Log.i(this.javaClass.simpleName, "counter attack claw animation TRIGGERED")

        if (counterAttackImpact.secondCard.location != Location.hero) {
            val card = heroMap[counterAttackImpact.secondCard.location]?.value ?: Card()
            if (!card.triggerCounterHitAnimation) {
                card.triggerCounterHitAnimation = true
                heroMap[counterAttackImpact.secondCard.location]?.postValue(card)
            }
        }

        if (counterAttackImpact.currentHealth != -1 && _cardHero.value?.triggerCounterHitAnimation == false) {
            _cardHero.value?.let {
                it.triggerCounterHitAnimation = true
                _cardHero.postValue(it)
            }
        }
    }

    private fun getDungeonStatus(): Dungeonstatus {
        val dungeonStatus = Dungeonstatus()

        dungeonMap.forEach { (key, card) ->
            card.value?.let {
                when {
                    it.isEmpty() -> dungeonStatus.emptySpots.add(key)
                    it.type == CardType.monster -> {
                        dungeonStatus.monstersInDungeon += 1
                        if (it.triggerCounterAttackAnimation) {
                            dungeonStatus.counterAttackOpen += 1
                        } else return@forEach
                    }
                    else -> return@forEach
                }
            }
        }
        return dungeonStatus
    }

    private fun moveFromToCard(from: MutableLiveData<Card>, to: MutableLiveData<Card>) {
        val toLocation = to.value?.location ?: Location.none
        val fromCard = from.value ?: Card()

        from.value = Card(location = fromCard.location)

        fromCard.location = toLocation
        to.value = fromCard
    }

    private fun getCard(area: Area, location: Location): Card {
        val card = when (area) {
            Area.hero -> _cardHero.value
            Area.dungeon -> dungeonMap[location]?.value
            Area.equipped -> heroMap[location]?.value
            Area.backpack -> _cardBackpack.value
            Area.discard -> _cardDiscard.value
            else -> Card()
        }
        return card ?: Card()
    }

    private fun updateCardValues(area: Area, updatedCard: Card) {
        when (area) {
            Area.hero -> _cardHero.setValue(updatedCard)
            Area.dungeon -> dungeonMap[updatedCard.location]?.setValue(updatedCard)
            Area.equipped -> heroMap[updatedCard.location]?.setValue(updatedCard)
            Area.backpack -> _cardBackpack.setValue(updatedCard)
            Area.discard -> _cardDiscard.setValue(updatedCard)
            else -> return
        }
    }

    private fun resetSelectedCards() {
        // reset highlighted cards
        highlightedCards.forEach { card ->
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
        val card = getCard(firstSelected.inArea, firstSelected.location)
        card.let {
            if (!it.isEmpty()) {
                it.showRIP = false
                it.showEquip = false
                it.showHealth = it.type != CardType.hero
                it.showConsumed = false
                it.showPotentialHealth = false
                it.isLookActive = true
            }
        }
        updateCardValues(firstSelected.inArea, card)

        // disable potential impact visualization for hero values
        _showHeroPotentialHealth.postValue(false)
        _showHeroPotentialMaxHealth.postValue(false)

        _specialsUsed.postValue(settings.usedSpecials)

        firstSelected = SelectedCard()
        secondSelected = SelectedCard()
    }

    private fun resetHeroAreaMap() {
        _cardEquipLeft.value = Card(location = Location.equip_left)
        _cardHero.value = Card(location = Location.hero)
        _cardEquipRight.value = Card(location = Location.equip_right)
        _cardBackpack.value = Card(
            image = context.resources?.getResourceName(R.drawable.backpack) ?: "",
            location = Location.backpack
        )
        _cardDiscard.value = Card(location = Location.discard)
    }
}