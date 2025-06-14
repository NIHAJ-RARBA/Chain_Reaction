package chain_Reaction.chainReaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ChainReactionApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChainReactionApplication.class, args);
	}

}
