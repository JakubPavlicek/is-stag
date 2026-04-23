import { getRequest } from './client.ts';
import { CommonQueryParams, getDefaultQueryParams } from './common.ts';

export interface GetStudentInfoParams extends CommonQueryParams {
  osCislo: string;
}

export function getStudentInfo({ osCislo, ...params }: GetStudentInfoParams) {
  return getRequest({
    path: '/student/getStudentInfo',
    query: {
      osCislo,
      ...getDefaultQueryParams(params),
    },
    tagName: 'GET /student/getStudentInfo',
  });
}
