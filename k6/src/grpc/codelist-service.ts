import { group, sleep } from 'k6';
import { Options } from 'k6/options';
import faker from 'k6/x/faker';
import * as codelistClient from './codelist-client.ts';
import { getRandomLanguage } from '../shared/common.ts';
import { CODELIST_VALUES } from '../../data/codelist-values.ts';
import { CODELIST_DOMAINS } from '../../data/codelist-domains.ts';

export const options: Options = {
  insecureSkipTLSVerify: true,
  thresholds: {
    grpc_req_duration: ['p(99) < 500'],
    'http_req_failed{scenario:smoke}': ['rate == 0'],
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
        { duration: '2m', target: 100 },
        { duration: '1m', target: 100 },
        { duration: '2m', target: 300 },
        { duration: '1m', target: 300 },
        { duration: '2m', target: 500 },
        { duration: '1m', target: 500 },
        { duration: '1m', target: 0 },
      ],
      exec: 'loadTest',
      startTime: '20s',
    },
  },
};

export function smokeTest() {
  codelistClient.connect();

  group('gRPC Smoke Test - CodelistService', () => {
    const codelistRequest = {
      codelist_keys: [{ domain: 'FAKULTA', low_value: 'FAV' }],
      language: 'cs',
    };

    codelistClient.getCodelistValues(codelistRequest);

    const personProfileRequest = {
      codelist_keys: [{ domain: 'TITUL_PRED', low_value: '1' }],
      birth_country_id: 203,
      language: 'cs',
    };

    codelistClient.getPersonProfileData(personProfileRequest);

    const personAddressRequest = {
      permanent_municipality_part_id: 414727,
      permanent_country_id: 203,
      language: 'cs',
    };

    codelistClient.getPersonAddressData(personAddressRequest);

    const personBankingRequest = {
      codelist_keys: [{ domain: 'CIS_BANK', low_value: '0800' }],
      euro_account_country_id: 203,
      language: 'cs',
    };

    codelistClient.getPersonBankingData(personBankingRequest);

    const personEducationRequest = {
      high_school_id: '000082163',
      high_school_country_id: 203,
      language: 'cs',
    };

    codelistClient.getPersonEducationData(personEducationRequest);
  });

  codelistClient.close();
}

export function loadTest() {
  codelistClient.connect();

  group('gRPC Load Test - CodelistService', () => {
    const request = {
      codelist_keys: getRandomCodelistKeys(faker.numbers.uintRange(1, 2)),
      language: getRandomLanguage(),
    };

    codelistClient.getCodelistValues(request);
  });

  codelistClient.close();
  sleep(faker.numbers.float64Range(0.5, 1.5));
}

function getRandomCodelistKeys(count: number) {
  const keys = [];
  for (let i = 0; i < count; i++) {
    keys.push({
      domain: CODELIST_DOMAINS[faker.numbers.uintRange(0, CODELIST_DOMAINS.length - 1)],
      low_value: CODELIST_VALUES[faker.numbers.uintRange(0, CODELIST_VALUES.length - 1)],
    });
  }
  return keys;
}
