import http from 'k6/http';
import { check } from 'k6';
import {
  BASE_URL,
  HEADERS,
  TEST_RUN_ID,
  UpdatePersonRequest,
} from './common.ts';

export function getPersonProfile(personId: number) {
  const url = `${BASE_URL}/api/v1/persons/${personId}`;
  const params = {
    headers: HEADERS,
    tags: {
      name: 'GET /persons/{personId}',
      testId: TEST_RUN_ID,
    },
  };

  const res = http.get(url, params);

  check(res, {
    'GET /persons/{personId}: status is 200': (r) => r.status === 200,
  });
}

export function getPersonAddresses(personId: number) {
  const url = `${BASE_URL}/api/v1/persons/${personId}/addresses`;
  const params = {
    headers: HEADERS,
    tags: {
      name: 'GET /persons/{personId}/addresses',
      testId: TEST_RUN_ID,
    },
  };

  const res = http.get(url, params);

  check(res, {
    'GET /persons/{personId}/addresses: status is 200': (r) => r.status === 200,
  });
}

export function getPersonBanking(personId: number) {
  const url = `${BASE_URL}/api/v1/persons/${personId}/banking`;
  const params = {
    headers: HEADERS,
    tags: {
      name: 'GET /persons/{personId}/banking',
      testId: TEST_RUN_ID,
    },
  };

  const res = http.get(url, params);

  check(res, {
    'GET /persons/{personId}/banking: status is 200': (r) => r.status === 200,
  });
}

export function getPersonEducation(personId: number) {
  const url = `${BASE_URL}/api/v1/persons/${personId}/education`;
  const params = {
    headers: HEADERS,
    tags: {
      name: 'GET /persons/{personId}/education',
      testId: TEST_RUN_ID,
    },
  };

  const res = http.get(url, params);

  check(res, {
    'GET /persons/{personId}/education: status is 200': (r) => r.status === 200,
  });
}

export function updatePersonProfile(
  personId: number,
  payload: UpdatePersonRequest,
) {
  const url = `${BASE_URL}/api/v1/persons/${personId}`;
  const body = JSON.stringify(payload);
  const params = {
    headers: HEADERS,
    tags: {
      name: 'PATCH /persons/{personId}',
      testId: TEST_RUN_ID,
    },
  };

  const res = http.patch(url, body, params);

  check(res, {
    'PATCH /persons/{personId}: status is 204': (r) => r.status === 204,
  });
}
