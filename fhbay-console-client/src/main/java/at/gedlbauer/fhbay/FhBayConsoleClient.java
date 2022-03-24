package at.gedlbauer.fhbay;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.jline.PromptProvider;

@SpringBootApplication
public class FhBayConsoleClient {
    public static void main(String[] args) {
        SpringApplication.run(FhBayConsoleClient.class, args);
    }

    @Bean("promptProvider")
    public PromptProvider getPrompt() {
        return () ->
                new AttributedString("fhbay> ", AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN));
    }
}
