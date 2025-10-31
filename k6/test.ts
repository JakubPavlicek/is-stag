import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  iterations: 30,
  insecureSkipTLSVerify: true,
  label: "test"
}

export default function test() {
  const res = http.get('https://is-stag.cz/api/swagger-ui.html');
  check(res, { 'status is 200': (response) => response.status === 200 });
  sleep(1);
}