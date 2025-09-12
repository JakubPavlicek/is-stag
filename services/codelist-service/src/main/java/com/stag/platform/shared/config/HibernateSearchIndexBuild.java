package com.stag.platform.shared.config;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class HibernateSearchIndexBuild implements ApplicationListener<ApplicationReadyEvent> {

    private final EntityManager entityManager;

    // TODO: Do not reindex the whole database on every startup.
    //  Only reindex the entries that have changed since the last indexing.
    //  This will save a lot of time and resources.

    @Override
    @Transactional
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        log.info("Starting Hibernate Search indexing...");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

//        SearchSession searchSession = Search.session(entityManager);
//        MassIndexer indexer = searchSession.massIndexer()
//                                           .idFetchSize(150)
//                                           .batchSizeToLoadObjects(30)
//                                           .threadsToLoadObjects(12);
//
//        try {
//            indexer.startAndWait();
//        } catch (InterruptedException e) {
//            log.warn("Failed to load data from database - interrupted while starting Hibernate Search indexing", e);
//            throw new RuntimeException(e);
//        }

        stopWatch.stop();
        log.info("Hibernate Search indexing completed in {} ms", stopWatch.getTotalTimeMillis());
    }

}
