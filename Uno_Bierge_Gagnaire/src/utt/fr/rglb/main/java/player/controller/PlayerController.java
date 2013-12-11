package utt.fr.rglb.main.java.player.controller;

import java.util.Collection;

import utt.fr.rglb.main.java.cards.model.GameModelBean;
import utt.fr.rglb.main.java.cards.model.basics.Card;
import utt.fr.rglb.main.java.cards.model.basics.Color;
import utt.fr.rglb.main.java.console.model.InputReader;
import utt.fr.rglb.main.java.console.view.View;
import utt.fr.rglb.main.java.main.ServerException;
import utt.fr.rglb.main.java.player.model.PlayerModel;

import com.google.common.base.Preconditions;


/**
 * Classe dont le r�le est de g�rer tout ce qui touche � un joueur
 */
public class PlayerController {
	protected PlayerModel player;
	protected View consoleView;

	/* ========================================= CONSTRUCTOR ========================================= */
	
	public PlayerController(String name, View consoleView) {
		Preconditions.checkNotNull(name,"[ERROR] name cannot be null");
		Preconditions.checkNotNull(consoleView,"[ERROR] view cannot be null");
		this.player = new PlayerModel(name);
		this.consoleView = consoleView;
	}

	/* ========================================= CARD PICKUP ========================================= */
	
	/**
	 * M�thode permettant d'ajouter une collection de cartes � la main du joueur
	 * @param cards Collection de cartes devant �tre ajout�es
	 */
	public void pickUpCards(Collection<Card> cards) {
		Preconditions.checkNotNull(cards,"[ERROR] Card collection picked up cannot be null");
		Preconditions.checkArgument(cards.size()>0, "[ERROR] Card collection picked cannot be empty");
		this.player.pickUpCards(cards);
		this.player.resetUnoAnnoucement();
	}

	/**
	 * M�thode permettant d'ajouter une unique carte � la main du joueur
	 * @param card Carte devant �tre ajout�e
	 */
	public void pickUpOneCard(Card card) {
		Preconditions.checkNotNull(card,"[ERROR] Card picked up cannot be null");
		this.consoleView.displayCard("You had no playable cards in you hand, you've drawn : ",card);
		this.player.pickUpOneCard(card);
		this.player.resetUnoAnnoucement();
	}

	/**
	 * M�thiode permettant de forcer un joueur � piocher (avec affichage d'un message)
	 * @param cards Collection de cartes � ajouter � sa main
	 */
	public void isForcedToPickUpCards(Collection<Card> cards) {
		Preconditions.checkNotNull(cards,"[ERROR] Card collection picked up cannot be null");
		Preconditions.checkArgument(cards.size()>0, "[ERROR] Card collection picked cannot be empty");
		Integer numberOfCards = cards.size();
		this.consoleView.displayErrorMessageUsingPlaceholders("Player [",getAlias(),"] was forced to draw ",numberOfCards.toString()," cards");
		this.player.pickUpCards(cards);
		this.player.resetUnoAnnoucement();
	}
	
	/* ========================================= CARD PLAY ========================================= */
	
	/**
	 * M�thode permettant de jouer une carte
	 * @param index Index de la carte
	 * @return Carte selectionn�e
	 */
	public Card playCard(int index) {
		Preconditions.checkState(this.player.getNumberOfCardsInHand() > 0, "[ERROR] Impossible to play a card : player has none");
		Preconditions.checkArgument(index >= 0 && index < this.player.getNumberOfCardsInHand(),"[ERROR] Incorrect index : must be > 0 (tried = " + index + ", but max is = " + this.player.getNumberOfCardsInHand());
		return this.player.playCard(index);
	}
	
