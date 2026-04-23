import { getRequest } from './client.ts';

export function getCountries() {
  return getRequest({
    path: '/countries',
    tagName: 'GET /countries',
  });
}

export function getDomains() {
  const res = getRequest({
    path: '/domains',
    tagName: 'GET /domains',
  });

  return res.json() as { domains: string[] };
}

export function getDomainValues(domain: string) {
  return getRequest({
    path: `/domains/${encodeURIComponent(domain)}`,
    tagName: 'GET /domains/{domain}',
  });
}
