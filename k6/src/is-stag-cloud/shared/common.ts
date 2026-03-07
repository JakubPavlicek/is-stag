export const TEST_RUN_ID = __ENV.K6_TEST_RUN_ID || '1';
export const BASE_URL = __ENV.BASE_URL || 'https://is-stag.cz/api/v1';

export const HEADERS = {
  'Accept-Language': 'cs',
  'Content-Type': 'application/json',
};