	/**
	 * M�thode permettant de savoir si le joueur a en sa possession au moins une carte compatible avec celle de r�f�rence
	 * @param referenceCard Carte constituant le talon actuel
	 * @return TRUE si le joueur en a au moins une, FALSE sinon
	 */
	public boolean hasAtLeastOnePlayableCard(GameModelBean gameModelBean) {
		Preconditions.checkNotNull(gameModelBean,"[ERROR] gameModelBean cannot be null");
		return gameModelBean.isCompatibleWith(this.getCardsInHand());
	}
	
	/**
	 * M�thode priv�e permettant de r�cuperer les cartes en main pour pouvoir les afficher
	 * @return Collection de cartes en main
	 */
	protected Collection<Card> getCardsInHand() {
		return this.player.getCardsInHand();
	}
	
	/* ========================================= TURN HANDLING ========================================= */
	
	/**
	 * M�thode permettant de g�rer le tour d'un joueur
	 * @param inputReader Objet permettant de recevoir l'index entr� par l'utilisateur
 	 * @param gameModelBean Carte du talon (carte de r�f�rence)
	 * @return La carte choisie par l'utilisateur (qui est n�cessairement compatible avec le talon)
	 */
	//TODO: Shrink method startTurn ?
	public Card startTurn(InputReader inputReader, GameModelBean gameModelBean) {
		Preconditions.checkNotNull(inputReader,"[ERROR] Impossible to start turn, inputReader is null");
		Preconditions.checkNotNull(gameModelBean,"[ERROR] Impossible to start turn, gameModelbean is null");
		String alias = this.player.toString();
		Collection<Card> cardCollection = this.getCardsInHand();
		String answer = inputReader.getValidAnswer(alias,cardCollection,gameModelBean);
		int index = inputReader.getNumberFromString(answer);
		boolean unoHasBeenAnnounced = inputReader.findIfUnoHasBeenAnnounced(answer);
		Card choosenCard = this.player.peekAtCard(index);
		while(!gameModelBean.isCompatibleWith(choosenCard)) {
			answer = inputReader.getAnotherValidIndexFromInputDueToIncompatibleCard(alias,cardCollection,gameModelBean);
			index = inputReader.getNumberFromString(answer);
			unoHasBeenAnnounced = inputReader.findIfUnoHasBeenAnnounced(answer);
			choosenCard = this.player.peekAtCard(index);
		}
		if(unoHasBeenAnnounced) {
			this.player.setUnoAnnoucement();
		}
		return this.player.playCard(index);
	}

	/**
	 * M�thode priv�e permettant de g�rer le cas o� le joueur est dans l'incapacit� de jouer son tour
	 * @param gameModelbean Carte du talon (carte de r�f�rence)
	 */
	public void unableToPlayThisTurn(GameModelBean gameModelbean) {
		Preconditions.checkNotNull(gameModelbean,"[ERROR] Impossible to start turn, gameModelbean is null");
		Collection<Card> cardsInHand = this.player.getCardsInHand();
		this.consoleView.displayCardCollection("You now have : ",cardsInHand);
		this.consoleView.displayCard("The last card play was : ",gameModelbean.getLastCardPlayed());
		gameModelbean.appendGlobalColorIfItIsSet();
		this.consoleView.displayTwoLinesOfJokerText("Sadly, even after picking a new card, you didn't have any playable","Your turn will now automatically end");
		chillForTwoSec("");
	}
	
	/* ========================================= EFFECTS RELATED ========================================= */
	
	/**
	 * M�thode permettant au joueur de choisir la couleur apr�s avoir jou� un joker (ou +4)
	 * @param inputReader 
	 * @return 
	 */
	public Color hasToChooseColor(InputReader inputReader) {
		Preconditions.checkNotNull(inputReader,"[ERROR] Impossible to start turn, inputReader is null");
		this.consoleView.displayOneLineOfJokerText("You played a Joker, please choose a color");
		return inputReader.getValidColor();
	}

	/* ========================================= GETTERS & UTILS ========================================= */
	
	/**
	 * M�thode permettant de r�cuperer le pseudo du joueur
	 * @return String correspondant � son pseudo
	 */
	public String getAlias() {
		return this.player.toString();
	}

