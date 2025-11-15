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

export function getStudentIds(request: { person_id: number }) {
  const res = client.invoke('stag.academics.student.v1.StudentService/GetStudentIds', request, {
    tags: { testId: TEST_RUN_ID },
  });

  check(res, {
    'GetStudentIds: status is OK': (r) => r.status === grpc.StatusOK,
  });

  return res;
}

export function getStudentPersonId(request: { student_id: string }) {
  const res = client.invoke('stag.academics.student.v1.StudentService/GetStudentPersonId', request, {
    tags: { testId: TEST_RUN_ID },
  });

  check(res, {
    'GetStudentPersonId: status is OK': (r) => r.status === grpc.StatusOK,
  });

  return res;
}
