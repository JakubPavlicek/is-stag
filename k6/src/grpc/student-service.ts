import { group, sleep } from 'k6';
import { Options } from 'k6/options';
import faker from 'k6/x/faker';
import * as studentClient from './student-client.ts';
import { MAX_PERSON_ID, MIN_PERSON_ID } from '../shared/common.ts';
import { STUDENT_IDS } from '../../data/student-ids.ts';

export const options: Options = {
  insecureSkipTLSVerify: true,
  thresholds: {
    grpc_req_duration: ['p(99) < 500'],
    'http_req_failed{scenario:smoke}': ['rate == 0'],
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
  studentClient.connect();

  const personId = 13373;
  const studentId = STUDENT_IDS[0];

  group('gRPC Smoke Test - StudentService', () => {
    studentClient.getStudentIds({ person_id: personId });
    studentClient.getStudentPersonId({ student_id: studentId });
  });

  studentClient.close();
}

export function loadTest() {
  studentClient.connect();

  group('gRPC Load Test - StudentService', () => {
    // 50% chance to call one or the other
    if (Math.random() < 0.5) {
      const personId = faker.numbers.uintRange(MIN_PERSON_ID, MAX_PERSON_ID);
      studentClient.getStudentIds({ person_id: personId });
    } else {
      const studentId = STUDENT_IDS[faker.numbers.uintRange(0, STUDENT_IDS.length - 1)];
      studentClient.getStudentPersonId({ student_id: studentId });
    }
  });

  studentClient.close();
  sleep(faker.numbers.float64Range(0.5, 1.5));
}
