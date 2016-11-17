import com.codegans.ai.cup2016.StrategyDelegate;
import model.Game;
import model.Move;
import model.Wizard;
import model.World;

public final class MyStrategy implements Strategy {
    private final Object lock = new Object();
    private volatile StrategyDelegate delegate = null;

    @Override
    public void move(Wizard self, World world, Game game, Move move) {
        StrategyDelegate local = delegate;

        if (local == null) {
            synchronized (lock) {
                if (delegate == null) {
                    local = delegate = new StrategyDelegate();
                } else {
                    local = delegate;
                }
            }
        }

        local.move(self, world, game, move);
    }
}