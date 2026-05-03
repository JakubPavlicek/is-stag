import defaultFaker, { Faker } from 'k6/x/faker';

import { BANK_CODE_VALUES } from '../data/bank-codes.ts';
import { COUNTRY_VALUES } from '../data/countries.ts';
import { MARITAL_STATUS_VALUES } from '../data/marital-statuses.ts';
import { TITLE_PREFIX_VALUES, TITLE_SUFFIX_VALUES } from '../data/titles.ts';
import { getRequest, patchRequest } from './client.ts';

interface ContactUpdate {
  email?: string;
  phone?: string;
  mobile?: string;
  dataBox?: string;
}

interface TitlesUpdate {
  prefix?: string;
  suffix?: string;
}

interface BirthPlaceUpdate {
  country?: string;
  city?: string;
}

interface BankAccountUpdate {
  accountNumberPrefix?: string;
  accountNumberSuffix?: string;
  bankCode?: string;
  holderName?: string;
  holderAddress?: string;
}

export interface UpdatePersonRequest {
  birthSurname?: string;
  maritalStatus?: string;
  contact?: ContactUpdate;
  titles?: TitlesUpdate;
  birthPlace?: BirthPlaceUpdate;
  bankAccount?: BankAccountUpdate;
}

export function getPersonProfile(personId: number) {
  return getRequest({
    path: `/persons/${personId}`,
    tagName: 'GET /persons/{personId}',
  });
}

export function getPersonAddresses(personId: number) {
  return getRequest({
    path: `/persons/${personId}/addresses`,
    tagName: 'GET /persons/{personId}/addresses',
  });
}

export function getPersonBanking(personId: number) {
  return getRequest({
    path: `/persons/${personId}/banking`,
    tagName: 'GET /persons/{personId}/banking',
  });
}

export function getPersonEducation(personId: number) {
  return getRequest({
    path: `/persons/${personId}/education`,
    tagName: 'GET /persons/{personId}/education',
  });
}

export function updatePersonProfile(personId: number, payload: UpdatePersonRequest) {
  return patchRequest({
    path: `/persons/${personId}`,
    body: payload,
    tagName: 'PATCH /persons/{personId}',
    expectedStatus: 204,
  });
}

export function generateUpdatePersonBody(faker: Faker = defaultFaker): UpdatePersonRequest {
  const firstName = faker.person.firstName();
  const lastName = faker.person.lastName();

  return {
    birthSurname: faker.person.lastName(),
    maritalStatus: faker.strings.randomString(MARITAL_STATUS_VALUES),
    contact: {
      email: `${firstName}${faker.strings.digitN(8)}@gmail.com`,
      phone: `+420${faker.strings.digitN(9)}`,
      mobile: `+420${faker.strings.digitN(9)}`,
      dataBox: '9q74xgu',
    },
    titles: {
      prefix: faker.strings.randomString(TITLE_PREFIX_VALUES),
      suffix: faker.strings.randomString(TITLE_SUFFIX_VALUES),
    },
    birthPlace: {
      country: faker.strings.randomString(COUNTRY_VALUES),
      city: faker.address.city(),
    },
    bankAccount: {
      accountNumberPrefix: '19',
      accountNumberSuffix: '9737112278',
      bankCode: faker.strings.randomString(BANK_CODE_VALUES),
      holderName: `${firstName} ${lastName}`,
      holderAddress: `${faker.address.street()} ${faker.numbers.uintRange(1, 999)}, ${faker.address.zip()} ${faker.address.city()}`,
    },
  };
}
