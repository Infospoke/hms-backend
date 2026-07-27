package com.hms.service.events;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.hms.service.service.ResumeReuploadMailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ResumeReuploadEmailListener {

    private final ResumeReuploadMailService resumeReuploadMailService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleResumeReuploadRequest(ResumeReuploadRequestedEvent event) {

        log.info("Sending Resume Re-upload mail for applicationId : {}",
                event.getApplicationId());

        resumeReuploadMailService.sendResumeReuploadMail(
                event.getApplicationId());
    }
}