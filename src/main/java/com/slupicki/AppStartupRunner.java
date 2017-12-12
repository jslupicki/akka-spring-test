package com.slupicki;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.slupicki.actors.GreetingActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;

import static com.slupicki.SpringExtension.SPRING_EXTENSION_PROVIDER;

@Component
public class AppStartupRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AppStartupRunner.class);

    @Autowired
    private ActorSystem actorSystem;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Your application started with option names : {}", args.getOptionNames());
        ActorRef greeter = actorSystem.actorOf(SPRING_EXTENSION_PROVIDER.get(actorSystem)
                .props("greetingActor"), "greeter");

        FiniteDuration duration = FiniteDuration.create(1, TimeUnit.SECONDS);
        Timeout timeout = Timeout.durationToTimeout(duration);

        Future<Object> result = Patterns.ask(greeter, new GreetingActor.Greet("John"), timeout);

        String greet = (String) Await.result(result, duration);

        log.info("Actor {} answer with {}", greeter.toString(), greet);
    }
}