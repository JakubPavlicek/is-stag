import { group, sleep } from 'k6';
import { Options } from 'k6/options';
import faker from 'k6/x/faker';
import * as api from './user-api.ts';
import {
  generateUpdatePersonPayload,
  MAX_PERSON_ID,
  MIN_PERSON_ID,
} from '../shared/common.ts';

export const options: Options = {
  insecureSkipTLSVerify: true,
  thresholds: {
    http_req_duration: ['p(99) < 500'], // 99% of requests should be below 500ms
    'http_req_failed{scenario:smoke}': ['rate == 0'], // Fail smoke test if any HTTP request fails
  },
  scenarios: {
    // Smoke Test: A quick check to ensure all endpoints are functional.
    smoke: {
      executor: 'shared-iterations',
      vus: 1,
      iterations: 1,
      exec: 'smokeTest',
      startTime: '0s',
      maxDuration: '10s',
    },
    // Average Load Test: Simulates a typical day's load.
    // 90% of users are browsing, 10% are editing.
    average_load: {
      executor: 'ramping-vus',
      stages: [
        { duration: '2m', target: 50 },
        { duration: '5m', target: 50 },
        { duration: '1m', target: 0 },
      ],
      exec: 'browserScenario',
      startTime: '10s', // Start after the smoke test
    },
    // Run the editor scenario separately because k6 doesn't directly support weighted scenarios in the same executor.
    editor_load: {
      executor: 'ramping-vus',
      stages: [
        { duration: '2m', target: 5 },
        { duration: '5m', target: 5 },
        { duration: '1m', target: 0 },
      ],
      exec: 'editorScenario',
      startTime: '10s', // Start alongside the main load test
    },
    // Stress Test: Find the system's breaking point.
    stress: {
      executor: 'ramping-vus',
      stages: [
        { duration: '2m', target: 100 },
        { duration: '2m', target: 200 },
        { duration: '2m', target: 300 },
        { duration: '2m', target: 400 },
        { duration: '2m', target: 500 },
        { duration: '2m', target: 0 },
      ],
      exec: 'browserScenario',
      startTime: '9m', // Start after the Average Load test
    },
  },
};

// --- Test Scenario Functions ---

export function smokeTest() {
  // const personId = faker.numbers.uintRange(MIN_PERSON_ID, MAX_PERSON_ID);
  const personId = 13373;

  group('Smoke Test - User Service', () => {
    api.getPersonProfile(personId);
    api.getPersonAddresses(personId);
    api.getPersonBanking(personId);
    api.getPersonEducation(personId);

    const payload = generateUpdatePersonPayload();
    api.updatePersonProfile(personId, payload);
  });
}

export function browserScenario() {
  const personId = faker.numbers.uintRange(MIN_PERSON_ID, MAX_PERSON_ID);

  group('User Browsing Journey', () => {
    api.getPersonProfile(personId);
    sleep(faker.numbers.float64Range(0.2, 1));

    api.getPersonAddresses(personId);
    sleep(faker.numbers.float64Range(0.2, 1));

    api.getPersonBanking(personId);
    sleep(faker.numbers.float64Range(0.2, 1));

    api.getPersonEducation(personId);
  });

  sleep(faker.numbers.float64Range(1, 2));
}

export function editorScenario() {
  const personId = faker.numbers.uintRange(MIN_PERSON_ID, MAX_PERSON_ID);
  const payload = generateUpdatePersonPayload();

  group('User Editing Profile', () => {
    api.updatePersonProfile(personId, payload);
  });

  sleep(faker.numbers.float64Range(2, 5));
}
