import http from 'k6/http';
import { check } from 'k6';
import { UpdatePersonRequest } from './common.ts';

const BASE_URL = __ENV.BASE_URL || 'https://is-stag.cz';
const TEST_RUN_ID = __ENV.K6_TEST_RUN_ID || '1';

const HEADERS = {
  'Accept-Language': 'cs',
  'Content-Type': 'application/json',
};

export function getPersonProfile(personId: number) {
  const res = http.get(`${BASE_URL}/api/v1/persons/${personId}`, {
    headers: HEADERS,
    tags: { name: 'GET /persons/{personId}', testId: TEST_RUN_ID },
  });
  check(res, {
    'GET /persons/{personId}: status is 200': (r) => r.status === 200,
  });
}

export function getPersonAddresses(personId: number) {
  const res = http.get(`${BASE_URL}/api/v1/persons/${personId}/addresses`, {
    headers: HEADERS,
    tags: { name: 'GET /persons/{personId}/addresses', testId: TEST_RUN_ID },
  });
  check(res, {
    'GET /persons/{personId}/addresses: status is 200': (r) => r.status === 200,
  });
}

export function getPersonBanking(personId: number) {
  const res = http.get(`${BASE_URL}/api/v1/persons/${personId}/banking`, {
    headers: HEADERS,
    tags: { name: 'GET /persons/{personId}/banking', testId: TEST_RUN_ID },
  });
  check(res, {
    'GET /persons/{personId}/banking: status is 200': (r) => r.status === 200,
  });
}

export function getPersonEducation(personId: number) {
  const res = http.get(`${BASE_URL}/api/v1/persons/${personId}/education`, {
    headers: HEADERS,
    tags: { name: 'GET /persons/{personId}/education', testId: TEST_RUN_ID },
  });
  check(res, {
    'GET /persons/{personId}/education: status is 200': (r) => r.status === 200,
  });
}

export function updatePersonProfile(
  personId: number,
  payload: UpdatePersonRequest,
) {
  const res = http.patch(
    `${BASE_URL}/api/v1/persons/${personId}`,
    JSON.stringify(payload),
    {
      headers: HEADERS,
      tags: { name: 'PATCH /persons/{personId}', testId: TEST_RUN_ID },
    },
  );
  check(res, {
    'PATCH /persons/{personId}: status is 204': (r) => r.status === 204,
  });
}
