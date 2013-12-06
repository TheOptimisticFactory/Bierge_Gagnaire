package main.java.cards.model.basics;

import main.java.gameContext.model.GameFlag;

import com.google.common.base.Preconditions;


/**
 * Classe correspondant � une carte sp�ciale (carte avec un effet)
 */
public class CardSpecial extends Card {	
	private final Effect effet;

	/* ========================================= CONSTRUCTOR ========================================= */
	
	/**
	 * Constructeur de carte sp�ciale
	 * @param valeur Valeur de la carte (doit �tre sup�rieure � 0)
	 * @param couleur Couleur de la carte
	 * @param effet Effet de la carte
	 */
	public CardSpecial(int valeur, Color couleur, Effect effet) {
		super(valeur, couleur);
		System.out.println("CREATING SPECIAL CARD WITH VALUE OF " + valeur);
		Preconditions.checkNotNull(effet,"[ERROR] Effect cannot be null");
		this.effet = effet;
	}

	/* ========================================= EFFECT ========================================= */

	/**
	 * M�thode permettant de d�clencher l'execution d'un effet
	 * @return 
	 */
	public GameFlag declencherEffet() {
		return this.effet.triggerEffect();
	}
	
	@Override
	public Integer getValeur() {
		return this.value;
	}
	
	@Override
	public Color getCouleur() {
		return this.color;
	}
	
/* ========================================= ADVANCED COMPARAISON ========================================= */
	
	@Override
	public boolean isCompatibleWith(Card otherCard) {
		Preconditions.checkNotNull(otherCard,"[ERROR] Impossible to test compatibility : provided card is null");
		if(otherCard.isSpecial()) {
			CardSpecial explicitConversionFromOtherCard = (CardSpecial)otherCard;
			return isCompatibleWithSpecialCard(explicitConversionFromOtherCard);
		} else {
			return isCompatibleWithNumberedCard(otherCard);
		}
	}
	
	/**
	 * M�thode permettant de savoir si une carte peut �tre jou�e par dessus la carte actuelle (dans le cas d'une carte SPECIALE)
	 * A noter que dans le cas des cartes sp�ciales, la valeur n'est pas un crit�re de compatibilit�
	 * @param otherCard Carte que l'on souhaite eventuellement jouer
	 * @return TRUE si la carte est "compatible" (si elle peut �tre jou�e), FALSE sinon
	 */
	private boolean isCompatibleWithSpecialCard(CardSpecial otherCard) {
		Preconditions.checkNotNull(otherCard,"[ERROR] Impossible to test compatibility : provided card is null");
		if(this.hasSameColorThan(otherCard.getCouleur())) {
			return true;
		} else if(this.hasSameEffectThan(otherCard.getEffet())) {
			return true;
		} else if(otherCard.getCouleur().equals(Color.JOKER)){
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * M�thode priv�e permettant de g�rer la comparaison entre carte sp�ciale et la carte pass�e en param�tre (dans le cas d'une carte NUMEROTEE)
	 * @param otherCard Carte dont on souhaite tester la compatibilit�
	 * @return TRUE si la carte est compatible, FALSE sinon
	 */
	private boolean isCompatibleWithNumberedCard(Card otherCard) {
		Preconditions.checkNotNull(otherCard,"[ERROR] Impossible to test compatibility : provided card is null");
		if(this.hasSameColorThan(otherCard.getCouleur())) {
			return true;
		} else if(otherCard.getCouleur().equals(Color.JOKER)){
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * M�thode priv�e permettant de savoir si l'effet de la carte actuelle est le m�me que l'effet pass� en param�tre
	 * @param valueFromOtherCard Effet d'une 2�me carte, pass� en param�tre
	 * @return TRUE si les 2 effets sont identiques, FALSE sinon
	 */
	private boolean hasSameEffectThan(String effectFromAnotherCard) {
		Preconditions.checkNotNull(effectFromAnotherCard,"[ERROR] Impossible to compare effets : provided effect is null");
		return this.getEffet().equals(effectFromAnotherCard);
	}
	
	
	
	/* ========================================= BASIC COMPARAISON ========================================= */

	/**
	 * M�thode d�finissant les crit�res d'�galit� entre deux cartes sp�ciales
	 */
	@Override
	public boolean equals(Object other) {
		boolean isSpecialCard = other.getClass().equals(CardSpecial.class);
		if(!isSpecialCard) {
			return false;
		} else {
			CardSpecial otherSpecialCard = (CardSpecial)other;
			boolean sameColor = hasSameColorThan(otherSpecialCard.getCouleur());
			boolean sameValue = hasSameValueThan(otherSpecialCard.getValeur());
			boolean sameEffect = hasSameEffectThan(otherSpecialCard.getEffet());
			return sameColor && sameValue && sameEffect;
		}
	}
	
	/* ========================================= GETTERS ========================================= */
	
	/**
	 * M�thode permettant de r�cuperer la description d'un effet
	 * @return String contenant la description de l'effet de la carte
	 */
	public String getEffet() {
		return this.effet.getDescription();
	}
	
	/**
	 * M�thode permettant de v�rifier si une carte est sp�ciale ou non
	 * @return TRUE s'il s'agit d'une CarteSpeciale, FALSE sinon
	 */
	@Override
	public Boolean isSpecial() {
		return true;
	}

	/* ========================================= DISPLAY ========================================= */

	/**
	 * M�thode permettant sp�cifiant la fa�on dont s'affiche une carte sp�ciale
	 */
	@Override
	public String toString() {
		return "[CARTE SPECIALE] Valeur=" + super.getValeur() + ", Couleur=" + super.getCouleur() + ", Effet=" + this.effet;
	}
}