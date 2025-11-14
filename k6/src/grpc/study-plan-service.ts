import { check, group, sleep } from 'k6';
import { Options } from 'k6/options';
import grpc from 'k6/net/grpc';
import faker from 'k6/x/faker';
import { GRPC_URL, PROTO_DIR } from '../shared/common.ts';

const client = new grpc.Client();
client.load(
  [PROTO_DIR],
  'stag/academics/studyplan/v1/study_plan_service.proto',
);

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

  group('gRPC Smoke Test - StudyPlanService', () => {
    const request = {
      study_program_id: 2709, // Guessed ID
      study_plan_id: 99329, // Guessed ID
      language: 'cs',
    };

    const response = client.invoke(
      'stag.academics.studyplan.v1.StudyPlanService/GetStudyProgramAndField',
      request,
    );

    check(response, {
      'GetStudyProgramAndField: status is OK': (r) =>
        r.status === grpc.StatusOK,
    });

    client.close();
  });
}

export function loadTest() {
  client.connect(GRPC_URL, { plaintext: true, reflect: true });

  group('gRPC Load Test - StudyPlanService', () => {
    const request = {
      study_program_id: faker.numbers.uintRange(2000, 3000),
      study_plan_id: faker.numbers.uintRange(90000, 100000),
      language: Math.random() < 0.5 ? 'cs' : 'en',
    };

    const response = client.invoke(
      'stag.academics.studyplan.v1.StudyPlanService/GetStudyProgramAndField',
      request,
    );

    check(response, {
      'GetStudyProgramAndField: status is OK': (r) =>
        r.status === grpc.StatusOK,
    });

    client.close();
  });

  sleep(faker.numbers.float64Range(0.5, 1.5));
}
