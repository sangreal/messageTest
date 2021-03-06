package com.martyn.message.service;

import com.martyn.message.MainApplication;
import com.martyn.message.common.ThreadHelper;
import com.martyn.message.data.Message;
import com.martyn.message.exception.MyException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;
import java.util.concurrent.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
@SpringBootTest(classes = {MainApplication.class, H2JpaConfig.class})
public class MessageServiceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageServiceTest.class);

    @Autowired
    private ThreadHelper threadHelper;

    @Autowired
    private Sender sender;

    @Autowired
    private Receiver receiver;

    @Autowired
    private ErrorSender errorSender;

    @Test
    public void sendMessage() throws Exception {
        final String topic = "test1";
        final String userId = "user1";
        int messageCnt = 20;
        List<String > res = new ArrayList<>();
        for (int i = 0; i < messageCnt; i++) res.add(String.valueOf(i));
        res.forEach(i -> {
            sender.sendMessage(topic, i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        Set<String> sets = new HashSet<>();

        ExecutorService executorService = Executors.newFixedThreadPool(1);

        receiver.subscribe(topic, userId);
        List<CompletableFuture> futures = new ArrayList<>();
        for (int j = 0; j < messageCnt; j++) {
            futures.add(CompletableFuture.supplyAsync(() -> receiver.pollMessage(topic, userId)).thenAcceptAsync(fn -> sets.add(fn.getMessage()), executorService));
        }

        for (CompletableFuture f : futures) {
            f.join();
        }

        LOGGER.info("sets size : " + sets.size());

        threadHelper.getExecutor().awaitTermination(5, TimeUnit.SECONDS);
        threadHelper.getExecutor().shutdown();

        Assert.assertEquals(messageCnt, sets.size());
    }

    @Test(expected = MyException.class)
    public void testMessageWithWrongSN() {
        final String topic = "test1";
        final String userId = "user1";
        int messageCnt = 20;
        List<String > res = new ArrayList<>();
        for (int i = 0; i < messageCnt; i++) res.add(String.valueOf(i));
        res.forEach(i -> {
            errorSender.sendMessage(topic, i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

    }

    @Test
    public void testMessageTwoTopics() {
        final String topic = "test1";
        final String userId1 = "user1";
        final String userId2 = "user2";
        int messageCnt = 100;
        List<String > res = new ArrayList<>();
        for (int i = 0; i < messageCnt; i++) res.add(String.valueOf(i));

        ExecutorService executorService = Executors.newFixedThreadPool(5);

        receiver.subscribe(topic, userId1);
        receiver.subscribe(topic, userId2);

        res.forEach(i -> {
            sender.sendMessage(topic, i);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Set<String> mset1 = new HashSet<>();
        Set<String> mset2 = new HashSet<>();

        List<CompletableFuture> futures = new ArrayList<>();
        for (int j = 0; j < messageCnt; j++) {
            futures.add(CompletableFuture.supplyAsync(() -> receiver.pollMessage(topic, userId1)).thenAcceptAsync(fn -> mset1.add(fn.getMessage()), executorService));
            futures.add(CompletableFuture.supplyAsync(() -> receiver.pollMessage(topic, userId2)).thenAcceptAsync(fn -> mset2.add(fn.getMessage()), executorService));

        }

        for (CompletableFuture f : futures) {
            f.join();
        }

        LOGGER.info("mset1 : " + mset1.size());
        LOGGER.info("mese2 : " + mset2.size());

        Assert.assertEquals(mset1.size(), mset2.size());
    }

    @Test
    public void testAckSync() {
        final String topic = "test1";
        final String userId1 = "user1";
        final String userId2 = "user2";
        int messageCnt = 100;
        List<String > res = new ArrayList<>();
        for (int i = 0; i < messageCnt; i++) res.add(String.valueOf(i));

        ExecutorService executorService = Executors.newFixedThreadPool(5);

        receiver.subscribe(topic, userId1);
        receiver.subscribe(topic, userId2);

        res.forEach(i -> {
            sender.sendMessage(topic, i);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        Set<String> mset1 = new HashSet<>();

        List<CompletableFuture> futures = new ArrayList<>();
        for (int j = 0; j < messageCnt; j++) {
            futures.add(CompletableFuture.supplyAsync(() -> receiver.pollMessage(topic, userId1)).thenAcceptAsync(fn -> mset1.add(fn.getMessage()), executorService));

        }

        for (CompletableFuture f : futures) {
            f.join();
        }

        LOGGER.info("mset1 : " + mset1.size());

        Assert.assertEquals(mset1.size(), messageCnt);
    }
}
