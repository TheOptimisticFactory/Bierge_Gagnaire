package tests.java.cards.model.basicsTests;

import static org.junit.Assert.assertEquals;
import main.java.cards.model.basics.EffectPlus2;
import main.java.gameContext.model.GameFlags;

import org.junit.Before;
import org.junit.Test;


public class EffetPiocherCarteTest {
	private EffectPlus2 effetPiocher2Cartes;
	private EffectPlus2 effetPiocher7Cartes;
	private String expectedDescription2cards;
	private String expectedDescription7cards;
	private String expectedString2Cards;
	private String expectedString7Cards;
	private GameFlags expectedFlag;

	@Before
	public void setup() {
		this.effetPiocher2Cartes = new EffectPlus2(2);
		this.effetPiocher7Cartes = new EffectPlus2(7);
		this.expectedString2Cards = "Le joueur suivant devra piocher 2 cartes";
		this.expectedString7Cards = "Le joueur suivant devra piocher 7 cartes";
		this.expectedDescription2cards = "+2";
		this.expectedDescription7cards = "+7";
		this.expectedFlag = GameFlags.PLUS_TWO;
	}

	@Test
	public void testDeclencherEffet() {
		assertEquals(this.expectedFlag,this.effetPiocher2Cartes.triggerEffect());
		assertEquals(this.expectedFlag,this.effetPiocher7Cartes.triggerEffect());
	}

	@Test
	public void testToString() {
		assertEquals(this.expectedString2Cards, this.effetPiocher2Cartes.toString());
		assertEquals(this.expectedString7Cards, this.effetPiocher7Cartes.toString());
	}

	@Test
	public void testAfficherDescription() {
		assertEquals(this.expectedDescription2cards, this.effetPiocher2Cartes.getDescription());
		assertEquals(this.expectedDescription7cards, this.effetPiocher7Cartes.getDescription());
	}
}
