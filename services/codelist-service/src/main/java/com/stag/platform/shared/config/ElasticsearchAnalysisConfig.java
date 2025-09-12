package com.stag.platform.shared.config;

import org.hibernate.search.backend.elasticsearch.analysis.ElasticsearchAnalysisConfigurationContext;
import org.hibernate.search.backend.elasticsearch.analysis.ElasticsearchAnalysisConfigurer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchAnalysisConfig implements ElasticsearchAnalysisConfigurer {

    @Override
    public void configure(ElasticsearchAnalysisConfigurationContext context) {
        context.analyzer("czech")
               .custom()
               .tokenizer("standard")
               .tokenFilters("lowercase", "asciifolding"
//                   , "czech_stop", "czech_stemmer"
               );

//        context.tokenFilter("czech_stop")
//               .type("stop")
//               .param("stopwords", "_czech_");
//
//        context.tokenFilter("czech_stemmer")
//               .type("stemmer")
//               .param("language", "czech");
    }

}
