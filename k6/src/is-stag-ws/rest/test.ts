import { group, sleep } from 'k6';
import { RefinedResponse } from 'k6/http';
import { Options } from 'k6/options';
import faker from 'k6/x/faker';

import { STUDENT_IDS } from '../../data/student-ids.ts';
import * as codelistApi from './codelist-api.ts';
import * as fieldOfStudyApi from './field-of-study-api.ts';
import * as studentApi from './student-api.ts';
import * as usersApi from './users-api.ts';

export const options: Options = {
  insecureSkipTLSVerify: true,
  thresholds: {
    http_req_duration: ['p(99) < 1500'],
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
        { duration: '1m', target: 10 },
        { duration: '2m', target: 25 },
        { duration: '1m', target: 25 },
        { duration: '1m', target: 0 },
      ],
      exec: 'loadTest',
      startTime: '5s',
    },
  },
};

export function smokeTest() {
  group('Smoke Test - IS/STAG REST WebServices', () => {
    const studentInfo = studentApi.getStudentInfo({ osCislo: STUDENT_IDS[0] });
    const person = usersApi.getOsoba({ osCislo: STUDENT_IDS[0] });

    // Fetch title prefix
    const titlePrefixId = person.json('titulPred') as string;
    codelistApi.getCiselnikNewItems({ domena: 'TITUL_PRED', key: titlePrefixId });

    // Fetch title suffix
    const titleSuffixId = person.json('titulZa') as string;
    codelistApi.getCiselnikNewItems({ domena: 'TITUL_ZA', key: titleSuffixId });

    // Fetch field of study
    const fieldOfStudyId = studentInfo.json('oborIdnos') as number;
    fieldOfStudyApi.getFieldOfStudy({ oborIdno: fieldOfStudyId });

    // Fetch marital status
    const maritualStatusId = person.json('stav') as string;
    codelistApi.getCiselnikNewItems({ domena: 'STAV', key: maritualStatusId });

    // Fetch citizenship qualifier
    const citizenshipQualifierId = person.json('stav') as string;
    codelistApi.getCiselnikNewItems({ domena: 'KVANT_OBCAN', key: citizenshipQualifierId });

    // Fetch bank name
    const bankNameId = person.json('ucetBanka') as string;
    codelistApi.getCiselnikNewItems({ domena: 'CIS_BANK', key: bankNameId });
  });
}

export function loadTest() {
  // Randomly select a student ID from the list.
  const studentId = STUDENT_IDS[faker.numbers.uintRange(0, STUDENT_IDS.length - 1)];

  // Placeholder for student, which will be fetched from the studentApi.
  let student: RefinedResponse<'binary' | 'none' | 'text'>;
  // Placeholder for person, which will be fetched from the usersApi.
  let person: RefinedResponse<'binary' | 'none' | 'text'>;

  group('IS/STAG Student Journey', () => {
    student = studentApi.getStudentInfo({ osCislo: studentId });
  });

  sleep(faker.numbers.float64Range(0.1, 1));

  group('IS/STAG Student Journey', () => {
    person = usersApi.getOsoba({ osCislo: studentId });
  });

  sleep(faker.numbers.float64Range(0.1, 1));

  group('IS/STAG Codelist Journey', () => {
    // Fetch title prefix
    const titlePrefixId = person.json('titulPred') as string;
    codelistApi.getCiselnikNewItems({ domena: 'TITUL_PRED', key: titlePrefixId });

    sleep(faker.numbers.float64Range(0.1, 1));

    // Fetch title suffix
    const titleSuffixId = person.json('titulZa') as string;
    codelistApi.getCiselnikNewItems({ domena: 'TITUL_ZA', key: titleSuffixId });

    sleep(faker.numbers.float64Range(0.1, 1));

    // Fetch field of study
    const fieldOfStudyId = student.json('oborIdnos') as number;
    fieldOfStudyApi.getFieldOfStudy({ oborIdno: fieldOfStudyId });

    sleep(faker.numbers.float64Range(0.1, 1));

    // Fetch marital status
    const maritualStatusId = person.json('stav') as string;
    codelistApi.getCiselnikNewItems({ domena: 'STAV', key: maritualStatusId });

    sleep(faker.numbers.float64Range(0.1, 1));

    // Fetch citizenship qualifier
    const citizenshipQualifierId = person.json('stav') as string;
    codelistApi.getCiselnikNewItems({ domena: 'KVANT_OBCAN', key: citizenshipQualifierId });

    sleep(faker.numbers.float64Range(0.1, 1));

    // Fetch bank name
    const bankNameId = person.json('ucetBanka') as string;
    codelistApi.getCiselnikNewItems({ domena: 'CIS_BANK', key: bankNameId });
  });

  sleep(faker.numbers.float64Range(0.1, 1));
}
