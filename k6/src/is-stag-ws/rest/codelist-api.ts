import { CommonQueryParams, getDefaultQueryParams } from '../shared/common.ts';
import { getRequest } from './client.ts';

export interface GetCiselnikParams extends CommonQueryParams {
  domena: string;
}

export interface GetCiselnikNewItemsParams extends CommonQueryParams {
  domena: string;
  key: string | number;
}

export function getCiselnik({ domena, ...params }: GetCiselnikParams) {
  return getRequest({
    path: '/ciselniky/getCiselnik',
    query: {
      domena,
      ...getDefaultQueryParams(params),
    },
    tagName: 'GET /ciselniky/getCiselnik',
  });
}

export function getCiselnikNewDomains(params: CommonQueryParams = {}) {
  return getRequest({
    path: '/ciselniky/getCiselnikNewDomains',
    query: getDefaultQueryParams(params),
    tagName: 'GET /ciselniky/getCiselnikNewDomains',
  });
}

export function getCiselnikNewItems({ domena, key, ...params }: GetCiselnikNewItemsParams) {
  return getRequest({
    path: '/ciselniky/getCiselnikNewItems',
    query: {
      domena,
      key,
      ...getDefaultQueryParams(params),
    },
    tagName: 'GET /ciselniky/getCiselnikNewItems',
  });
}
