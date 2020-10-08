package org.thinkbigthings.zdd.server.security;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ExpiredTokenReaper {

    private static final int runEveryMs = 1000;

    @Scheduled(fixedRate = 1000)
    public void removeExpiredRememberMeTokens() {

        // TODO task should be configurable in properties
        // one approaxh   @Scheduled(fixedDelayString = "${fixedDelay.in.milliseconds}")

        // TODO If spring lazy initialization is true, this will never run unless another class needs this class
        // have the reaper method be part of a service that is always used

        // TODO inject a persistent token repository that has the ability to find and remove expired tokens

        System.out.println("Removed expired tokens");
    }

}
