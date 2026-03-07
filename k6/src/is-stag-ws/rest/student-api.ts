import { CommonQueryParams, getDefaultQueryParams } from '../shared/common.ts';
import { getRequest } from './client.ts';

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
