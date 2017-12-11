package com.slupicki.actors;

import akka.actor.AbstractActor;
import com.slupicki.services.GreetingService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GreetingActor extends AbstractActor {

    private GreetingService greetingService;

    public GreetingActor(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Greet.class, greet -> {
                    getSender().tell(greetingService.greet(greet.name), getSelf());
                })
                .build();
    }

    public static class Greet {

        private String name;

        public Greet(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}