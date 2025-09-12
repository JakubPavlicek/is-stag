package com.stag.platform.address.service;

import com.stag.platform.address.entity.AddressPoint;
import com.stag.platform.address.repository.projection.AddressSuggestion;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final EntityManager entityManager;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<AddressSuggestion> findAddressSuggestions(String query) {
        SearchSession searchSession = Search.session(entityManager);

        return searchSession.search(AddressPoint.class)
                            .select(f -> f.composite()
                                          .from(
                                              f.id(Long.class),
                                              f.field("fullAddress", String.class)
                                          )
                                          .as(AddressSuggestion::new)
                            )
                            .where(f -> f.match()
                                         .fields("fullAddress")
                                         .matching(query)
                            )
                            .fetchHits(10);
    }

}
