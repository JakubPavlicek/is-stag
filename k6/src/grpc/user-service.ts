import { group, sleep } from 'k6';
import { Options } from 'k6/options';
import faker from 'k6/x/faker';
import * as userClient from './user-client.ts';
import { getRandomLanguage, MAX_PERSON_ID, MIN_PERSON_ID } from '../shared/common.ts';

export const options: Options = {
  insecureSkipTLSVerify: true,
  thresholds: {
    grpc_req_duration: ['p(99) < 500'], // 99% of requests should be below 500ms
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
        { duration: '2m', target: 100 },
        { duration: '1m', target: 100 },
        { duration: '2m', target: 300 },
        { duration: '1m', target: 300 },
        { duration: '2m', target: 500 },
        { duration: '1m', target: 500 },
        { duration: '1m', target: 0 },
      ],
      exec: 'loadTest',
      startTime: '10s',
    },
  },
};

export function smokeTest() {
  userClient.connect();

  const personId = 13373;

  group('gRPC Smoke Test - PersonService', () => {
    const request = {
      person_id: personId,
      language: 'cs',
    };

    userClient.getPersonSimpleProfile(request);
  });

  userClient.close();
}

export function loadTest() {
  userClient.connect();

  const personId = faker.numbers.uintRange(MIN_PERSON_ID, MAX_PERSON_ID);

  group('gRPC Load Test - PersonService', () => {
    const request = {
      person_id: personId,
      language: getRandomLanguage(),
    };

    userClient.getPersonSimpleProfile(request);
  });

  userClient.close();
  sleep(faker.numbers.float64Range(0.5, 1.5));
}
