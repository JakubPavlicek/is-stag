import { check } from 'k6';
import http, { Params } from 'k6/http';

import { BASE_URL, HEADERS, TEST_RUN_ID } from './common.ts';

type QueryValue = string | number | boolean | null | undefined;

interface GetRequestOptions {
  path: string;
  query?: Record<string, QueryValue>;
  tagName: string;
  expectedStatus?: number;
}

interface PostRequestOptions {
  path: string;
  body: Record<string, string | number>;
  tagName: string;
  expectedStatus?: number;
}

function buildQueryString(query: Record<string, QueryValue> = {}) {
  return Object.entries(query)
    .filter(([, value]) => value !== undefined && value !== null)
    .map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(String(value))}`)
    .join('&');
}

export function getRequest({ path, query = {}, tagName, expectedStatus = 200 }: GetRequestOptions) {
  const queryString = buildQueryString(query);
  const url = queryString ? `${BASE_URL}${path}?${queryString}` : `${BASE_URL}${path}`;
  const params: Params = {
    headers: HEADERS,
    tags: {
      name: tagName,
      testId: TEST_RUN_ID,
    },
  };

  const response = http.get(url, params);

  check(response, {
    [`${tagName}: status is ${expectedStatus}`]: (res) => res.status === expectedStatus,
  });

  return response;
}

export function postRequest({ path, body, tagName, expectedStatus = 204 }: PostRequestOptions) {
  const url = `${BASE_URL}${path}`;
  const params: Params = {
    headers: {
      ...HEADERS,
      'Content-Type': 'application/json',
    },
    tags: {
      name: tagName,
      testId: TEST_RUN_ID,
    },
  };

  const response = http.post(url, JSON.stringify(body), params);

  check(response, {
    [`${tagName}: status is ${expectedStatus}`]: (res) => res.status === expectedStatus,
  });

  return response;
}
