import { check, group, sleep } from 'k6';
import { Options } from 'k6/options';
import grpc from 'k6/net/grpc';
import faker from 'k6/x/faker';
import {
  GRPC_URL,
  PROTO_DIR,
  MAX_PERSON_ID,
  MIN_PERSON_ID,
  STUDENT_IDS,
} from '../shared/common.ts';

const client = new grpc.Client();
client.load([PROTO_DIR], 'stag/academics/student/v1/student_service.proto');

export const options: Options = {
  insecureSkipTLSVerify: true,
  thresholds: {
    grpc_req_duration: ['p(99) < 500'],
    'grpc_req_failed{scenario:smoke}': ['rate == 0'],
  },
  scenarios: {
    smoke: {
      executor: 'shared-iterations',
      vus: 1,
      iterations: 1,
      exec: 'smokeTest',
      startTime: '0s',
      maxDuration: '10s',
    },
    average_load: {
      executor: 'ramping-vus',
      stages: [
        { duration: '1m', target: 10 },
        { duration: '2m', target: 10 },
        { duration: '1m', target: 0 },
      ],
      exec: 'loadTest',
      startTime: '10s',
    },
  },
};

export function smokeTest() {
  client.connect(GRPC_URL, { plaintext: true, reflect: true });

  const personId = 13373;
  const studentId = STUDENT_IDS[0];

  group('gRPC Smoke Test - StudentService', () => {
    let res = client.invoke(
      'stag.academics.student.v1.StudentService/GetStudentIds',
      { person_id: personId },
    );
    check(res, {
      'GetStudentIds: status is OK': (r) => r.status === grpc.StatusOK,
    });

    res = client.invoke(
      'stag.academics.student.v1.StudentService/GetStudentPersonId',
      { student_id: studentId },
    );
    check(res, {
      'GetStudentPersonId: status is OK': (r) => r.status === grpc.StatusOK,
    });

    client.close();
  });
}

export function loadTest() {
  client.connect(GRPC_URL, { plaintext: true, reflect: true });

  group('gRPC Load Test - StudentService', () => {
    // 50% chance to call one or the other
    if (Math.random() < 0.5) {
      const personId = faker.numbers.uintRange(MIN_PERSON_ID, MAX_PERSON_ID);
      const res = client.invoke(
        'stag.academics.student.v1.StudentService/GetStudentIds',
        { person_id: personId },
      );
      check(res, {
        'GetStudentIds: status is OK': (r) => r.status === grpc.StatusOK,
      });
    } else {
      const studentId =
        STUDENT_IDS[faker.numbers.uintRange(0, STUDENT_IDS.length - 1)];
      const res = client.invoke(
        'stag.academics.student.v1.StudentService/GetStudentPersonId',
        { student_id: studentId },
      );
      check(res, {
        'GetStudentPersonId: status is OK': (r) => r.status === grpc.StatusOK,
      });
    }

    client.close();
  });

  sleep(faker.numbers.float64Range(0.5, 1.5));
}
