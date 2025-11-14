import { group, sleep } from 'k6';
import { Options } from 'k6/options';
import faker from 'k6/x/faker';
import * as api from './codelist-api.ts';
import { CODELIST_DOMAINS } from '../shared/common.ts';

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
        { duration: '2m', target: 20 },
        { duration: '5m', target: 20 },
        { duration: '1m', target: 0 },
      ],
      exec: 'browserScenario',
      startTime: '10s', // Start after the smoke test
    },
  },
};

// --- Test Scenario Functions ---

export function smokeTest() {
  group('Smoke Test - Codelist Service', () => {
    api.getAddresses('plzen', 5);
    api.getCountries();
    const domains = api.getDomains();
    if (domains && domains.domains.length > 0) {
      api.getDomainValues(domains.domains[0]);
    }
    api.getDomainValues(CODELIST_DOMAINS[0]);
  });
}

export function browserScenario() {
  group('Codelist Browsing Journey', () => {
    // User searches for an address
    api.getAddresses(faker.address.city(), faker.numbers.uintRange(3, 10));
    sleep(faker.numbers.float64Range(0.2, 1));

    // User looks up countries
    api.getCountries();
    sleep(faker.numbers.float64Range(0.2, 1));

    // User browses a random codelist domain
    const domain =
      CODELIST_DOMAINS[faker.numbers.uintRange(0, CODELIST_DOMAINS.length - 1)];
    api.getDomainValues(domain);
  });

  sleep(faker.numbers.float64Range(1, 2));
}
