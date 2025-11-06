import http from 'k6/http';
import { Options } from 'k6/options';
import faker from 'k6/x/faker'
import { check, sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'https://is-stag.cz';

export const options: Options = {
  iterations: 200,
  vus: 10,
  insecureSkipTLSVerify: true,
  thresholds: {
    http_req_duration: ['p(95)<1000'],
  }
}

export default function test() {
  // const personId = faker.numbers.uintRange(1, 500000);
  const personId = 246950
  const res: http.Response = http.get(`${BASE_URL}/api/v1/persons/${personId}`);
  check(res, { 'status is 200': (response) => response.status === 200 });
  sleep(1);
}