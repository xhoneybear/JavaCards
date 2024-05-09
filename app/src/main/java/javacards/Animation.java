package javacards;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

public class Animation {

    /**
     * Asynchronous sleep for animation purposes.
     * @param delay The amount of time to sleep (in ms).
     * @return null completable future.
     */
    public static CompletableFuture<Void> nonBlockingSleep(long delay) {
        CompletableFuture<Void> completableFuture = new CompletableFuture<>();

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(() -> completableFuture.complete(null), delay, TimeUnit.MILLISECONDS);
        executorService.shutdown();

        return completableFuture;
    }

    /**
     * This function allows moving cards.
     * @param card The card to move.
     * @param pos The position to move to.
     */
    public static void move(Card card, double[] pos) {
        TranslateTransition move = new TranslateTransition(Duration.seconds(0.4), card.card);
        move.setToX(pos[0]);
        move.setToY(pos[1]);
        if (pos.length == 3) {
            RotateTransition rotate = new RotateTransition(Duration.seconds(0.4), card.card);
            rotate.setToAngle(pos[2]);
            rotate.play();
        }
        move.play();
    }

    /**
     * This function allows flipping cards.
     * @param card The card to flip.
     */
    public static void flip(Card card) {
        card.scale.setByX(card.scale.getByX() == 2 ? -2 : 2);
        card.scale.play();
        nonBlockingSleep(200).thenRun(() -> {
            card.visible = !card.visible;
            if (card.visible) {
                card.view.setImage(card.img);
            } else {
                card.view.setImage(Config.BACK);
            }
        });
    }
}
