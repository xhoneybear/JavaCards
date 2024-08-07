package dev.lisek.crazybytes.game;

import dev.lisek.crazybytes.App;
import dev.lisek.crazybytes.config.Config;
import dev.lisek.crazybytes.entity.Card;
import dev.lisek.crazybytes.entity.CardPile;
import dev.lisek.crazybytes.entity.Player;
import dev.lisek.crazybytes.entity.Players;
import dev.lisek.crazybytes.ui.Animation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

class GameMenu extends StackPane {
    GameMenu(StackPane layout) {
        StackPane bg = new StackPane();
        bg.setBackground(Background.fill(Color.BLACK));
        bg.setOpacity(0.5);
        Button back = new Button("Back to game");
        back.setOnAction(eh -> layout.getChildren().remove(this));
        Button settings = new Button("Settings");
        Button exit = new Button("Exit");
        exit.setOnAction(eh -> App.stage.setScene(App.menu));
        VBox fg = new VBox(back, settings, exit);
        fg.setAlignment(Pos.CENTER);
        this.getChildren().addAll(bg, fg);
    }
}

public abstract class Game extends Scene {

    public static StackPane layout;

    public final CardPile deck = new CardPile(true);
    public final CardPile stack = new CardPile(false);
    public final Players players;
    public final boolean hotseat;
    public int rounds;
    int cards;

    Game(Players players, boolean hotseat) {
        super(layout = new StackPane(), 1600, 1000);
        this.getStylesheets().add("style.css");

        this.players = players;
        this.hotseat = hotseat;

        StackPane board = new StackPane();

        ImageView menu = new ImageView(new Image(Config.DIR + "icons/menu.png"));
        menu.setTranslateX(8);
        menu.setTranslateY(8);
        menu.setCursor(Cursor.HAND);
        menu.setOnMouseClicked(eh -> layout.getChildren().add(new GameMenu(layout)));

        layout.getChildren().addAll(board, menu);
        StackPane.setAlignment(menu, Pos.TOP_LEFT);

        for (int i = 51; i >= 0; i--) {
            board.getChildren().add(deck.cards.get(i));
        }
        for (Player player : players.list) {
            board.getChildren().add(player.label);
        }
    }

    public abstract boolean play(Card card);

    public abstract void draw(Card card);

    public abstract void computerMove(Player player);

    public abstract boolean checkWin();

    private void deal(int pass) {
        if (pass == this.cards * players.length) {
            begin();
            return;
        }
        players.next().drawCard(deck.dealCard());
        Animation.nonBlockingSleep(250, () -> deal(pass + 1));
    }

    private void begin() {
        stack.add(deck.dealCard());
        Animation.flip(stack.cards.get(0));
        for (Card card : deck.cards) {
            Animation.move(card, new double[]{-210, 0});
        }
        players.handControl();
        deck.activate();
    }

    public void start() {
        App.stage.setScene(this);
        deal(0);
    }
}
