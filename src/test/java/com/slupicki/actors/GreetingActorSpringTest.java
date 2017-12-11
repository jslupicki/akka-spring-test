package com.slupicki.actors;

import akka.actor.ActorRef;
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

    @Autowired
    private ActorSystem actorSystem;

    private ActorRef greeter;

    @BeforeEach
    void setUp() {
        greeter = actorSystem.actorOf(SPRING_EXTENSION_PROVIDER.get(actorSystem)
                .props("greetingActor"), "greeter_for_test");
    }

    @Test
    void start() throws Exception {
        FiniteDuration duration = FiniteDuration.create(1, TimeUnit.SECONDS);
        Timeout timeout = Timeout.durationToTimeout(duration);

        Future<Object> result = Patterns.ask(greeter, new GreetingActor.Greet("John"), timeout);

        String greet = (String) Await.result(result, duration);

        assertThat(greet).contains("John", "Hello");
    }
}