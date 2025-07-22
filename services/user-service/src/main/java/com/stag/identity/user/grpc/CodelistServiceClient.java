package com.stag.identity.user.grpc;

import com.stag.identity.user.dto.CodelistEntryId;
import com.stag.platform.codelist.v1.CodelistKey;
import com.stag.platform.codelist.v1.CodelistServiceGrpc;
import com.stag.platform.codelist.v1.CodelistValue;
import com.stag.platform.codelist.v1.GetCodelistValuesRequest;
import com.stag.platform.codelist.v1.GetCodelistValuesResponse;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CodelistServiceClient {

    @GrpcClient("codelist-service")
    private CodelistServiceGrpc.CodelistServiceBlockingStub codelistServiceStub;

    public Map<CodelistEntryId, String> getCodelistMeanings(Collection<CodelistEntryId> codelistEntryIds) {
        GetCodelistValuesRequest request = GetCodelistValuesRequest.newBuilder()
                                                                   .addAllCodelistKeys(getCodelistKeys(codelistEntryIds))
                                                                   .setLanguage("cs")
                                                                   .build();

        GetCodelistValuesResponse codelistMeanings = codelistServiceStub.getCodelistValues(request);
        return codelistMeanings.getCodelistValuesList()
                               .stream()
                               .collect(Collectors.toMap(
                                   entry -> new CodelistEntryId(entry.getDomain(), entry.getLowValue()),
                                   CodelistValue::getMeaning
                               ));
    }

    private static List<CodelistKey> getCodelistKeys(Collection<CodelistEntryId> codelistEntryIds) {
        return codelistEntryIds.stream()
                               .map(CodelistServiceClient::toCodelistKey)
                               .toList();
    }

    private static CodelistKey toCodelistKey(CodelistEntryId id) {
        return CodelistKey.newBuilder()
                          .setDomain(id.domain())
                          .setLowValue(id.lowValue())
                          .build();
    }

}
