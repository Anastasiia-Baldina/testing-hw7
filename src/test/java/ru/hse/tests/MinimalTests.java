package ru.hse.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.hse.TelegramBotApplication;
import ru.hse.bot.EcommerceTelegramBot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MinimalTests {
    private TelegramBotApplication app;
    private EcommerceTelegramBot bot;
    private StringBuffer botOut;
    private static final long userId = 1L;
    private ByteArrayOutputStream sysOutContent;
    private PrintStream outPS;
    private PrintStream originalOut;

    @BeforeEach
    public void beforeEach() {
        sysOutContent = new ByteArrayOutputStream();
        final String utf8 = StandardCharsets.UTF_8.name();
        try {
            originalOut = System.out;
            outPS = new PrintStream(sysOutContent, true, utf8);
            System.setOut(outPS);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        app = new TelegramBotApplication(true);
        try {
            app.startServices();
            bot = app.getBot();
            botOut = app.getBotTestWriter().getBuffer();
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    public void afterEach() throws IOException {
        outPS.close();
        sysOutContent.close();
        System.setOut(originalOut);
    }

    private String reactOnMessage(String message) {
        bot.onUpdateReceived(app.getDataStubs().formUpdateRequest(message, userId));
        try {
            return botOut.toString();
        } finally {
            botOut.setLength(0);
        }
    }

    @Test
    public void testBeforeStart() {
        String res = reactOnMessage("/hello");
        assertTrue(res.length() > 0);
        assertTrue(res.toLowerCase().contains("please use /start to create an account first"));
    }
}
