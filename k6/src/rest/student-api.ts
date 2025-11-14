import { check } from 'k6';
import http from 'k6/http';
import { BASE_URL, HEADERS, TEST_RUN_ID } from '../shared/common.ts';

export function getStudentProfile(studentId: string) {
  const url = `${BASE_URL}/api/v1/students/${studentId}`;
  const params = {
    headers: HEADERS,
    tags: {
      name: 'GET /students/{studentId}',
      testId: TEST_RUN_ID,
    },
  };

  const res = http.get(url, params);

  check(res, {
    'getStudentProfile: status is 200': (r) => r.status === 200,
  });
}
