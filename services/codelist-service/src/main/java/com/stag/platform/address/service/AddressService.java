package com.stag.platform.address.service;

import com.stag.platform.address.entity.AddressPoint;
import com.stag.platform.address.repository.projection.AddressSuggestion;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final EntityManager entityManager;

    // TODO: Use Hibernate Search (see ChatGPT chats)
    //      see https://stackoverflow.com/questions/78023879/hibernate-search-with-spring-boot-3
    //      see https://github.com/Netz00/hibernate-search-6-example
    //      see https://hibernate.org/search/more-resources/
    //      see https://www.mindbowser.com/hibernate-search-6-with-spring-boot/
    //      see https://medium.com/@elijahndungu30/spring-boot-3-0-search-api-using-hibernate-search-5fafad506b69
    //  or ElasticSearch
    //  or make the STAG form more structured (see Gemini CLI chats)

    @Transactional(readOnly = true)
    public List<AddressSuggestion> findAddressSuggestions(String query) {
        SearchSession searchSession = Search.session(entityManager);

        SearchResult<AddressPoint> addressPoints =
            searchSession.search(AddressPoint.class)
                         .where(factory -> factory.match() // try phrase()
                                                  .fields("fullAddress")
                                                  .matching(query)
                                                  .fuzzy(0) // No fuzzy matching
                         )
                         .fetch(10);

        return addressPoints.hits()
                            .stream()
                            .map(ap -> new AddressSuggestion(
                                ap.getId(),
                                ap.getFullAddress()
                            ))
                            .toList();
    }

}
