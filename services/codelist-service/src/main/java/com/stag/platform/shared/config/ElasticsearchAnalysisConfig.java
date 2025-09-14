package com.stag.platform.shared.config;

import org.hibernate.search.backend.elasticsearch.analysis.ElasticsearchAnalysisConfigurationContext;
import org.hibernate.search.backend.elasticsearch.analysis.ElasticsearchAnalysisConfigurer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchAnalysisConfig implements ElasticsearchAnalysisConfigurer {

    @Override
    public void configure(ElasticsearchAnalysisConfigurationContext context) {
        context.analyzer("czech_autocomplete")
               .custom()
               .tokenizer("standard")
               .tokenFilters("lowercase", "asciifolding", "czech_stemmer", "edge_ngram");

        context.analyzer("czech_autocomplete_search")
               .custom()
               .tokenizer("standard")
               .tokenFilters("lowercase", "asciifolding", "czech_stemmer");

        // Token filters
        context.tokenFilter("czech_stemmer")
               .type("stemmer")
               .param("language", "czech");

        context.tokenFilter("edge_ngram")
               .type("edge_ngram")
               .param("min_gram", "1")
               .param("max_gram", "20");
    }

}
