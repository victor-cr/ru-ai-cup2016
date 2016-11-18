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
//        File file = new File(System.getProperty("user.home"), "test.ai-cup-2016");
//
//        try (PrintWriter out = new PrintWriter(new FileWriter(file, false))) {
//            Arrays.stream(world.getBuildings()).map(e -> "0x808080ff" + ';' + e.getRadius() + ';' + e.getX() + ';' + e.getY() + '\n').forEach(out::write);
//            Arrays.stream(world.getTrees()).map(e -> "0x008000ff" + ';' + e.getRadius() + ';' + e.getX() + ';' + e.getY() + '\n').forEach(out::write);
//            out.write("0xff0000ff;" + self.getRadius() + ';' + self.getX() + ';' + self.getY() + '\n');
//        } catch (IOException e) {
//            throw new IllegalStateException(e);
//        }
//
//
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