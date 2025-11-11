import { group, sleep } from 'k6';
import { Options } from 'k6/options';
import faker from 'k6/x/faker';
import * as api from './user-api.ts';
import { generateUpdatePersonPayload } from './common.ts';

// The number of students is around 10,000. We test it with a slightly larger range
// to include some "not found" cases, which is a realistic scenario.
const MIN_PERSON_ID = 100;
const MAX_PERSON_ID = 235000;

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
        { duration: '2m', target: 50 }, // Ramp up to 50 VUs over 2 minutes
        { duration: '5m', target: 50 }, // Stay at 50 VUs for 5 minutes
        { duration: '1m', target: 0 }, // Ramp down
      ],
      exec: 'browserScenario',
      startTime: '10s', // Start after the smoke test
    },
    // This is a placeholder for the editor scenario, running with fewer VUs.
    // k6 doesn't directly support weighted scenarios in the same executor,
    // so we run them as separate scenarios.
    editor_load: {
      executor: 'ramping-vus',
      stages: [
        { duration: '2m', target: 5 }, // 10% of the browser VUs
        { duration: '5m', target: 5 },
        { duration: '1m', target: 0 },
      ],
      exec: 'editorScenario',
      startTime: '10s', // Start alongside the main load test
    },
    // // 3. Stress Test: Find the system's breaking point.
    // // To run this, uncomment it and comment out the 'average_load' and 'editor_load' scenarios.
    // stress: {
    //   executor: 'ramping-vus',
    //   stages: [
    //     { duration: '2m', target: 100 },
    //     { duration: '2m', target: 200 },
    //     { duration: '2m', target: 300 },
    //     { duration: '2m', target: 400 },
    //     { duration: '2m', target: 0 },
    //   ],
    //   exec: 'browserScenario',
    //   startTime: '10s',
    // },
  },
};

// --- Test Scenario Functions ---

export function smokeTest() {
  // const personId = faker.numbers.uintRange(MIN_PERSON_ID, MAX_PERSON_ID);
  const personId = 13373;

  group('Smoke Test - All Endpoints', () => {
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
