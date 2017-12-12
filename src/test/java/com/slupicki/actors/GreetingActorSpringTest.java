package com.slupicki.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.util.Timeout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;

import static com.slupicki.SpringExtension.SPRING_EXTENSION_PROVIDER;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class GreetingActorSpringTest {

    private static final String ACTOR_NAME = "greeter_for_test";

    @Autowired
    private ActorSystem actorSystem;

    private ActorSelection greeter;

    @BeforeEach
    void setUp() throws Exception {
        greeter = actorSystem.actorSelection("user/" + ACTOR_NAME);
        FiniteDuration timeout = FiniteDuration.create(1, TimeUnit.SECONDS);
        Future<ActorRef> future = greeter.resolveOne(timeout);
        Await.ready(future, timeout);
        if (future.value().get().isFailure()) {
            actorSystem.actorOf(SPRING_EXTENSION_PROVIDER.get(actorSystem).props("greetingActor"), ACTOR_NAME);
            greeter = actorSystem.actorSelection("user/" + ACTOR_NAME);
        }
    }

    @Test
    void simpleAsk() throws Exception {
        String greet = sendGreeting(greeter, "John");

        assertThat(greet).contains("John", "Hello");
    }

    @Test
    void findActorByName() throws Exception {
        ActorSelection actorSelection = actorSystem.actorSelection("user/greeter_for_test");

        String greet = sendGreeting(actorSelection, "John");

        assertThat(greet).contains("John", "Hello");
    }

    private String sendGreeting(ActorSelection greeter, String name) throws Exception {
        FiniteDuration duration = FiniteDuration.create(1, TimeUnit.SECONDS);
        Timeout timeout = Timeout.durationToTimeout(duration);

        Future<Object> result = Patterns.ask(greeter, new GreetingActor.Greet(name), timeout);

        return (String) Await.result(result, duration);
    }

}