	/**
	 * M�thode permettant de r�cuperer le score du joueur
	 * @return int correspondant � son score
	 */
	public int getScore() {
		return this.player.getScore();
	}

	/**
	 * M�thode permettant de r�cuperer le nombre de cartes en main
	 * @return int correspondant au nombre de cartes en main
	 */
	public int getNumberOfCardsInHand() {
		return this.player.getNumberOfCardsInHand();
	}

	/**
	 * M�thode permettant de savoir si le joueur poss�de encore des cartes dans sa main
	 * @return
	 */
	public boolean stillHasCards() {
		return getNumberOfCardsInHand() > 0;
	}
	
	/**
	 * M�thode defissant comment les objets de cette classe s'affiche
	 */
	@Override
	public String toString() {
		return this.player.toString();
	}

	/**
	 * M�thode permettant de r�-initialiser la main du joueur (suppression de toutes ses cartes)
	 */
	public void resetHand() {
		this.player.resetHand();	
	}
	
	protected void chillForTwoSec(String stringToDisplay) {
		try {
			for(int i=0; i<4; i++) {
				Thread.sleep(500);
				this.consoleView.AppendOneLineOfBoldText(stringToDisplay);
			}
		} catch (InterruptedException e) {
			throw new ServerException("[ERROR] Something went wrong while [IA] " + this.getAlias() + " was peacefully chilling",e);
		}
	}
	
	/* ========================================= POINTS ========================================= */
	
	/**
	 * M�thode permettant de r�cup�rer le nombre de points des cartes en main
	 * @return La somme des points de toutes les cartes en main
	 */
	public int getPointsFromCardsInHand() {
		int pointsFromCards = 0;
		for(Card currentCard : this.player.getCardsInHand()) {
			pointsFromCards += currentCard.getValeur();
		}
		return pointsFromCards;
	}

	/**
	 * M�thode permettant d'incr�menter le score du joueur
	 * @param playerScore Nombre � ajouter au score actuel
	 * @return TRUE si le joueur a atteint 500 points, FALSE sinon
	 */
	public boolean increaseScoreBy(Integer playerScore) {
		Preconditions.checkNotNull(playerScore,"[ERROR] Impossible to set score, provided number is null");
		Preconditions.checkArgument(playerScore > 0,"[ERROR] Impossible to set score, provided number must be positive");
		this.player.increaseScoreBy(playerScore);
		return this.player.getScore() > 500;
	}
	
	/* ========================================= UNO ANNOUNCEMENT ========================================= */
	
	/**
	 * M�thode permettant de v�rifier si le joueur a pr�c�dement annonc� UNO
	 * @return TRUE si c'est le cas, FALSE sinon
	 */
	public boolean hasAnnouncedUno() {
		return this.player.hasAnnouncedUno();
	}

	/**
	 * M�thode permettant de v�rifier si le joueur avait effectivement le droit d'annoncer UNO
	 * @return TRUE s'il reste au joueur 1 carte (annonce lors du jeu de l'avant derni�re carte) OU 0 cartes (jeu de la derni�re carte), FALSE sinon
	 */
	public boolean deservesTheRightToAnnounceUno() {
		return (this.player.getNumberOfCardsInHand() == 1) || (this.player.getNumberOfCardsInHand() == 0);
	}

	/**
	 * M�thode permettant de v�rifier si le joueur a oubli� d'annoncer UNO quand il joue sa derni�re carte
	 * @return TRUE si le joueur n'a pas plus de cartes et a effectivement oubli� d'annoncer UNO, FALSE sinon
	 */
	public boolean hasNoCardAndForgotToAnnounceUno() {
		boolean hasNoCard = this.player.getNumberOfCardsInHand() == 0;
		boolean forgotToAnnounceUno = ! (this.hasAnnouncedUno());
		return hasNoCard && forgotToAnnounceUno;
	}
}