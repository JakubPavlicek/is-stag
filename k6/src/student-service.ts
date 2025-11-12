import { group, sleep } from 'k6';
import { Options } from 'k6/options';
import faker from 'k6/x/faker';
import * as api from './student-api.ts';
import { STUDENT_IDS } from './common.ts';

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
  },
};

// --- Test Scenario Functions ---

export function smokeTest() {
  // Use a known valid student ID for the smoke test.
  const studentId = STUDENT_IDS[0];

  group('Smoke Test - Student Service', () => {
    api.getStudentProfile(studentId);
  });
}

export function browserScenario() {
  // Select a random student ID from the list.
  const studentId =
    STUDENT_IDS[faker.numbers.uintRange(0, STUDENT_IDS.length - 1)];

  group('Student Browsing Journey', () => {
    api.getStudentProfile(studentId);
  });

  sleep(faker.numbers.float64Range(0.5, 1));
}
