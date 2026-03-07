import { CommonQueryParams, getDefaultQueryParams } from '../shared/common.ts';
import { getRequest } from './client.ts';

export interface GetFieldOfStudyParams extends CommonQueryParams {
  oborIdno: number;
}

export function getFieldOfStudy({ oborIdno, ...params }: GetFieldOfStudyParams) {
  return getRequest({
    path: '/programy/getOborInfo',
    query: {
      oborIdno,
      ...getDefaultQueryParams(params),
    },
    tagName: 'GET /programy/getOborInfo',
  });
}
