import { CommonQueryParams, getDefaultQueryParams } from '../shared/common.ts';
import { getRequest } from './client.ts';

export interface GetOsobaParams extends CommonQueryParams {
  osCislo: string;
}

export function getOsoba({ osCislo, ...params }: GetOsobaParams) {
  return getRequest({
    path: '/users/getOsoba',
    query: {
      osCislo,
      ...getDefaultQueryParams(params),
    },
    tagName: 'GET /users/getOsoba',
  });
}
