package dev.lisek.crazybytes.game;

import dev.lisek.crazybytes.App;
import dev.lisek.crazybytes.entity.Card;
import dev.lisek.crazybytes.entity.CardPile;
import dev.lisek.crazybytes.entity.Player;
import dev.lisek.crazybytes.entity.Players;
import dev.lisek.crazybytes.ui.Animation;

public class Blackjack extends Game {

    class Ruleset {
        final static int getScore(CardPile hand) {
            int score = 0;
            boolean ace = false;
            for (Card card : hand.cards) {
                score += card.getRank() > 10 ? 10 : card.getRank();
                if (card.getRank() == 1)
                    ace = true;
            }
            if (ace && score <= 11)
                score += 10;
            return score <= 21 ? score : 0;
        }

        final static boolean checkValid(CardPile hand) {
            return getScore(hand) > 0;
        }
    }

    public Blackjack(Players players, boolean local) {
        super(players, local);
        super.rounds = 1;
        super.cards = 2;
    }

    /**
     * Checks the validity of the move and plays the card if it is valid.
     * @param card The card to play. <br>
     * @return True if the move is valid, false otherwise. <br><ul>
     * @see <li>Card#setHandler(boolean)
     * @see <li>Player#takeControl()
     * </ul>
     */
    @Override
    public boolean play(Card card) {
        App.game.players.handControl();
        return true;
    }

    /**
     * Draws and animates a card.
     * @param card The card to draw. <br><ul>
     * @see <li>Card#setHandler(boolean)
     * @see <li>Player#takeControl()
     * </ul>
     */
    @Override
    public void draw(Card card) {
        card.setHandler(true);

        deck.dealCard();
        App.game.players.current().drawCard(card);
        if (deck.cards.isEmpty()) {
            App.game.stack.shuffle();
            while (!App.game.stack.cards.isEmpty()) {
                Card temp = App.game.stack.dealCard();
                deck.add(temp);
                Animation.move(temp, new double[]{-210, 0});
                Animation.flip(temp);
                temp.toFront();
            }
            deck.activate();
        }
        deck.cards.get(0).setHandler(false);
        if (!Ruleset.checkValid(App.game.players.current().getHand())) {
            System.out.println(App.game.players.current().name.getText() + " busted!");
            if (!this.players.current().AI)
                App.game.players.handControl();
        }
    }

    @Override
    public void computerMove(Player player) {
        Animation.nonBlockingSleep((int)(Math.random() * 1000), () -> {
            if (getScore(player.getHand()) != 0 && getScore(player.getHand()) + Math.random() * 5 < 21) {
                draw(App.game.deck.cards.get(0));
                player.takeControl();
            } else {
                players.handControl();
            }
        });
    }

    @Override
    public int getScore(CardPile hand) {
        return Ruleset.getScore(hand);
    }
}