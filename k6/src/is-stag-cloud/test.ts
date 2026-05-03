import { group, sleep } from 'k6';
import exec from 'k6/execution';
import { Options } from 'k6/options';
import { Faker } from 'k6/x/faker';

import { STUDENT_IDS } from '../data/student-ids.ts';
import * as studentApi from './student-api.ts';
import * as userApi from './user-api.ts';

const BASE_FAKER_SEED = Number(__ENV.XK6_FAKER_SEED ?? 11);
const LOAD_TEST_SCENARIO_OFFSET = 100_000;

export const options: Options = {
  insecureSkipTLSVerify: true,
  thresholds: {
    http_req_duration: ['p(99) < 500'],
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
      startVUs: 0,
      stages: [{ duration: '20m', target: 800 }],
      exec: 'loadTest',
      startTime: '5s',
      gracefulRampDown: '0s',
    },
  },
};

function createFaker(scenarioOffset = 0) {
  return new Faker(BASE_FAKER_SEED + scenarioOffset + exec.scenario.iterationInTest);
}

export function smokeTest() {
  group('Cloud - Smoke Test', () => {
    const fake = createFaker();
    const studentProfile = studentApi.getStudentProfile(STUDENT_IDS[0]);
    const personId = studentProfile?.json('personId') as number;

    userApi.getPersonProfile(personId);
    userApi.getPersonAddresses(personId);
    userApi.getPersonBanking(personId);
    userApi.getPersonEducation(personId);

    const updateBody = userApi.generateUpdatePersonBody(fake);
    userApi.updatePersonProfile(personId, updateBody);
  });
}

export function loadTest() {
  const faker = createFaker(LOAD_TEST_SCENARIO_OFFSET);
  // Randomly select a student ID from the list.
  const studentId = STUDENT_IDS[faker.numbers.uintRange(0, STUDENT_IDS.length - 1)];
  // Placeholder for person ID, which will be fetched from the student profile.
  let personId: number;

  // Fetch the student profile to get the person ID.
  group('Cloud - Student', () => {
    const studentProfile = studentApi.getStudentProfile(studentId);
    personId = studentProfile?.json('personId') as number;
  });

  sleep(faker.numbers.float64Range(0.1, 1));

  // Fetch user-related information using the person ID obtained from the student profile.
  group('Cloud - User', () => {
    userApi.getPersonProfile(personId);
    sleep(faker.numbers.float64Range(0.1, 1));

    userApi.getPersonAddresses(personId);
    sleep(faker.numbers.float64Range(0.1, 1));

    userApi.getPersonBanking(personId);
    sleep(faker.numbers.float64Range(0.1, 1));

    userApi.getPersonEducation(personId);
  });

  sleep(faker.numbers.float64Range(0.1, 1));

  // Update person profile with randomly generated data.
  group('Cloud - Update Person', () => {
    const updateBody = userApi.generateUpdatePersonBody(faker);
    userApi.updatePersonProfile(personId, updateBody);
  });

  sleep(faker.numbers.float64Range(0.1, 1));
}
