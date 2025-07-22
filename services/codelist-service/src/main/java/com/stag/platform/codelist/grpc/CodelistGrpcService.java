package com.stag.platform.codelist.grpc;

import com.stag.platform.codelist.entity.CodelistEntryId;
import com.stag.platform.codelist.service.CodelistService;
import com.stag.platform.codelist.v1.CodelistServiceGrpc;
import com.stag.platform.codelist.v1.CodelistValue;
import com.stag.platform.codelist.v1.GetCodelistValuesRequest;
import com.stag.platform.codelist.v1.GetCodelistValuesResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

@GrpcService
@RequiredArgsConstructor
public class CodelistGrpcService extends CodelistServiceGrpc.CodelistServiceImplBase {

    private final CodelistService codelistService;

    @Override
    public void getCodelistValues(GetCodelistValuesRequest request, StreamObserver<GetCodelistValuesResponse> responseObserver) {
        List<CodelistEntryId> codelistEntryIds = request.getCodelistKeysList()
                                                        .stream()
                                                        .map(key -> new CodelistEntryId(key.getDomain(), key.getLowValue()))
                                                        .toList();

        List<CodelistValue> codelistValues = codelistService.getCodelistEntryMeanings(codelistEntryIds)
                                                            .stream()
                                                            .map(entry -> CodelistValue.newBuilder()
                                                                                       .setDomain(entry.idDomain())
                                                                                       .setLowValue(entry.idLowValue())
                                                                                       .setMeaning(request.getLanguage().equals("cs") ? entry.meaningCz() : entry.meaningEn())
                                                                                       .build()
                                                            )
                                                            .toList();

        GetCodelistValuesResponse response = GetCodelistValuesResponse.newBuilder()
                                                                      .addAllCodelistValues(codelistValues)
                                                                      .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
