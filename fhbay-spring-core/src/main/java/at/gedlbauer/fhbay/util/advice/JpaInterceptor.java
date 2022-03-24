package at.gedlbauer.fhbay.util.advice;

import at.gedlbauer.fhbay.util.JpaUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManagerFactory;

@Component
@Aspect
public class JpaInterceptor {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Around("execution(public * at.gedlbauer.fhbay.shellcommands..*.*(..))")
    public Object holdEntityManager(ProceedingJoinPoint pjp) throws Throwable {
        if (entityManagerFactory == null)
            throw new IllegalArgumentException("Property 'entityManagerFactory' is required");

        boolean participate = false;
        if (TransactionSynchronizationManager.hasResource(entityManagerFactory))
            participate = true;
        else {
            logger.trace("Opening EntityManager");
            JpaUtil.openEntityManager(entityManagerFactory);
        }

        try {
            return pjp.proceed(); // delegates to method of target class.
        } finally {
            if (!participate) {
                JpaUtil.closeEntityManager(entityManagerFactory);
                logger.trace("Closed EntityManager");
            }
        }
    }
}