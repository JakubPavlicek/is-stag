import { group, sleep } from 'k6';
import { Options } from 'k6/options';
import faker from 'k6/x/faker';
import * as studyPlanClient from './study-plan-client.ts';
import {
  getRandomLanguage,
  MAX_STUDY_PLAN_ID,
  MAX_STUDY_PROGRAM_ID,
  MIN_STUDY_PLAN_ID,
  MIN_STUDY_PROGRAM_ID,
} from '../shared/common.ts';

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
      maxDuration: '10s',
    },
    average_load: {
      executor: 'ramping-vus',
      stages: [
        { duration: '1m', target: 10 },
        { duration: '2m', target: 10 },
        { duration: '1m', target: 0 },
      ],
      exec: 'loadTest',
      startTime: '10s',
    },
  },
};

export function smokeTest() {
  studyPlanClient.connect();

  group('gRPC Smoke Test - StudyPlanService', () => {
    const request = {
      study_program_id: 223,
      study_plan_id: 10,
      language: 'cs',
    };

    studyPlanClient.getStudyProgramAndField(request);
  });

  studyPlanClient.close();
}

export function loadTest() {
  studyPlanClient.connect();

  group('gRPC Load Test - StudyPlanService', () => {
    const request = {
      study_program_id: faker.numbers.uintRange(MIN_STUDY_PROGRAM_ID, MAX_STUDY_PROGRAM_ID),
      study_plan_id: faker.numbers.uintRange(MIN_STUDY_PLAN_ID, MAX_STUDY_PLAN_ID),
      language: getRandomLanguage(),
    };

    studyPlanClient.getStudyProgramAndField(request);
  });

  studyPlanClient.close();
  sleep(faker.numbers.float64Range(0.5, 1.5));
}
