import grpc from 'k6/net/grpc';
import { check } from 'k6';
import { GRPC_URL, TEST_RUN_ID } from '../shared/common.ts';

const client = new grpc.Client();

export function connect() {
  client.connect(GRPC_URL, { plaintext: true, reflect: true });
}

export function close() {
  client.close();
}

export function getStudyProgramAndField(request: any) {
  const response = client.invoke('stag.academics.studyplan.v1.StudyPlanService/GetStudyProgramAndField', request, {
    tags: { testId: TEST_RUN_ID },
  });

  check(response, {
    'GetStudyProgramAndField: status is OK': (r) => r.status === grpc.StatusOK,
  });

  return response;
}
