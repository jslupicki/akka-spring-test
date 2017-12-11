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
import scala.Option;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;
import scala.util.Try;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class GreetingActorSpringTest {

    @Autowired
    private ActorSystem actorSystem;

    private ActorRef greeter;

    @BeforeEach
    void setUp() {

        ActorSelection actorSelection = actorSystem.actorSelection("user/greeter");
        Future<ActorRef> actorRefFuture = actorSelection.resolveOne(FiniteDuration.create(1, TimeUnit.SECONDS));
        Option<Try<ActorRef>> value = actorRefFuture.value();
        Try<ActorRef> actorRefTry = value.get();
        greeter = actorRefTry.get();

/* TODO: investigate why this don't work
        greeter = actorSystem
                .actorSelection("user/greeter")
                .resolveOne(FiniteDuration.create(2, TimeUnit.SECONDS))
                .value()
                .get()
                .get();
*/
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