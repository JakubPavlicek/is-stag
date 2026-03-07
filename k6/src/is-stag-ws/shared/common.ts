export const TEST_RUN_ID = __ENV.K6_TEST_RUN_ID || '1';
export const IS_STAG_BASE_URL = __ENV.BASE_URL || 'https://stag-demo.zcu.cz/ws/services/rest2';
export const IS_STAG_COOKIE = __ENV.WSCOOKIE || '8240b2482c79e3c578a61d45912bd360315d8ed7875a4a9e19eaa52ba688f141';

export const HEADERS = {
  Accept: 'application/json',
  Cookie: `WSCOOKIE=${IS_STAG_COOKIE}`,
};

export interface CommonQueryParams {
  lang?: string;
  outputFormat?: string;
}

export function getDefaultQueryParams({ lang = 'cs', outputFormat = 'JSON' }: CommonQueryParams) {
  return { lang, outputFormat };
}
