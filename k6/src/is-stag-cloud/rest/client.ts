import { check } from 'k6';
import http, { Params } from 'k6/http';

import { BASE_URL, HEADERS, TEST_RUN_ID } from '../shared/common.ts';

interface RequestOptions {
  path: string;
  tagName: string;
  expectedStatus?: number;
}

interface PatchRequestOptions extends RequestOptions {
  body: unknown;
}

export function getRequest({ path, tagName, expectedStatus = 200 }: RequestOptions) {
  const params: Params = {
    headers: HEADERS,
    tags: {
      name: tagName,
      testId: TEST_RUN_ID,
    },
  };
  const response = http.get(`${BASE_URL}${path}`, params);

  check(response, {
    [`${tagName}: status is ${expectedStatus}`]: (res) => res.status === expectedStatus,
  });

  return response;
}

export function patchRequest({ path, body, tagName, expectedStatus = 204 }: PatchRequestOptions) {
  const params: Params = {
    headers: HEADERS,
    tags: {
      name: tagName,
      testId: TEST_RUN_ID,
    },
  };
  const response = http.patch(`${BASE_URL}${path}`, JSON.stringify(body), params);

  check(response, {
    [`${tagName}: status is ${expectedStatus}`]: (res) => res.status === expectedStatus,
  });

  return response;
}
