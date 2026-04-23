import faker from 'k6/x/faker';

import { BANK_CODE_VALUES } from '../data/bank-codes.ts';
import { STAT_IDNO_VALUES } from '../data/countries.ts';
import { STAV_VALUES } from '../data/marital-statuses.ts';
import { TITUL_PRED_VALUES, TITUL_ZA_VALUES } from '../data/titles.ts';
import { getRequest, postRequest } from './client.ts';
import { CommonQueryParams, getDefaultQueryParams } from './common.ts';

export interface GetOsobaParams extends CommonQueryParams {
  osCislo: string;
}

export interface UpdateOsobaBody {
  osobIdno: number;
  rodnePrijmeni: string;
  stav: string;
  email: string;
  telefon: string;
  mobil: string;
  titulPred: string;
  titulZa: string;
  mistoNar: string;
  statIdnoNaro: number;
  ucetPred: string;
  ucetZa: string;
  ucetBanka: string;
  ucetMajitel: string;
  ucetAdresa: string;
}

export function generateUpdateOsobaBody(osobIdno: number): UpdateOsobaBody {
  const firstName = faker.person.firstName();
  const lastName = faker.person.lastName();

  return {
    osobIdno,
    rodnePrijmeni: faker.person.lastName(),
    stav: faker.strings.randomString(STAV_VALUES),
    email: `${firstName}${faker.strings.digitN(8)}@gmail.com`,
    telefon: `+420${faker.strings.digitN(9)}`,
    mobil: `+420${faker.strings.digitN(9)}`,
    titulPred: faker.strings.randomString(TITUL_PRED_VALUES),
    titulZa: faker.strings.randomString(TITUL_ZA_VALUES),
    statIdnoNaro: faker.numbers.randomUint(STAT_IDNO_VALUES),
    mistoNar: faker.address.city(),
    ucetPred: '19',
    ucetZa: '9737112278',
    ucetBanka: faker.strings.randomString(BANK_CODE_VALUES),
    ucetMajitel: `${firstName} ${lastName}`,
    ucetAdresa: `${faker.address.street()} ${faker.numbers.uintRange(1, 999)}, ${faker.address.zip()} ${faker.address.city()}`,
  };
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

export function updateOsoba(body: UpdateOsobaBody) {
  return postRequest({
    path: '/users/updateOsoba',
    body: body as unknown as Record<string, string | number>,
    tagName: 'POST /users/updateOsoba',
  });
}
