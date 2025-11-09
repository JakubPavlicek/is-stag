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
    tags: { name: 'GetPersonProfile', testId: TEST_RUN_ID },
  });
  check(res, {
    'GetPersonProfile: status is 200': (r) => r.status === 200,
  });
}

export function getPersonAddresses(personId: number) {
  const res = http.get(`${BASE_URL}/api/v1/persons/${personId}/addresses`, {
    headers: HEADERS,
    tags: { name: 'GetPersonAddresses', testId: TEST_RUN_ID },
  });
  check(res, {
    'GetPersonAddresses: status is 200': (r) => r.status === 200,
  });
}

export function getPersonBanking(personId: number) {
  const res = http.get(`${BASE_URL}/api/v1/persons/${personId}/banking`, {
    headers: HEADERS,
    tags: { name: 'GetPersonBanking', testId: TEST_RUN_ID },
  });
  check(res, {
    'GetPersonBanking: status is 200': (r) => r.status === 200,
  });
}

export function getPersonEducation(personId: number) {
  const res = http.get(`${BASE_URL}/api/v1/persons/${personId}/education`, {
    headers: HEADERS,
    tags: { name: 'GetPersonEducation', testId: TEST_RUN_ID },
  });
  check(res, {
    'GetPersonEducation: status is 200': (r) => r.status === 200,
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
      tags: { name: 'UpdatePersonProfile', testId: TEST_RUN_ID },
    },
  );
  check(res, {
    'UpdatePersonProfile: status is 204': (r) => r.status === 204,
  });
}
