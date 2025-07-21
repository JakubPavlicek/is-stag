package com.stag.platform.codelist.grpc;

import com.stag.platform.codelist.entity.CodelistValueId;
import com.stag.platform.codelist.projection.CodelistValueMeaning;
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
        List<CodelistValueId> codelistValueIdsList = request.getCodelistValueIdsList()
                                                            .stream()
                                                            .map(cv -> CodelistValueId.builder()
                                                                                      .domain(cv.getDomain())
                                                                                      .lowValue(cv.getLowValue())
                                                                                      .build())
                                                            .toList();

        List<CodelistValueMeaning> codelistValueMeanings = codelistService.getCodelistValues(codelistValueIdsList);
        List<CodelistValue> codelistValues = codelistValueMeanings.stream()
                                                                  .map(cv -> CodelistValue.newBuilder()
                                                                                          .setDomain(cv.idDomain())
                                                                                          .setLowValue(cv.idLowValue())
                                                                                          .setMeaning(cv.meaningCz())
                                                                                          .build())
                                                                  .toList();

        GetCodelistValuesResponse response = GetCodelistValuesResponse.newBuilder()
                                                                      .addAllCodelistValues(codelistValues)
                                                                      .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
