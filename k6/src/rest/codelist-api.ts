import http from 'k6/http';
import { check } from 'k6';
import { BASE_URL, HEADERS, TEST_RUN_ID } from '../shared/common.ts';

export function getAddresses(query: string, limit: number) {
  const url = `${BASE_URL}/api/v1/addresses?query=${query}&limit=${limit}`;
  const params = {
    headers: HEADERS,
    tags: {
      name: 'GET /addresses',
      testId: TEST_RUN_ID,
    },
  };

  const res = http.get(url, params);

  check(res, {
    'GET /addresses: status is 200': (r) => r.status === 200,
  });
}

export function getCountries() {
  const url = `${BASE_URL}/api/v1/countries`;
  const params = {
    headers: HEADERS,
    tags: {
      name: 'GET /countries',
      testId: TEST_RUN_ID,
    },
  };

  const res = http.get(url, params);

  check(res, {
    'GET /countries: status is 200': (r) => r.status === 200,
  });
}

export function getDomains() {
  const url = `${BASE_URL}/api/v1/domains`;
  const params = {
    headers: HEADERS,
    tags: {
      name: 'GET /domains',
      testId: TEST_RUN_ID,
    },
  };

  const res = http.get(url, params);

  check(res, {
    'GET /domains: status is 200': (r) => r.status === 200,
  });

  return res.json() as { domains: string[] };
}

export function getDomainValues(domain: string) {
  const url = `${BASE_URL}/api/v1/domains/${domain}`;
  const params = {
    headers: HEADERS,
    tags: {
      name: 'GET /domains/{domain}',
      testId: TEST_RUN_ID,
    },
  };

  const res = http.get(url, params);

  check(res, {
    'GET /domains/{domain}: status is 200': (r) => r.status === 200,
  });
}
