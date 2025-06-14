package chain_Reaction.chainReaction.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class GameStateWatcher {
    private final File gameStateFile = new File("gamestate.txt");
    private final AtomicLong version = new AtomicLong();

    @PostConstruct
    public void init() {
        version.set(gameStateFile.lastModified());
    }

    @Scheduled(fixedDelay = 100) // check every second
    public void checkForUpdate() {
        long lastModified = gameStateFile.lastModified();
        if (lastModified > version.get()) {
            version.set(lastModified);
            System.out.println("gamestate.txt updated at " + lastModified);
        }
    }

    public long getVersion() {
        return version.get();
    }
}
