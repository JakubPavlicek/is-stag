import { check, group, sleep } from 'k6';
import { Options } from 'k6/options';
import grpc from 'k6/net/grpc';
import faker from 'k6/x/faker';
import {
  GRPC_URL,
  PROTO_DIR,
  MAX_PERSON_ID,
  MIN_PERSON_ID,
} from '../shared/common.ts';

const client = new grpc.Client();
// client.load([PROTO_DIR], 'stag/identity/person/v1/person_service.proto');

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

  const personId = 13373; // Use a known valid ID for smoke test

  group('gRPC Smoke Test - PersonService', () => {
    const request = {
      person_id: personId,
      language: 'cs',
    };

    const response = client.invoke(
      'stag.identity.person.v1.PersonService/GetPersonSimpleProfile',
      request,
    );

    check(response, {
      'GetPersonSimpleProfile: status is OK': (r) => r.status === grpc.StatusOK,
    });

    client.close();
  });
}

export function loadTest() {
  client.connect(GRPC_URL, { plaintext: true, reflect: true });

  const personId = faker.numbers.uintRange(MIN_PERSON_ID, MAX_PERSON_ID);

  group('gRPC Load Test - PersonService', () => {
    const request = {
      person_id: personId,
      language: Math.random() < 0.5 ? 'cs' : 'en',
    };

    const response = client.invoke(
      'stag.identity.person.v1.PersonService/GetPersonSimpleProfile',
      request,
    );

    check(response, {
      'GetPersonSimpleProfile: status is OK': (r) => r.status === grpc.StatusOK,
    });

    client.close();
  });

  sleep(faker.numbers.float64Range(0.5, 1.5));
}
