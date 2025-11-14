import { check, group, sleep } from 'k6';
import { Options } from 'k6/options';
import grpc from 'k6/net/grpc';
import faker from 'k6/x/faker';
import { GRPC_URL, PROTO_DIR, CODELIST_DOMAINS } from '../shared/common.ts';

const client = new grpc.Client();
client.load([PROTO_DIR], 'stag/platform/codelist/v1/codelist_service.proto');

export const options: Options = {
  insecureSkipTLSVerify: true,
  thresholds: {
    grpc_req_duration: ['p(99) < 500'],
    'grpc_req_failed{scenario:smoke}': ['rate == 0'],
  },
  scenarios: {
    smoke: {
      executor: 'shared-iterations',
      vus: 1,
      iterations: 1,
      exec: 'smokeTest',
      startTime: '0s',
      maxDuration: '20s',
    },
    average_load: {
      executor: 'ramping-vus',
      stages: [
        { duration: '1m', target: 10 },
        { duration: '2m', target: 10 },
        { duration: '1m', target: 0 },
      ],
      exec: 'loadTest',
      startTime: '20s',
    },
  },
};

function getRandomCodelistKeys(count: number) {
  const keys = [];
  for (let i = 0; i < count; i++) {
    keys.push({
      domain:
        CODELIST_DOMAINS[
          faker.numbers.uintRange(0, CODELIST_DOMAINS.length - 1)
        ],
      // This is a guess, might not exist, but good for load
      low_value: faker.word.noun(),
    });
  }
  return keys;
}

export function smokeTest() {
  client.connect(GRPC_URL, { plaintext: true, reflect: true });

  group('gRPC Smoke Test - CodelistService', () => {
    // GetCodelistValues
    let res = client.invoke(
      'stag.platform.codelist.v1.CodelistService/GetCodelistValues',
      {
        codelist_keys: [{ domain: 'FAKULTA', low_value: 'FAV' }],
        language: 'cs',
      },
    );
    check(res, {
      'GetCodelistValues: status is OK': (r) => r.status === grpc.StatusOK,
    });

    // GetPersonProfileData
    res = client.invoke(
      'stag.platform.codelist.v1.CodelistService/GetPersonProfileData',
      {
        codelist_keys: [{ domain: 'TITUL_PRED', low_value: 'BC' }],
        birth_country_id: 203, // Czech Republic
        language: 'cs',
      },
    );
    check(res, {
      'GetPersonProfileData: status is OK': (r) => r.status === grpc.StatusOK,
    });

    client.close();
  });
}

export function loadTest() {
  client.connect(GRPC_URL, { plaintext: true, reflect: true });

  group('gRPC Load Test - CodelistService', () => {
    const request = {
      codelist_keys: getRandomCodelistKeys(faker.numbers.uintRange(1, 5)),
      language: Math.random() < 0.5 ? 'cs' : 'en',
    };

    const response = client.invoke(
      'stag.platform.codelist.v1.CodelistService/GetCodelistValues',
      request,
    );

    check(response, {
      'GetCodelistValues: status is OK': (r) => r.status === grpc.StatusOK,
    });

    client.close();
  });

  sleep(faker.numbers.float64Range(0.5, 1.5));
}
