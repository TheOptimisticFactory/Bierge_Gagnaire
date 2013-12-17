package utt.fr.rglb.main.java.cards.model.basics;

import utt.fr.rglb.main.java.game.model.GameFlag;

/**
 * Interface sp�cifiant les comportements communs de tous les effets
 */
public interface Effect {
	/**
	 * M�thode permettant de d�clencher un effet et de r�cuperer l'�tat associ�
	 * @return Enumeration d'etat r�sultat du d�clenchement de l'effet
	 */
	public GameFlag triggerEffect();
	
	/**
	 * M�thode permettant d'afficher un effet dans l'interface
	 * @return String � afficher
	 */
	public String toString();
	
	/**
	 * M�thode permettant d'afficher la description d'un effet
	 * @return String correspondant � la description de l'effet
	 */
	public String getDescription();
}